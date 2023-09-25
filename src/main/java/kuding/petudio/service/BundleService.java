package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.lowservice.PairPictureAndPictureServiceDto;
import kuding.petudio.service.lowservice.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BundleService {

    private final BundleRepository bundleRepository;
    private final AmazonService amazonService;

    @Autowired
    public BundleService(BundleRepository bundleRepository, AmazonService amazonService) {
        this.bundleRepository = bundleRepository;
        this.amazonService = amazonService;
    }

    /**
     *
     * @param pageOffset
     * @param pageSize
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public List<ServiceReturnBundleDto> findBundlesOrderByCreatedDate(int pageOffset, int pageSize) throws IOException {
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bundle> bundlePage = bundleRepository.findAll(pageRequest);
        List<Bundle> bundles = bundlePage.getContent();
        List<ServiceReturnBundleDto> serviceReturnBundleDtoList = amazonService.getAllPicturesInAllBundles(bundles);
        return serviceReturnBundleDtoList;
    }

    /**
     * 넘겨받은 pictures를 묶어서 하나의 bundle로 DB에 저장
     * pictures를 S3에 저장
     * @param serviceParamPictureDtos
     * @param bundleType
     * @return bundle_id
     */
    @Transactional
    public Long saveBundleBindingPictures(List<ServiceParamPictureDto> serviceParamPictureDtos, BundleType bundleType) throws IOException {
        Bundle bundle = new Bundle(bundleType);
        List<PairPictureAndPictureServiceDto> pairs = new ArrayList<>();

        //DB에 저장
        for (ServiceParamPictureDto serviceParamPictureDto : serviceParamPictureDtos) {
            String storedName = getStoredName(serviceParamPictureDto.getOriginalName());
            Picture picture = new Picture(serviceParamPictureDto.getOriginalName(), storedName, serviceParamPictureDto.getPictureType());
            pairs.add(new PairPictureAndPictureServiceDto(picture, serviceParamPictureDto));
            bundle.addPicture(picture);
        }
        Bundle saveBundle = bundleRepository.save(bundle);

        //s3에 저장
        amazonService.savePicturesToS3(pairs);

        return saveBundle.getId();
    }

    private String getStoredName(String originalName) {
        String uuid = UUID.randomUUID().toString();
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            return uuid;
        }
        String ext = originalName.substring(pos + 1);
        String storedName = uuid + "." + ext;
        return storedName;
    }

}

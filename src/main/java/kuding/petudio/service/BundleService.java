package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import kuding.petudio.service.etc.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@Transactional
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
     * @param bundleId
     * @return bundleDto
     */
    @Transactional(readOnly = true)
    public ServiceReturnBundleDto findBundleById(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);

        List<Picture> pictures = bundle.getPictures();
        List<ServiceReturnPictureDto> pictureDtoList = new ArrayList<>();
        for (Picture picture : pictures) {
            String pictureS3Url = amazonService.getPictureS3Url(picture.getStoredName());
            ServiceReturnPictureDto pictureDto = new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), pictureS3Url, picture.getPictureType());
            pictureDtoList.add(pictureDto);
        }
        return new ServiceReturnBundleDto(bundle.getId(), pictureDtoList, bundle.getBundleType());
    }

    /**
     * 해당 번들의 좋아요를 하나 증가시킨다.
     * @param bundleId
     */
    public void addLikeCont(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);
        bundle.addLikeCount();
    }

    /**
     * 최신 번들들을 반환한다.
     * @param pageOffset
     * @param pageSize
     * @return bundleDtoList
     */
    @Transactional(readOnly = true)
    public List<ServiceReturnBundleDto> findRecentBundles(int pageOffset, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bundle> bundlePage = bundleRepository.findAll(pageRequest);
        List<Bundle> bundles = bundlePage.getContent();
        log.info("bundles = {}", bundles);
        List<ServiceReturnBundleDto> serviceReturnBundleDtoList = new ArrayList<>();
        for (Bundle bundle : bundles) {
            List<Picture> pictures = bundle.getPictures();
            List<ServiceReturnPictureDto> pictureDtoList = new ArrayList<>();
            for (Picture picture : pictures) {
                String pictureS3Url = amazonService.getPictureS3Url(picture.getStoredName());
                ServiceReturnPictureDto serviceReturnPictureDto = new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), pictureS3Url, picture.getPictureType());
                pictureDtoList.add(serviceReturnPictureDto);
            }
            ServiceReturnBundleDto serviceReturnBundleDto = new ServiceReturnBundleDto(bundle.getId(), pictureDtoList, bundle.getBundleType());
            serviceReturnBundleDtoList.add(serviceReturnBundleDto);
        }
        return serviceReturnBundleDtoList;
    }

    /**
     * 넘겨받은 pictures를 묶어서 하나의 bundle로 DB에 저장
     * pictures를 S3에 저장
     * @param serviceParamPictureDtos 하나의 번들에 묶일 picture들
     * @param bundleType 어떠한 ai모델을 사용햇는가
     * @return bundle_id
     */
    public Long saveBundleBindingPictures(List<ServiceParamPictureDto> serviceParamPictureDtos, String title ,BundleType bundleType) {
        Bundle bundle = new Bundle(title, bundleType);
        List<Pair<Picture, ServiceParamPictureDto>> pairs = new ArrayList<>();

        //DB에 저장
        for (ServiceParamPictureDto serviceParamPictureDto : serviceParamPictureDtos) {
            String storedName = createStoredName(serviceParamPictureDto.getOriginalName());
            Picture picture = new Picture(serviceParamPictureDto.getOriginalName(), storedName, serviceParamPictureDto.getPictureType());
            pairs.add(new Pair<>(picture, serviceParamPictureDto));
            bundle.addPicture(picture);
        }
        Bundle saveBundle = bundleRepository.save(bundle);

        //s3에 저장
        for (Pair<Picture, ServiceParamPictureDto> pair : pairs) {
            Picture picture = pair.getFirst();
            ServiceParamPictureDto pictureDto = pair.getSecond();
            amazonService.savePictureToS3(pictureDto.getPictureFile(), picture.getStoredName());
        }

        return saveBundle.getId();
    }

    private String createStoredName(String originalName) {
        String uuid = UUID.randomUUID().toString();
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            return uuid;
        }
        String ext = originalName.substring(pos + 1);
        return uuid + "." + ext;
    }

}

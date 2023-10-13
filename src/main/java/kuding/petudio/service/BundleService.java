package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import kuding.petudio.etc.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
            ServiceReturnPictureDto pictureDto = new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), picture.getStoredName(), pictureS3Url, picture.getPictureType());
            pictureDtoList.add(pictureDto);
        }
        return new ServiceReturnBundleDto(bundle.getId(), pictureDtoList, bundle.getBundleType(), bundle.getLikeCount());
    }

    /**
     * 최신 번들 리스트를 반환한다.
     * @param pageOffset
     * @param pageSize
     * @return bundleDtoList
     */
    @Transactional(readOnly = true)
    public List<ServiceReturnBundleDto> findRecentPublicBundles(int pageOffset, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Bundle> bundlePage = bundleRepository.findByIsPublicTrue(pageRequest);
        List<Bundle> bundles = bundlePage.getContent();
        log.info("bundles = {}", bundles);
        List<ServiceReturnBundleDto> serviceReturnBundleDtoList = new ArrayList<>();
        for (Bundle bundle : bundles) {
            List<Picture> pictures = bundle.getPictures();
            List<ServiceReturnPictureDto> pictureDtoList = new ArrayList<>();
            for (Picture picture : pictures) {
                String pictureS3Url = amazonService.getPictureS3Url(picture.getStoredName());
                ServiceReturnPictureDto serviceReturnPictureDto = new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), picture.getStoredName(),pictureS3Url, picture.getPictureType());
                pictureDtoList.add(serviceReturnPictureDto);
            }
            ServiceReturnBundleDto serviceReturnBundleDto = new ServiceReturnBundleDto(bundle.getId(), pictureDtoList, bundle.getBundleType(), bundle.getLikeCount());
            serviceReturnBundleDtoList.add(serviceReturnBundleDto);
        }
        return serviceReturnBundleDtoList;
    }

    /**
     * 넘겨받은 'before' pictures를 묶어서 하나의 bundle로 DB에 저장
     * pictures를 S3에 저장
     * @param serviceParamPictureDtos 하나의 번들에 묶일 picture들
     * @param bundleType 어떠한 ai모델을 사용햇는가
     * @return bundle_id
     */
    public Long createBundleBindingBeforePictures(List<ServiceParamPictureDto> serviceParamPictureDtos, String title , BundleType bundleType) {
        Bundle bundle = new Bundle(title, bundleType);
        List<Pair<Picture, ServiceParamPictureDto>> pairs = new ArrayList<>();

        //DB에 저장
        for (ServiceParamPictureDto serviceParamPictureDto : serviceParamPictureDtos) {
            String storedName = createStoredName(serviceParamPictureDto.getOriginalName());
            Picture picture = new Picture(serviceParamPictureDto.getOriginalName(), storedName, PictureType.BEFORE);
            pairs.add(new Pair<>(picture, serviceParamPictureDto));
            bundle.addPicture(picture);
        }
        Bundle saveBundle = bundleRepository.save(bundle);

        //s3에 저장
        for (Pair<Picture, ServiceParamPictureDto> pair : pairs) {
            Picture picture = pair.getFirst();
            ServiceParamPictureDto pictureDto = pair.getSecond();
            amazonService.saveMultipartFileToS3(pictureDto.getPictureFile(), picture.getStoredName());
        }

        return saveBundle.getId();
    }

    /**
     * after picture에 대한 정보를 DB에 저장하고, s3에 사진 파일을 저장한다.
     * @param bundleId
     * @param afterPictures
     */
    public void addAfterPicturesToBundle(Long bundleId, List<File> afterPictures) {
        Bundle findBundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);
        List<Pair<Picture, File>> pairs = new ArrayList<>();

        for (File afterPicture : afterPictures) {
            String storedName = createStoredName(afterPicture.getName());
            Picture picture = new Picture(afterPicture.getName(), storedName, PictureType.AFTER);
            findBundle.addPicture(picture);
            pairs.add(new Pair<>(picture, afterPicture));
        }
        findBundle.completeCreatingAfterPicture();

        for (Pair<Picture, File> pair : pairs) {
            Picture pictureEntity = pair.getFirst();
            File pictureFile = pair.getSecond();
            amazonService.saveJavaFileToS3(pictureFile, pictureEntity.getStoredName());
        }
    }

    /**
     * 해당 번들의 좋아요를 하나 증가시킨다.
     * @param bundleId
     */
    public void addLikeCount(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);
        bundle.addLikeCount();
    }

    /**
     * 해당 번들의 접근을 public으로 전환한다.
     * @param bundleId
     */
    public void changeToPublic(Long bundleId) {
        Bundle findBundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);
        findBundle.changeToPublic();
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

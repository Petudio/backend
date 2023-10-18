package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return bundleToBundleDto(bundle);
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

        return bundles.stream()
                .map(this::bundleToBundleDto)
                .collect(Collectors.toList());
    }

    /**
     * bundle을 생성하고 해당 bundle의 id return
     * @param bundleType
     * @return
     */
    public Long createBundle(BundleType bundleType) {
        Bundle createBundle = new Bundle(bundleType);
        Bundle savedBundle = bundleRepository.save(createBundle);
        return savedBundle.getId();
    }

    /**
     * 해당 bundle에 picture들을 모두 저장
     * @param bundleId
     * @param pictureDtoList
     */
    public void addPicturesToBundle(Long bundleId, List<ServiceParamPictureDto> pictureDtoList) {
        Bundle findBundle = bundleRepository.findById(bundleId).orElseThrow();

        //param dto 와 picture entity pair 생성
        List<Pair<ServiceParamPictureDto, Picture>> paramPictureList = pictureDtoList.stream()
                .map(this::pictureDtoToPicture)
                .collect(Collectors.toList());

        //DB에 저장
        paramPictureList
                .forEach(paramPicture -> findBundle.addPicture(paramPicture.getSecond()));

        //S3에 저장
        paramPictureList
                .forEach(paramPicture -> amazonService.saveMultipartFileToS3(paramPicture.getFirst().getPictureFile(), paramPicture.getSecond().getStoredName()));
    }

    /**
     * 해당 번들은 AI모델을 통해 이미지 생성 완료되었다고 표시
     * @param bundleId
     */
    public void completeGeneratingAfterPictures(Long bundleId) {
        Bundle findBundle = bundleRepository.findById(bundleId).orElseThrow();
        findBundle.completeGeneratingAfterPictures();
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
     * 해당 번들의 접근을 public으로 전환하고 title을 추가한다.
     * @param bundleId
     */
    public void changeToPublic(Long bundleId, String title) {
        Bundle findBundle = bundleRepository.findById(bundleId).orElseThrow(NoSuchElementException::new);
        findBundle.changeToPublic(title);
    }

    private ServiceReturnBundleDto bundleToBundleDto(Bundle bundle) {
        List<Picture> pictures = bundle.getPictures();

        List<ServiceReturnPictureDto> pictureDtoList = pictures.stream()
                .map(picture ->
                        new ServiceReturnPictureDto(
                                picture.getId(),
                                picture.getOriginalName(),
                                picture.getStoredName(),
                                amazonService.getPictureS3Url(picture.getStoredName()),
                                picture.getPictureType()))
                .collect(Collectors.toList());

        return new ServiceReturnBundleDto(
                bundle.getId(),
                pictureDtoList,
                bundle.getBundleType(),
                bundle.getLikeCount());
    }

    private Pair<ServiceParamPictureDto, Picture> pictureDtoToPicture(ServiceParamPictureDto pictureDto) {
        Picture picture = new Picture(
                pictureDto.getOriginalName(),
                createStoredName(pictureDto.getOriginalName()),
                pictureDto.getPictureType());
        return new Pair<>(pictureDto, picture);
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

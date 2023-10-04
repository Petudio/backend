package kuding.petudio.controller;

import kuding.petudio.controller.dto.BundleUploadDTO;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.AmazonService;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.PictureService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static kuding.petudio.domain.BundleType.ANIMAL_TO_HUMAN;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/bundle")
public class BundleController {

    private final BundleService bundleService;
    private final PictureService pictureService;
    private final AmazonService amazonService;

    @GetMapping
    @ResponseBody
    public List<ServiceReturnBundleDto> bundleList(
            @RequestParam int pageOffset,
            @RequestParam int pageSize) {
        List<ServiceReturnBundleDto> recentBundles = bundleService.findRecentBundles(pageOffset, pageSize);

//        ServiceReturnPictureDto testDTO = new ServiceReturnPictureDto(1L, "test", new byte['1'], PictureType.BEFORE);
//        List<ServiceReturnPictureDto> serviceReturnPictureDtos = new ArrayList<>();
//        serviceReturnPictureDtos.add(testDTO);
//        ServiceReturnBundleDto testData = new ServiceReturnBundleDto(1L, serviceReturnPictureDtos, ANIMAL_TO_HUMAN);
//        recentBundles.add(testData); //테스트용
        return recentBundles;
    }

    /**
     * AI 생성 전 Before 이미지 업로드
     * @return 업로드한 이미지와 AI로 생성된 이미지를 담은 리스트
     */
    @PostMapping("/upload")
    public List<ServiceParamPictureDto> uploadBeforePicture(MultipartFile beforePicture){
        ServiceParamPictureDto beforePictureDto = new ServiceParamPictureDto(beforePicture.getOriginalFilename(),beforePicture,PictureType.BEFORE);
        ServiceParamPictureDto afterPictureDto = pictureService.animalToHuman(beforePictureDto);
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        pictureDtos.add(beforePictureDto);
        pictureDtos.add(afterPictureDto);
        return pictureDtos;
    }

//    /**
//     * 생성 버튼 눌렀을 때
//     * 1.before 이미지가 저장되어 있는 Bundle을 조회
//     * 2.Picture 서비스의 AI 메서드를 통해 afterImage 생성
//     * 3.조회한 번들에 afterImage 정보를 담아 다시 DB에 저장
//     */
//    @PostMapping("/upload/{bundleId}")
//    public void makeAfterImage(@PathVariable Long bundleId) {
//        ServiceReturnBundleDto findBundle = bundleService.findBundleById(bundleId); //번들에서 before 이미지가 저장되어 있는 번들 조회
//        List<ServiceReturnPictureDto> bundlePictures = findBundle.getPictures(); //조회한 번들에서 이미지 정보 가져옴
//        ServiceReturnPictureDto returnBeforePicture = bundlePictures.get(0); //before 이미지 정보
//        MultipartFile multipartFile = amazonService.convertByteArrayToMultiFile(returnBeforePicture.getPictureByteArray());
//        ServiceParamPictureDto beforePicture = new ServiceParamPictureDto(returnBeforePicture.getOriginalName(), multipartFile, returnBeforePicture.getPictureType());
//        ServiceParamPictureDto afterPicture = pictureService.animalToHuman(beforePicture);
//
//    }


    /**
     * Bundle의 업로드 데이터를 받아 DB에 저장한다.
     * @param bundleUploadDTO -> JSON 형식 HTTPBody
     * 주고 받는 데이터 형식 정리 필요
     */
    @PostMapping("/new")
    public void uploadBundle(@RequestBody BundleUploadDTO bundleUploadDTO) {

        List<MultipartFile> multipartFiles = bundleUploadDTO.getMultipartFiles();
        String bundleTitle = bundleUploadDTO.getBundleTitle(); //TODO Bundle 도메인에 타이틀 추가 필요

        //각 사진에 대한  DTO -> Before인지 After인지 알 수 있는 방법..? //TODO 리팩토링 필요
        ServiceParamPictureDto serviceParamPictureDtoBefore = makeServiceParamPictureDto(multipartFiles.get(0), PictureType.BEFORE);
        ServiceParamPictureDto serviceParamPictureDtoAfter = makeServiceParamPictureDto(multipartFiles.get(1), PictureType.AFTER);

        List<ServiceParamPictureDto> serviceParamPictureDtos = new ArrayList<>();
        serviceParamPictureDtos.add(serviceParamPictureDtoBefore);
        serviceParamPictureDtos.add(serviceParamPictureDtoAfter);

        bundleService.saveBundleBindingPictures(serviceParamPictureDtos, bundleTitle,ANIMAL_TO_HUMAN); // 게시글 업로드
    }

    private ServiceParamPictureDto makeServiceParamPictureDto(MultipartFile multipartFile, PictureType pictureType) {
        String originalName = multipartFile.getOriginalFilename();
        return new ServiceParamPictureDto(originalName, multipartFile, pictureType);
    }

    @PostMapping("/{bundleId}/like")
    public void addLikeCount(@PathVariable Long bundleId) {
        bundleService.addLikeCont(bundleId);
    }
}

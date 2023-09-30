package kuding.petudio.controller;

import kuding.petudio.controller.dto.BundleUploadDTO;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
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

        bundleService.saveBundleBindingPictures(serviceParamPictureDtos, ANIMAL_TO_HUMAN); // 게시글 업로드
    }

    private ServiceParamPictureDto makeServiceParamPictureDto(MultipartFile multipartFile, PictureType pictureType) {
        String originalName = multipartFile.getOriginalFilename();
        return new ServiceParamPictureDto(originalName, multipartFile, pictureType);
    }

    @PostMapping("/{bundleId}/like")
    public void addLikeCount(@PathVariable String bundleId) {
        bundleService.addLikeCont(Long.valueOf(bundleId));
    }
}

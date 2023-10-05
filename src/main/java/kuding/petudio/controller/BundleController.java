package kuding.petudio.controller;

import kuding.petudio.controller.dto.BundleUploadDTO;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.PictureService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Bundle 목록 조회
     * @return JSON 형식 데이터
     */
    @GetMapping
    @ResponseBody
    public List<ServiceReturnBundleDto> bundleList(
            @RequestParam int pageOffset,
            @RequestParam int pageSize) {
        List<ServiceReturnBundleDto> recentBundles = bundleService.findRecentBundles(pageOffset, pageSize);
        return recentBundles;
    }

    /**
     * AI 생성 전 Before 이미지 업로드
     * @return 업로드한 이미지와 AI로 생성된 이미지 DTO들을 JSON으로 변환하여 리턴
     */
    @ResponseBody
    @PostMapping("/upload")
    public List<ServiceParamPictureDto> uploadBeforePicture(@RequestParam("beforePicture") MultipartFile beforePicture){
        ServiceParamPictureDto beforePictureDto = new ServiceParamPictureDto(beforePicture.getOriginalFilename(),beforePicture,PictureType.BEFORE);
        ServiceParamPictureDto afterPictureDto = pictureService.animalToHuman(beforePictureDto);
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        pictureDtos.add(beforePictureDto);
        pictureDtos.add(afterPictureDto);
        return pictureDtos;
    }

    /**
     * 프론트에서 두 사진에 대한 DTO를 받아 DB에 저장한다.
     * @param bundleUploadDTO -> JSON 형식 HTTPBody
     * 주고 받는 데이터 형식 정리 필요
     */
    @PostMapping("/new")
    public void uploadBundle(@RequestBody BundleUploadDTO bundleUploadDTO) {

        List<ServiceParamPictureDto> pictureDtos = bundleUploadDTO.getPictureDtos();
        String bundleTitle = bundleUploadDTO.getBundleTitle();

        bundleService.saveBundleBindingPictures(pictureDtos, bundleTitle, ANIMAL_TO_HUMAN); // 게시글 업로드
    }

    @PostMapping("/{bundleId}/like")
    public void addLikeCount(@PathVariable Long bundleId) {
        bundleService.addLikeCont(bundleId);
    }
}

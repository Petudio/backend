package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.PictureReturnDto;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.AiPictureService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/bundle")
public class BundleController {

    private final BundleService bundleService;
    private final AiPictureService aiPictureService;

    /**
     * Bundle 목록 조회
     * @return JSON 형식 데이터
     */
    @GetMapping
    @ResponseBody
    public List<ServiceReturnBundleDto> bundleList(@RequestParam int pageOffset, @RequestParam int pageSize) {

        List<ServiceReturnBundleDto> recentBundles = bundleService.findRecentPublicBundles(pageOffset, pageSize);
        return recentBundles;
    }

    /**
     * AI 생성 전 Before 이미지 업로드
     * @return 업로드한 이미지와 AI로 생성된 이미지 DTO들을 JSON으로 변환하여 리턴
     */
    @ResponseBody
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestParam("beforePicture") MultipartFile beforePicture) throws IOException {
        ServiceParamPictureDto beforePictureDto = new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE);
        aiPictureService.animalToHuman(beforePictureDto);
        BaseDto baseDto = new BaseDto();
        baseDto.setData(null);
        return baseDto;
    }

    /**
     * 프론트에서 두 사진에 대한 DTO를 받아 DB에 저장한다.
     * @param bundleUploadDTO -> JSON 형식 HTTPBody
     * 주고 받는 데이터 형식 정리 필요
     */
    @PostMapping("/new")
    public void uploadBundle(@RequestParam Long bundleId, @RequestParam Boolean isPublic) {
        if(isPublic){
            bundleService.changeToPublic(bundleId); //TODO
        }
    }

    @PostMapping("/like/{bundleId}")
    public void addLikeCount(@PathVariable Long bundleId) {
        bundleService.addLikeCount(bundleId);
    }

    @GetMapping("/s3url/{bundleId}")
    public BundleReturnDto getBundle(@RequestParam Long bundleId) {
        ServiceReturnBundleDto findBundle = bundleService.findBundleById(bundleId);
        List<ServiceReturnPictureDto> pictures = findBundle.getPictures();
        List<PictureReturnDto> pictureReturnDtos = new ArrayList<PictureReturnDto>();
        for (ServiceReturnPictureDto picture : pictures) {
            pictureReturnDtos.add(new PictureReturnDto(picture.getId(), picture.getOriginalName(), picture.getPictureS3Url(), picture.getPictureType()));
        }
        return new BundleReturnDto(findBundle.getId(), pictureReturnDtos, findBundle.getBundleType());
    }
}

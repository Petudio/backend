package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.AiPictureService;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fourcuts")
public class FourCutsController {

    private final BundleService bundleService;
    private final AiPictureService aiPictureService;

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestParam("beforePictures") List<MultipartFile> beforePictures){
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile beforePicture : beforePictures) {
            pictureDtos.add(new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE));
        }
        Long bundleId = bundleService.createBundleBindingBeforePictures(pictureDtos, null, BundleType.ANIMAL_TO_HUMAN);
        aiPictureService.createSampleAfterPicture(bundleId);
        return new BaseDto(null);
    }
}


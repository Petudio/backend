package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.ColabServerCallService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/four-cuts")
public class FourCutsController {

    private final BundleService bundleService;
    private final ColabServerCallService colabServerCallService;
    private final CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestPart("beforePictures") List<MultipartFile> beforePictures) {
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile beforePicture : beforePictures) {
            pictureDtos.add(new ServiceParamPictureDto(beforePicture.getOriginalFilename(), template.execute(beforePicture::getBytes) ,PictureType.BEFORE, -1));
        }
        Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
        bundleService.addPicturesToBundle(bundleId, pictureDtos);

        ServiceReturnBundleDto bundleDto = bundleService.findBundleById(bundleId);
        BundleReturnDto bundle = DtoConverter.serviceReturnBundleToBundleReturn(bundleDto);
        return new BaseDto(bundle);
    }

    @GetMapping("/generate")
    public BaseDto generateAfterPictures(Long bundleId) {
        //TODO 제거 필요
        bundleService.completeTraining(bundleId);

        if (!bundleService.isTrainingComplete(bundleId)) {
            return new BaseDto("Training is not yet complete");
        }
        colabServerCallService.generateAfterPicture(bundleId);
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        return new BaseDto(bundle);
    }
}


package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.ColabServerCallService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/colab")
public class ColabController {

    private final BundleService bundleService;
    private final ColabServerCallService colabServerCallService;

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestPart("beforePictures") List<MultipartFile> beforePictures) {
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile beforePicture : beforePictures) {
            pictureDtos.add(new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE));
        }
        Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
        bundleService.addPicturesToBundle(bundleId, pictureDtos);

        log.info("bundleId = ", bundleId);
        ResponseEntity<String> responseEntity = colabServerCallService.generatePictures(bundleId);
        log.info("response from colab server = {}", responseEntity);

        ServiceReturnBundleDto bundleDto = bundleService.findBundleById(bundleId);
        BundleReturnDto bundle = DtoConverter.serviceReturnBundleToBundleReturn(bundleDto);
        return new BaseDto(bundle);
    }
}


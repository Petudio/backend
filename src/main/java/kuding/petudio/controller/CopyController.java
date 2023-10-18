package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.AiServerCallService;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/copy")
public class CopyController {

    private final BundleService bundleService;
    private final AiServerCallService aiServerCallService;

    /**
     * 프론트에서 건네준 before pictures를 이용해 bundle 생성, before pictures를 단순히 카피해주는 기능
     *
     * @param beforePictures
     * @return
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestParam("beforePictures") List<MultipartFile> beforePictures){
        List<ServiceParamPictureDto> pictureDtoList = beforePictures.stream()
                .map(beforePicture -> new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE))
                .collect(Collectors.toList());

        Long bundleId = bundleService.createBundle(BundleType.COPY);
        bundleService.addPicturesToBundle(bundleId, pictureDtoList);

        ServiceReturnBundleDto bundleDto = bundleService.findBundleById(bundleId);
        BundleReturnDto bundle = DtoConverter.serviceReturnBundleToBundleReturn(bundleDto);
        return new BaseDto(bundle);
    }

    @PostMapping("/generate/{bundleId}")
    public BaseDto generateAiPicture(@PathVariable Long bundleId) {
        ResponseEntity<String> responseEntity = aiServerCallService.generatePictures(bundleId);
        return new BaseDto(responseEntity.getStatusCode());
    }
}

package kuding.petudio.controller;

import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/callback")
public class AiServerCallBackController {

    private final BundleService bundleService;

    @PostMapping("/addAfter")
    private String addAfter(Long bundleId, @RequestParam("afterPictures") List<MultipartFile> afterPictures) {
        List<ServiceParamPictureDto> pictureDtoList = afterPictures.stream()
                .map(afterPicture -> new ServiceParamPictureDto(afterPicture.getOriginalFilename(), afterPicture, PictureType.AFTER))
                .collect(Collectors.toList());
        bundleService.addAfterPicturesToBundle(bundleId, afterPictures);
        return "ok";
    }
}

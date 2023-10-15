package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.AiServerCallService;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/copy")
public class CopyController {

    private final BundleService bundleService;
    private final AiServerCallService aiServerCallService;

    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestParam("beforePictures") List<MultipartFile> beforePictures, @RequestParam("title") String title) throws IOException {
        List<ServiceParamPictureDto> pictureDtoList = beforePictures.stream()
                .map(beforePicture -> new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE))
                .collect(Collectors.toList());
        Long bundleId = bundleService.createBundleBindingBeforePictures(pictureDtoList, title, BundleType.COPY);
        aiServerCallService.createCopyPictures(bundleId);
        return new BaseDto("ok");
    }
}

package kuding.petudio.service.testcontroller;

import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.PictureServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private BundleService bundleService;

    @PostMapping("/test/picture")
    public String uploadTestFile(@RequestParam("picture") MultipartFile picture) throws IOException {
        PictureServiceDto pictureServiceDto = new PictureServiceDto(picture.getOriginalFilename(), picture, PictureType.BEFORE);
        List<PictureServiceDto> pictures = new ArrayList<>();
        pictures.add(pictureServiceDto);
        bundleService.saveBundleBindingPictures(pictures, BundleType.ANIMAL_TO_HUMAN);
        return "ok";
    }
}

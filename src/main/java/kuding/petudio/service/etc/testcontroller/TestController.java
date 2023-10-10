package kuding.petudio.service.etc.testcontroller;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.repository.PictureRepository;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private BundleService bundleService;
    @Autowired
    private BundleRepository bundleRepository;
    @Autowired
    private PictureRepository pictureRepository;

    @PostMapping("/test/picture")
    public String uploadTestFile(@RequestParam("picture") MultipartFile picture) {
        ServiceParamPictureDto serviceParamPictureDto = new ServiceParamPictureDto(picture.getOriginalFilename(), picture, PictureType.BEFORE);
        List<ServiceParamPictureDto> pictures = new ArrayList<>();
        pictures.add(serviceParamPictureDto);
        bundleService.saveBundleBindingPictures(pictures, "example", BundleType.ANIMAL_TO_HUMAN);
        return "ok";
    }

    @PostMapping("/test/upload-picture-pair")
    public String uploadTestFile(@RequestParam("picture1") MultipartFile picture1, @RequestParam("picture2") MultipartFile picture2) {
        ServiceParamPictureDto serviceParamPictureDto1 = new ServiceParamPictureDto(picture1.getOriginalFilename(), picture1, PictureType.BEFORE);
        ServiceParamPictureDto serviceParamPictureDto2 = new ServiceParamPictureDto(picture2.getOriginalFilename(), picture2, PictureType.AFTER);
        List<ServiceParamPictureDto> pictures = new ArrayList<>();
        pictures.add(serviceParamPictureDto1);
        pictures.add(serviceParamPictureDto2);
        bundleService.saveBundleBindingPictures(pictures, "example", BundleType.ANIMAL_TO_HUMAN);
        return "ok";
    }

    @GetMapping("/test/getS3Url")
    public String getS3Url() {
        List<ServiceReturnBundleDto> recentBundles = bundleService.findRecentBundles(0, 5);
        log.info("recent Bundles = {}", recentBundles);
        return recentBundles.get(0).getPictures().get(0).getPictureS3Url();
    }

    @GetMapping("/test/getAll")
    public String getAll() {
        List<Bundle> all = bundleRepository.findAll();
        log.info("all = {}", all);
        List<Picture> all2 = pictureRepository.findAll();
        log.info("all2 = {}", all2);
        return "ok";
    }
}

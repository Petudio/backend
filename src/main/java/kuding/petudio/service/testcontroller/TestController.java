package kuding.petudio.service.testcontroller;

import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        ServiceParamPictureDto serviceParamPictureDto = new ServiceParamPictureDto(picture.getOriginalFilename(), picture, PictureType.BEFORE);
        List<ServiceParamPictureDto> pictures = new ArrayList<>();
        pictures.add(serviceParamPictureDto);
        bundleService.saveBundleBindingPictures(pictures, BundleType.ANIMAL_TO_HUMAN);
        return "ok";
    }

    @GetMapping("/test/pictures")
    public ResponseEntity getAllPictures() throws IOException {
        List<ServiceReturnBundleDto> bundles = bundleService.findBundlesOrderByCreatedDate(0, 5);
        String originalName = bundles.get(0).getPictures().get(0).getOriginalName();
        byte[] bytes = bundles.get(0).getPictures().get(0).getPictureByteArray();
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String contentDisposition = "attachment; filename = \"" + originalName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}

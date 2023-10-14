package kuding.petudio.etc.testcontroller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.AiPictureService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private BundleService bundleService;
    @Autowired
    private BundleRepository bundleRepository;
    @Autowired
    private AiPictureService aiPictureService;


    /**
     * ai 생성 모델에 대한 샘플 컨트롤러
     *
     * @param picture1
     * @return
     */
    @PostMapping("/test/process")
    public String process(@RequestParam("picture1") MultipartFile picture1) {
        List<ServiceParamPictureDto> pictures = new ArrayList<>();
        ServiceParamPictureDto picture = new ServiceParamPictureDto(picture1.getOriginalFilename(), picture1, PictureType.BEFORE);
        pictures.add(picture);
        Long bundleId = bundleService.createBundleBindingBeforePictures(pictures, "sample", BundleType.COPY);//before picture에 대해 먼저 번들을 생성함
        aiPictureService.createSampleAfterPicture(bundleId);//async함수, beforePicture를 통해 afterPicture를 생성하고 이를 위에서 이미 만들어 놓은 bundle에 저장
        return "ok";
    }

    @GetMapping("/test/changeToPublic")
    public String changeAllToPublic() {
        List<Bundle> bundles = bundleRepository.findAll();
        bundles.stream()
                .forEach(bundle -> bundleService.changeToPublic(bundle.getId()));
        return "ok";
    }

    @GetMapping("/test/getRecentPublic")
    public List<ServiceReturnBundleDto> getRecentPublic() {
        List<ServiceReturnBundleDto> recentPublicBundles = bundleService.findRecentPublicBundles(0, 5);
        log.info("bundle size = {}", recentPublicBundles.size());
        return recentPublicBundles;
    }

    @PostMapping("/test/aiserver")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String serverUrl = "http://localhost:8081/upload";

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            // 기존 ByteArrayResource의 getFilename 메서드 override
            @Override
            public String getFilename() {
                return "beforePicture";
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.POST,
                requestEntity,
                String.class);
        return "ok";
    }

    @PostMapping("/test/aiserver/multiple")
    public String handleFileUpload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        String serverUrl = "http://localhost:8081/upload/multiple";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile file : files) {
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("files",fileResource); //"files"가 AI 서버의 @RequestParam("files")와 매핑
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl,
                HttpMethod.POST,
                requestEntity,
                String.class);
        return "ok";
    }
}


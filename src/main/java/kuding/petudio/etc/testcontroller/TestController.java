package kuding.petudio.etc.testcontroller;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.AiServerCallService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private AiServerCallService aiServerCallService;

    @GetMapping("/test/changeToPublic")
    public String changeAllToPublic() {
        List<Bundle> bundles = bundleRepository.findAll();
        bundles.stream()
                .forEach(bundle -> bundleService.changeToPublic(bundle.getId(), "test Title"));
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


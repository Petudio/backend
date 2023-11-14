package kuding.petudio.service;

import com.amazonaws.util.Base64;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.type.PictureType;
import kuding.petudio.domain.Prompt;
import kuding.petudio.etc.Pair;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.dto.ColabServerResponseDto;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ColabServerCallService {
    private final BundleService bundleService;
    private final BundleRepository bundleRepository;
    private final CheckedExceptionConverterTemplate exceptionConvertTemplate = new CheckedExceptionConverterTemplate();
    private final AmazonService amazonService;

    @Value("${ColabServerUrl}")
    private String COLAB_SERVER_BASE_URL;

    @Transactional(readOnly = true)
    public void sendBeforePicturesToAiServer(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow();
        List<Pair<String, byte[]>> pairList = getPairListOfPictureNameAndByteArray(bundle);
        restTemplateToAiServer(bundle.getRandomName(), pairList);
    }

    public void generateAfterPicture(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow();
        List<Prompt> prompts = bundle.getPrompts();
        List<ServiceParamPictureDto> paramDtoList = prompts.stream()
                .map(prompt -> {
                    ColabServerResponseDto response = getAfterPicture(bundle.getRandomName(), prompt.getContent());
                    return new ServiceParamPictureDto(response.getFilename(), Base64.decode(response.getEncodedImage()), PictureType.AFTER, prompt.getSection());
                }).collect(Collectors.toList());
        bundleService.addPicturesToBundle(bundleId, paramDtoList);
    }

    @Transactional(readOnly = true)
    public boolean checkTrainingComplete(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow();
        String randomName = bundle.getRandomName();
        String url = UriComponentsBuilder.fromUriString(COLAB_SERVER_BASE_URL + "/isTrainingComplete")
                .queryParam("randomName", randomName)
                .build()
                .toString();
        RestTemplate restTemplate = new RestTemplate();
        String bool = restTemplate.getForEntity(url, String.class).getBody();
        return bool.equals("True");
    }

    private ColabServerResponseDto getAfterPicture(String randomName, String prompt) {
        String url = UriComponentsBuilder.fromUriString(COLAB_SERVER_BASE_URL + "/generate")
                .queryParam("randomName", randomName)
                .queryParam("prompt", prompt)
                .build()
                .toUriString();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> responseEntity = template.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ColabServerResponseDto response = exceptionConvertTemplate.execute(() -> objectMapper.readValue(responseEntity.getBody(), new TypeReference<ColabServerResponseDto>() {}));
        return response;
    }

    private List<Pair<String, byte[]>> getPairListOfPictureNameAndByteArray(Bundle bundle) {
        List<Picture> pictureList = bundle.getPictures();
        //beforePicture만 걸러내기 redundant?
        List<Picture> beforePictureList = pictureList.stream()
                .filter(picture -> picture.getPictureType() == PictureType.BEFORE)
                .collect(Collectors.toList());

        return beforePictureList.stream()
                .map(beforePicture -> new Pair<>(
                        beforePicture.getOriginalName(),
                        amazonService.getPictureBytesFromS3(beforePicture.getStoredName())))
                .collect(Collectors.toList());
    }

    private ResponseEntity<String> restTemplateToAiServer(String randomName, List<Pair<String, byte[]>> pairListOfPictureNameAndByteArray) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = createBody(pairListOfPictureNameAndByteArray, randomName);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                COLAB_SERVER_BASE_URL + "/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }

    private MultiValueMap<String, Object> createBody(List<Pair<String, byte[]>> pairList, String randomName) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("randomName", randomName);
        pairList.forEach(pair -> {
                    String originalName = pair.getFirst();
                    byte[] byteArray = pair.getSecond();
                    ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray) {
                        @Override
                        public String getFilename() {
                            return originalName;
                        }
                    };
                    body.add("beforePictures", byteArrayResource);
                });
        return body;
    }

}

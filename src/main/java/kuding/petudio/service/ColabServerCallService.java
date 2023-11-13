package kuding.petudio.service;

import com.amazonaws.util.Base64;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.PictureType;
import kuding.petudio.domain.Prompt;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.repository.BundleRepository;
import kuding.petudio.service.dto.ColabServerResponseDto;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ColabServerCallService {
    private final BundleService bundleService;
    private final BundleRepository bundleRepository;
    private final CheckedExceptionConverterTemplate exceptionConvertTemplate = new CheckedExceptionConverterTemplate();

    private String COLAB_SERVER_BASE_URL = "https://e1ca-104-196-183-146.ngrok-free.app";

    /**
     * 해당 번들의 before Pictures를 가지고 ai server에 image generating 요청
     * @param bundleId
     * @return
     */
    @Transactional
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

    private ColabServerResponseDto getAfterPicture(String randomName, String prompt) {
        String url = UriComponentsBuilder.fromUriString(COLAB_SERVER_BASE_URL + "/generate")
                .queryParam("randomName", randomName)
                .queryParam("prompt", prompt)
                .build()
                .toUriString();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> responseEntity = template.getForEntity(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return exceptionConvertTemplate.execute(() -> objectMapper.readValue(responseEntity.getBody(), new TypeReference<ColabServerResponseDto>() {}));
    }

}

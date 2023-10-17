package kuding.petudio.service;

import kuding.petudio.domain.PictureType;
import kuding.petudio.etc.Pair;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * bundleId를 건네받고 건네받은 번들의 beforePicture와 ai모델을 이용하여 afterPicture를 async하게 만들고 이를 DB,S3에 저장한다.
 */
@Slf4j
@Service
public class AiServerCallService {

    private final AmazonService amazonService;
    private final BundleService bundleService;
    @Value("${petudio.ai.server.url}")
    private String aiServerBaseUrl;

    @Autowired
    public AiServerCallService(AmazonService amazonService, BundleService bundleService) {
        this.amazonService = amazonService;
        this.bundleService = bundleService;
    }

    public ResponseEntity<String> createCopyPictures(Long bundleId) {
        //번들을 찾는다.
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        List<ServiceReturnPictureDto> pictureDtoList = bundle.getPictures();
        //해당 번들에서 beforePicture을 찾는다.
        List<ServiceReturnPictureDto> beforePictureDtoList = pictureDtoList.stream()
                .filter(pictureDto -> pictureDto.getPictureType() == PictureType.BEFORE)
                .collect(Collectors.toList());

        List<Pair<ServiceReturnPictureDto, byte[]>> pairList = beforePictureDtoList.stream()
                .map(beforePictureDto -> new Pair<>(beforePictureDto, amazonService.getPictureBytesFromS3(beforePictureDto.getStoredName())))
                .collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = createBody(pairList, bundleId);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                aiServerBaseUrl + "/copy/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        log.info("responseEntity = {}", responseEntity.getBody());
        return responseEntity;
    }

    private MultiValueMap<String, Object> createBody(List<Pair<ServiceReturnPictureDto, byte[]>> pairList, Long bundleId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("bundleId", bundleId);
        pairList
                .forEach(pair -> {
                    ServiceReturnPictureDto pictureDto = pair.getFirst();
                    byte[] byteArray = pair.getSecond();
                    ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray) {
                        @Override
                        public String getFilename() {
                            return pictureDto.getOriginalName();
                        }
                    };
                    body.add("beforePictures", byteArrayResource);
                });

        return body;
    }

}

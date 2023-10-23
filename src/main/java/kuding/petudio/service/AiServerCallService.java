package kuding.petudio.service;

import kuding.petudio.domain.Bundle;
import kuding.petudio.domain.BundleType;
import kuding.petudio.domain.Picture;
import kuding.petudio.domain.PictureType;
import kuding.petudio.etc.Pair;
import kuding.petudio.repository.BundleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final BundleRepository bundleRepository;
    @Value("${petudio.ai.server.url}")
    private String AI_SERVER_BASE_URL;

    @Autowired
    public AiServerCallService(AmazonService amazonService, BundleRepository bundleRepository) {
        this.amazonService = amazonService;
        this.bundleRepository = bundleRepository;
    }

    /**
     * 해당 번들의 before Pictures를 가지고 ai server에 image generating 요청
     * @param bundleId
     * @return
     */
    @Transactional(readOnly = true)
    public ResponseEntity<String> generatePictures(Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId).orElseThrow();
        List<Pair<String, byte[]>> pairListOfPictureNameAndByteArray = getPairListOfPictureNameAndByteArray(bundle);
        return sendBeforePicturesToAiServer(bundleId, pairListOfPictureNameAndByteArray, bundle.getBundleType());
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

    /**
     * 보내고 싶은 파일에 대해 original file name과 byte array pair로 묶어서 건네주면 ai 서버에 건네준다.
     * @param pairListOfPictureNameAndByteArray ai server에 보내고 싶은 사진 파일들을 건네준다, Pair<originalFileName, byteArray>
     * @param bundleType
     * @return
     */
    private ResponseEntity<String> sendBeforePicturesToAiServer(Long bundleId, List<Pair<String, byte[]>> pairListOfPictureNameAndByteArray, BundleType bundleType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = createBody(pairListOfPictureNameAndByteArray, bundleId);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                AI_SERVER_BASE_URL + bundleType.getAiServerPathUrl(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }

    /**
     * originalName byteArray pair를 가지고 multipart form data의 body 부분 생성
     * @param pairList
     * @param bundleId
     * @return
     */
    private MultiValueMap<String, Object> createBody(List<Pair<String, byte[]>> pairList, Long bundleId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("bundleId", bundleId);
        pairList
                .forEach(pair -> {
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

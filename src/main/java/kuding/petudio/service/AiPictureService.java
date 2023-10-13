package kuding.petudio.service;

import kuding.petudio.domain.PictureType;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * bundleId를 건네받고 건네받은 번들의 beforePicture와 ai모델을 이용하여 afterPicture를 async하게 만들고 이를 DB,S3에 저장한다.
 */
@Slf4j
@Service
public class AiPictureService {

    private final AmazonService amazonService;
    private final BundleService bundleService;
    //로컬 저장소의 base url
    @Value("${local.repository.baseurl}")
    private String baseUrl;
    @Value("${petudio.ai.server.url}")
    private String aiServerBaseUrl;

    @Autowired
    public AiPictureService( AmazonService amazonService, BundleService bundleService) {
        this.amazonService = amazonService;
        this.bundleService = bundleService;
    }







    @Async
    public void createSampleAfterPicture(Long bundleId) {
        //번들을 찾는다.
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        List<ServiceReturnPictureDto> pictures = bundle.getPictures();
        //해당 번들에서 beforePicture을 찾는다.
        ServiceReturnPictureDto beforePicture = pictures.stream()
                .filter(picture -> picture.getPictureType() == PictureType.BEFORE)
                .findAny().orElseThrow(IllegalStateException::new);
        //before Picture의 byte array를 s3에서 찾는다.
        byte[] beforePictureBytes = amazonService.getPictureBytesFromS3(beforePicture.getStoredName());

        //로컬 저장소에서 해당 번들의 아이디를 가지는 폴더를 생성한다, 해당 폴더에 해당 번들과 관련된 모든 사진파일을 저장한다.
        File bundleFolder = new File(baseUrl + "/" +bundleId);
        bundleFolder.mkdir();

        //beforePicture 파일을 로컬 폴더에 저장한다.
        File beforePictureFile = new File(baseUrl + "/" + bundleId + "/" + beforePicture.getOriginalName());
        CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();
        template.execute(() -> {
            boolean newFile = beforePictureFile.createNewFile();
            log.info("newFile = {}", newFile);
            FileOutputStream fos = new FileOutputStream(beforePictureFile);
            fos.write(beforePictureBytes);
            fos.close();
            return null;
        });

        //TODO beforePictureFile과 AI 모델을 이용해 새로운 afterPicture 생성, 여기서는 단순히 복사만 함.
        String originalNameAfter = createOriginalNameAfter(beforePicture.getOriginalName());
        log.info("after name = {}", originalNameAfter);
        File afterPictureFile = new File(baseUrl + "/" + bundleId + "/" + originalNameAfter);
        Boolean newFile = template.execute(afterPictureFile::createNewFile);
        log.info("newAfterFile = {}", newFile);
        FileInputStream beforePictureInputStream = template.execute(() -> new FileInputStream(beforePictureFile));
        FileOutputStream afterPictureOutputStream = template.execute(() -> new FileOutputStream(afterPictureFile));

        byte[] buf = new byte[1024];

        //afterPicture 파일을 로컬 폴더에 저장한다.
        template.execute(() -> {
            int readData;
            while ((readData = beforePictureInputStream.read(buf)) > 0) {
                afterPictureOutputStream.write(buf, 0, readData);
            }
            beforePictureInputStream.close();
            afterPictureOutputStream.close();
            return null;
        });

        String str = sampleRestRequest();
        log.info("restTemplate = {}", str);

        template.execute(() -> {
            Thread.sleep(1000 * 10);
            return null;
        });
//        TODO=========================================

        //DB에 afterPicture에 대한 내용 저장하고, s3에 저장하기
        ArrayList<File> files = new ArrayList<>();
        files.add(afterPictureFile);
        bundleService.addAfterPicturesToBundle(bundleId, files);

        //로컬 파일 삭제하기, FileUtils를 통해 한번에 삭제하도록 리팩토링?
        beforePictureFile.delete();
        afterPictureFile.delete();
        bundleFolder.delete();
    }

    private String sampleRestRequest() {
        URI uri = UriComponentsBuilder
                .fromUriString(aiServerBaseUrl)
                .path("/hello")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.getForEntity(uri, String.class);
        return resp.getBody();
    }

    private String createOriginalNameAfter(String originalName) {
        int pos = originalName.lastIndexOf(".");
        if (pos == -1) {
            return originalName + "_after";
        }
        String prefix = originalName.substring(0, pos);
        String ext = originalName.substring(pos + 1);
        return prefix + "_after" + "." + ext;
    }


}

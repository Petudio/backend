package kuding.petudio.service;

import kuding.petudio.domain.PictureType;
import kuding.petudio.repository.PictureRepository;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import kuding.petudio.service.etc.callback.ExceptionResolveTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * bundleId를 건네받고 건네받은 번들의 beforePicture와 ai모델을 이용하여 afterPicture를 async하게 만들고 이를 DB,S3에 저장한다.
 */
@Slf4j
@Service
public class AiPictureService {

    private final PictureRepository pictureRepository;
    private final AmazonService amazonService;
    private final BundleService bundleService;
    @Value("${local.repository.baseurl}")
    private String baseUrl;

    public AiPictureService(PictureRepository pictureRepository, AmazonService amazonService, BundleService bundleService) {
        this.pictureRepository = pictureRepository;
        this.amazonService = amazonService;
        this.bundleService = bundleService;
    }

    /**
     *
     * @param picture : 원본 이미지
     * @return 생성 이미지
     * 동물 이미지 -> 사람 이미지 생성
     */
    public ServiceParamPictureDto animalToHuman(ServiceParamPictureDto picture) {
        //TODO AI알고리즘으로 이미지 생성, 생성된 이미지 return
        System.out.println("sleep 실행 전");

        try {

            Thread.sleep(1000 * 10); //10초 대기

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

        System.out.println("sleep 실행 후");
        return null;//return afterImage
    }





    @Async
    public void createSampleAfterPicture(Long bundleId) {
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        List<ServiceReturnPictureDto> pictures = bundle.getPictures();
        ServiceReturnPictureDto beforePicture = pictures.stream()
                .filter(picture -> picture.getPictureType() == PictureType.BEFORE)
                .findAny().orElseThrow(IllegalStateException::new);
        byte[] beforePictureBytes = amazonService.getPictureBytesFromS3(beforePicture.getStoredName());

        File bundleFolder = new File(baseUrl + "/" +bundleId);
        bundleFolder.mkdir();

        File beforePictureFile = new File(baseUrl + "/" + bundleId + "/" + beforePicture.getOriginalName());
        ExceptionResolveTemplate template = new ExceptionResolveTemplate();
        template.execute(() -> {
            boolean newFile = beforePictureFile.createNewFile();
            log.info("newFile = {}", newFile);
            FileOutputStream fos = new FileOutputStream(beforePictureFile);
            fos.write(beforePictureBytes);
            fos.close();
            return null;
        });

        //TODO beforePictureFile과 AI 모델을 이용해 새로운 afterPicture 생성
        String originalNameAfter = createOriginalNameAfter(beforePicture.getOriginalName());
        log.info("after name = {}", originalNameAfter);
        File afterPictureFile = new File(baseUrl + "/" + bundleId + "/" + originalNameAfter);
        Boolean newFile = template.execute(afterPictureFile::createNewFile);
        log.info("newAfterFile = {}", newFile);
        FileInputStream beforePictureInputStream = template.execute(() -> new FileInputStream(beforePictureFile));
        FileOutputStream afterPictureOutputStream = template.execute(() -> new FileOutputStream(afterPictureFile));

        byte[] buf = new byte[1024];

        template.execute(() -> {
            int readData;
            while ((readData = beforePictureInputStream.read(buf)) > 0) {
                afterPictureOutputStream.write(buf, 0, readData);
            }
            beforePictureInputStream.close();
            afterPictureOutputStream.close();
            return null;
        });

        template.execute(() -> {
            Thread.sleep(1000 * 10);
            return null;
        });
//        TODO=========================================

        ArrayList<File> files = new ArrayList<>();
        files.add(afterPictureFile);
        bundleService.addAfterPicturesToBundle(bundleId, files);

        //TODO 파일 삭제하기, FileUtils를 통해 한번에 삭제하도록 리팩토링?
        beforePictureFile.delete();
        afterPictureFile.delete();
        bundleFolder.delete();
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

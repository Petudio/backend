package kuding.petudio.service;

import kuding.petudio.domain.Picture;
import kuding.petudio.repository.PictureRepository;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class PictureService {

    private final PictureRepository pictureRepository;
    private final AmazonService amazonService;

    @Autowired
    public PictureService(PictureRepository pictureRepository, AmazonService amazonService) {
        this.pictureRepository = pictureRepository;
        this.amazonService = amazonService;
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

    @Transactional
    public ServiceReturnPictureDto findPictureById(Long pictureId) {
        Picture picture = pictureRepository.findById(pictureId).orElseThrow(NoSuchElementException::new);
        String pictureS3Url = amazonService.getPictureS3Url(picture.getStoredName());
        return new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), pictureS3Url, picture.getPictureType());
    }


}

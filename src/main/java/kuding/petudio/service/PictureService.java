package kuding.petudio.service;

import kuding.petudio.domain.Picture;
import kuding.petudio.repository.PictureRepository;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import kuding.petudio.service.lowservice.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        return null;//return afterImage
    }

    @Transactional
    public ServiceReturnPictureDto findPictureById(Long pictureId) {
        Picture picture = pictureRepository.findById(pictureId).orElseThrow(NoSuchElementException::new);
        byte[] byteArray = amazonService.getPictureFromS3(picture.getStoredName());
        return new ServiceReturnPictureDto(picture.getId(), picture.getOriginalName(), byteArray, picture.getPictureType());
    }

}

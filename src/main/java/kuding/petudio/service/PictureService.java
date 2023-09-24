package kuding.petudio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PictureService {

    /**
     *
     * @param picture : 원본 이미지
     * @return 생성 이미지
     * 동물 이미지 -> 사람 이미지 생성
     */
    public PictureServiceDto animalToHuman(PictureServiceDto picture) {
        //AI알고리즘으로 이미지 생성, 생성된 이미지 return
        return null;//return afterImage
    }

    public PictureServiceDto service2(PictureServiceDto picture) {
        return null;
    }


    /**
     *
     * @param pictures
     * pictures를 모두 S3 저장소에 저장
     */
    public void savePicturesToS3(List<PictureServiceDto> pictures) {
        //pictures에 있는 multipartFile을 모두 S3 저장소에 저장
    }
}

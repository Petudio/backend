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
        //TODO AI알고리즘으로 이미지 생성, 생성된 이미지 return
        return null;//return afterImage
    }

    public PictureServiceDto service2(PictureServiceDto picture) {
        return null;
    }

}

package kuding.petudio.service;

import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PictureService {

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

    public ServiceParamPictureDto service2(ServiceParamPictureDto picture) {
        return null;
    }

}

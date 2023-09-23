package kuding.petudio.service;

import kuding.petudio.dto.PictureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class PictureService {

    public MultipartFile imageRender(MultipartFile beforeImage) {
        //AI알고리즘으로 이미지 생성, 생성된 이미지 return
        return null;//return afterImage
    }

    public void storeImage(List<PictureDto> pictures) {

    }
}

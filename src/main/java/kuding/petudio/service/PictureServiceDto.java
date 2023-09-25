package kuding.petudio.service;

import kuding.petudio.domain.PictureType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


/**
 * controller와 service간 picture를 주고받기 위한 객체
 */
@Getter
public class PictureServiceDto {
    private String originalName;
    private MultipartFile pictureFile;
    private PictureType pictureType;

    public PictureServiceDto(String originalName, MultipartFile pictureFile, PictureType pictureType) {
        this.originalName = originalName;
        this.pictureFile = pictureFile;
        this.pictureType = pictureType;
    }
}

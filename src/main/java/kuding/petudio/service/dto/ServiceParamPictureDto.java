package kuding.petudio.service.dto;

import kuding.petudio.domain.PictureType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ServiceParamPictureDto {
    private String originalName;
    private MultipartFile pictureFile;
    private PictureType pictureType;

    public ServiceParamPictureDto(String originalName, MultipartFile pictureFile, PictureType pictureType) {
        this.originalName = originalName;
        this.pictureFile = pictureFile;
        this.pictureType = pictureType;
    }
}

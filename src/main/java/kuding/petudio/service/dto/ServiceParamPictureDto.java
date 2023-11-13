package kuding.petudio.service.dto;

import kuding.petudio.domain.PictureType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ServiceParamPictureDto {
    private String originalName;
    private byte[] pictureFileByteArray;
    private PictureType pictureType;

    public ServiceParamPictureDto(String originalName, byte[] pictureFileByteArray, PictureType pictureType) {
        this.originalName = originalName;
        this.pictureFileByteArray = pictureFileByteArray;
        this.pictureType = pictureType;
    }
}

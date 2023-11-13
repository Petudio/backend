package kuding.petudio.service.dto;

import kuding.petudio.domain.PictureType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ServiceParamPictureDto {
    private String originalName;
    private byte[] pictureFileByteArray;
    private PictureType pictureType;
    private int section;

    public ServiceParamPictureDto(String originalName, byte[] pictureFileByteArray, PictureType pictureType, int section) {
        this.originalName = originalName;
        this.pictureFileByteArray = pictureFileByteArray;
        this.pictureType = pictureType;
        this.section = section;
    }
}

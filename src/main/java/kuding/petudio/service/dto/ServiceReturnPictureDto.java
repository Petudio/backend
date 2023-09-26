package kuding.petudio.service.dto;

import kuding.petudio.domain.PictureType;
import lombok.Getter;

@Getter
public class ServiceReturnPictureDto {
    private Long id;
    private String originalName;
    private byte[] pictureByteArray;
    private PictureType pictureType;

    public ServiceReturnPictureDto(Long id, String originalName, byte[] pictureByteArray, PictureType pictureType) {
        this.id = id;
        this.originalName = originalName;
        this.pictureByteArray = pictureByteArray;
        this.pictureType = pictureType;
    }
}

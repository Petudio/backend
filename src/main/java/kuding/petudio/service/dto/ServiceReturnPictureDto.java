package kuding.petudio.service.dto;

import kuding.petudio.domain.type.PictureType;
import lombok.Getter;

@Getter
public class ServiceReturnPictureDto {
    private Long id;
    private String originalName;
    private String storedName;
    private String pictureS3Url;
    private PictureType pictureType;
    private int section;

    public ServiceReturnPictureDto(Long id, String originalName, String storedName, String pictureS3Url, PictureType pictureType, int section) {
        this.id = id;
        this.originalName = originalName;
        this.storedName = storedName;
        this.pictureS3Url = pictureS3Url;
        this.pictureType = pictureType;
        this.section = section;
    }
}

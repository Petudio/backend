package kuding.petudio.controller.dto;

import kuding.petudio.domain.PictureType;
import lombok.Getter;

@Getter
public class PictureReturnDto {
    private Long pictureId;
    private String originalName;
    private String pictureLink;
    private PictureType pictureType;

    public PictureReturnDto(Long pictureId, String originalName, String pictureLink, PictureType pictureType) {
        this.pictureId = pictureId;
        this.originalName = originalName;
        this.pictureLink = pictureLink;
        this.pictureType = pictureType;
    }
}

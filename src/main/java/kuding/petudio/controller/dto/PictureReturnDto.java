package kuding.petudio.controller.dto;

import kuding.petudio.domain.type.PictureType;
import lombok.Getter;

@Getter
public class PictureReturnDto {
    private Long pictureId;
    private String originalName;
    private String pictureLink;
    private PictureType pictureType;
    private int section;

    public PictureReturnDto(Long pictureId, String originalName, String pictureLink, PictureType pictureType, int section) {
        this.pictureId = pictureId;
        this.originalName = originalName;
        this.pictureLink = pictureLink;
        this.pictureType = pictureType;
        this.section = section;
    }
}

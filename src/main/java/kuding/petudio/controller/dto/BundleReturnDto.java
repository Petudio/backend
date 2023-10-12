package kuding.petudio.controller.dto;

import kuding.petudio.domain.BundleType;
import lombok.Getter;

import java.util.List;

@Getter
public class BundleReturnDto {
    private Long bundleId;
    private List<PictureReturnDto> pictureReturnDtos;
    private BundleType bundleType;

    public BundleReturnDto(Long bundleId, List<PictureReturnDto> pictureReturnDtos, BundleType bundleType) {
        this.bundleId = bundleId;
        this.pictureReturnDtos = pictureReturnDtos;
        this.bundleType = bundleType;
    }
}

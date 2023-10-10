package kuding.petudio.controller.dto;

import kuding.petudio.domain.BundleType;

import java.util.List;

public class BundleReturnDto {
    private Long id;
    private List<PictureReturnDto> pictureReturnDtos;
    private BundleType bundleType;

    public BundleReturnDto(Long id, List<PictureReturnDto> pictureReturnDtos, BundleType bundleType) {
        this.id = id;
        this.pictureReturnDtos = pictureReturnDtos;
        this.bundleType = bundleType;
    }
}

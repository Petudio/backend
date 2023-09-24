package kuding.petudio.service;

import kuding.petudio.domain.BundleType;
import lombok.Getter;

import java.util.List;

/**
 * service와 contrller간의 bundle을 주고받기 위한 객체
 */
@Getter
public class BundleServiceDto {

    private List<PictureServiceDto> pictures;
    private BundleType bundleType;

    public BundleServiceDto(List<PictureServiceDto> pictures, BundleType bundleType) {
        this.pictures = pictures;
        this.bundleType = bundleType;
    }
}

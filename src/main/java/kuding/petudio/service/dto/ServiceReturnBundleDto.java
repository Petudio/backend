package kuding.petudio.service.dto;

import kuding.petudio.domain.type.AnimalType;
import kuding.petudio.domain.type.BundleType;
import lombok.Getter;

import java.util.List;

/**
 * service와 contrller간의 bundle을 주고받기 위한 객체
 */
@Getter
public class ServiceReturnBundleDto {
    private Long id;
    private List<ServiceReturnPictureDto> pictures;
    private BundleType bundleType;
    private String randomName;
    private AnimalType animalType;
    private int likeCount;

    public ServiceReturnBundleDto(Long id, List<ServiceReturnPictureDto> pictures, BundleType bundleType, int likeCount, String randomName) {
        this.pictures = pictures;
        this.id = id;
        this.bundleType = bundleType;
        this.likeCount = likeCount;
        this.randomName = randomName;
    }

}

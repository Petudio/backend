package kuding.petudio.service.dto;

import kuding.petudio.domain.BundleType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * service와 contrller간의 bundle을 주고받기 위한 객체
 */
@Getter
public class ServiceReturnBundleDto {

    private List<ServiceReturnPictureDto> pictures = new ArrayList<>();
    private BundleType bundleType;

    public ServiceReturnBundleDto( BundleType bundleType) {
        this.bundleType = bundleType;
    }
}

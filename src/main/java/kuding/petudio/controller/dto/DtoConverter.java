package kuding.petudio.controller.dto;

import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;

import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

    /**
     * serviceReturnBundleDto -> BundleReturnDto 변환
     * @param serviceReturnBundleDto
     * @return BundleReturnDto
     */
    public static BundleReturnDto serviceReturnBundleToBundleReturn(ServiceReturnBundleDto serviceReturnBundleDto) {
        List<PictureReturnDto> recentPictures = serviceReturnBundleDto.getPictures().stream()
                .map(DtoConverter::serviceReturnPictureToPictureReturn)
                .collect(Collectors.toList());

        return new BundleReturnDto(serviceReturnBundleDto.getId(), recentPictures, serviceReturnBundleDto.getBundleType());
    }

    /**
     * ServiceReturnPictureDto -> PictureReturnDto 변환
     * @param serviceReturnPictureDto
     * @return PictureReturnDto
     */
    public static PictureReturnDto serviceReturnPictureToPictureReturn(ServiceReturnPictureDto serviceReturnPictureDto) {
        return new PictureReturnDto(
                serviceReturnPictureDto.getId(),
                serviceReturnPictureDto.getOriginalName(),
                serviceReturnPictureDto.getPictureS3Url(),
                serviceReturnPictureDto.getPictureType()
        );
    }

}

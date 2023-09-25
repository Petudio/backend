package kuding.petudio.service.lowservice;

import kuding.petudio.domain.Picture;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import lombok.Data;

@Data
public class PairPictureAndPictureServiceDto {
    private final Picture picture;
    private final ServiceParamPictureDto serviceParamPictureDto;
}

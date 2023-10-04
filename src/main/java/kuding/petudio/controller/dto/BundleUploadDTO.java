package kuding.petudio.controller.dto;

import kuding.petudio.domain.Picture;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BundleUploadDTO {

    private List<ServiceParamPictureDto> pictureDtos;
    private String bundleTitle;
}

package kuding.petudio.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PictureDto {
    private String originalName;
    private MultipartFile file;
}

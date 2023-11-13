package kuding.petudio.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColabServerResponseDto {
    private String filename;
    private String encodedImage;
}

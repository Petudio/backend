package kuding.petudio.controller.dto;

import lombok.Data;

@Data
public class BaseDto {
    private Object data;

    public BaseDto(Object data) {
        this.data = data;
    }
}

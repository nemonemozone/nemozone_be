package Nemozone.Nemozone.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoLoginResponseDto<T> {
    private T body;
}

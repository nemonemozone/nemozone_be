package Nemozone.Nemozone.dto;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TotalDateResponseDto {
    private final Long totalDate;
}

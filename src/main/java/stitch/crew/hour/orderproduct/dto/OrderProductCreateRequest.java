package stitch.crew.hour.orderproduct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderProductCreateRequest(
       @NotBlank String productName,
       @NotNull Long price,
       @NotNull Long productId,
       @NotNull Long amount,
       @NotBlank String option
) {
}


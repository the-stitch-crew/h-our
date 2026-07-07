package stitch.crew.hour.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

public record Paging(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") @Min(0) @Max(50) int size
) {
    public PageRequest toPageable(){ return PageRequest.of(page,size); }
}

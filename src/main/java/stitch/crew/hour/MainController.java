package stitch.crew.hour;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.ApiResponse;
import stitch.crew.hour.common.response.ApiResult;

@RestController
public class MainController {
    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> index() {
        return ApiResult.error(ErrorCode.INTERNAL_ERROR);
    }
}

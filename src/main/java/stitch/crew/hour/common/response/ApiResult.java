package stitch.crew.hour.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.exception.ErrorCode;

public class ApiResult {

    private static <T> ApiResponse<T> success(SuccessCode code,T data) {
        return new ApiResponse<>(
                true,
                code.name(),
                code.getSuccessMessage(),
                data
        );
    }

    private static <T> ApiResponse<T> fail(ErrorCode code) {
        return new ApiResponse<>(
                false,
                code.name(),
                code.getMessage(),
                null
        );
    }
    private static <T> ApiResponse<T> fail(ErrorCode code, String message) {
        return new ApiResponse<>(
                false,
                code.name(),
                message,
                null
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(SuccessCode code, T data) {
        return ResponseEntity.ok(success(code, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> ok(SuccessCode code) {
        return ResponseEntity.ok(success(code, null));
    }


    public static <T> ResponseEntity<ApiResponse<T>> created( SuccessCode code, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(success(code, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> created(SuccessCode code) {
        return ResponseEntity.status(HttpStatus.CREATED).body(success(code, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode code) {
        return ResponseEntity.status(code.getStatus()).body(fail(code));
    }
    public static <T> ResponseEntity<ApiResponse<T>> error(ErrorCode code,  String message) {
        return ResponseEntity.status(code.getStatus()).body(fail(code, message));
    }
}

package stitch.crew.hour.common.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import stitch.crew.hour.common.response.ApiResponse;
import stitch.crew.hour.common.response.ApiResult;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode code = exception.getErrorCode();
        return ApiResult.error(code);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String errorMessage = exception.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResult.error(ErrorCode.VALIDATION_FAILED, errorMessage);

    }
}

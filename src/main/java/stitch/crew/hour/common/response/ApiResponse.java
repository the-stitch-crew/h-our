package stitch.crew.hour.common.response;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
){
}

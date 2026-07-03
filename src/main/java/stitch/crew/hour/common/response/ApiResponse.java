package stitch.crew.hour.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
){
	public static <T> ApiResponse<T> ok(T data, String successMessage){
		return new ApiResponse<>(
			true,
			HttpStatus.OK.toString(),
			successMessage,
			data
		);
	}

	public static <T> ApiResponse<T> created(T data, String successMessage){
		return new ApiResponse<>(
			true,
			HttpStatus.CREATED.toString(),
			successMessage,
			data
		);
	}
}

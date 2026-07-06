package stitch.crew.hour.auth.dto;

public record LoginResponse(
	String accessToken,
	String refreshToken
) {
}

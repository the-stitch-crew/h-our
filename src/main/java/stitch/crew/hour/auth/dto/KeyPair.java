package stitch.crew.hour.auth.dto;

public record KeyPair(
	String accessToken,
	String refreshToken
) {
}

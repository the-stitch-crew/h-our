package stitch.crew.hour.auth.dto;

public record OAuthSignupPayload(
	String email,
	String userName,
	String provider
) {
}

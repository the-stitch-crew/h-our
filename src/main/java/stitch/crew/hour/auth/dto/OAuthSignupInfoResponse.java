package stitch.crew.hour.auth.dto;

public record OAuthSignupInfoResponse(
	String email,
	String userName,
	String provider
) {
	public static OAuthSignupInfoResponse from(OAuthSignupPayload payload) {
		return new OAuthSignupInfoResponse(
			payload.email(),
			payload.userName(),
			payload.provider()
		);
	}
}

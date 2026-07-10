package stitch.crew.hour.auth.service;

import java.time.Duration;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class OAuthSignupCookieManager {

	public static final String SIGNUP_TOKEN_COOKIE_NAME = "signupToken";

	@Value("${custom.oauth.signup-token-ttl-ms}")
	private long signupTokenTtlMillis;

	@Value("${custom.oauth.signup-cookie.path:/api/auth/oauth}")
	private String path;

	@Value("${custom.oauth.signup-cookie.secure:false}")
	private boolean secure;

	@Value("${custom.oauth.signup-cookie.same-site:Lax}")
	private String sameSite;

	public void addSignupTokenCookie(
		HttpServletResponse response,
		String signupToken
	) {
		response.addHeader(
			HttpHeaders.SET_COOKIE,
			createCookie(signupToken, Duration.ofMillis(signupTokenTtlMillis)).toString()
		);
	}

	public String createExpiredCookieHeader() {
		return createCookie("", Duration.ZERO).toString();
	}

	private ResponseCookie createCookie(
		String value,
		Duration maxAge
	) {
		return ResponseCookie.from(SIGNUP_TOKEN_COOKIE_NAME, value)
			.httpOnly(true)
			.secure(secure)
			.path(path)
			.maxAge(maxAge)
			.sameSite(sameSite)
			.build();
	}
}

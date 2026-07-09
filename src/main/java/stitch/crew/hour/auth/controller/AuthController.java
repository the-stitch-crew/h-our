package stitch.crew.hour.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.dto.OAuthSignupInfoResponse;
import stitch.crew.hour.auth.dto.OAuthSignupRequest;
import stitch.crew.hour.auth.service.AuthService;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.auth.dto.RefreshTokenRequest;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponses<KeyPair>> login(
		@Valid @RequestBody LoginRequest request
	) {
		KeyPair response = authService.login(request);
		return ApiResult.ok(SuccessCode.AUTH_LOGIN_SUCCESS, response);
	}

	@PostMapping("/oauth/signup")
	public ResponseEntity<ApiResponses<KeyPair>> oauthSignup(
		@Valid @RequestBody OAuthSignupRequest request
	) {
		KeyPair response = authService.oauthSignup(request);
		return ApiResult.created(SuccessCode.USER_CREATED, response);
	}

	@GetMapping("/oauth/signup")
	public ResponseEntity<ApiResponses<OAuthSignupInfoResponse>> getOAuthSignupInfo(
		@RequestParam String signupToken
	) {
		OAuthSignupInfoResponse response = authService.getOAuthSignupInfo(signupToken);
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponses<KeyPair>> refresh(
		@Valid @RequestBody RefreshTokenRequest request
	) {
		KeyPair response = authService.refresh(request);
		return ApiResult.ok(SuccessCode.AUTH_REFRESH_SUCCESS, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponses<Void>> logout(
		@Valid @RequestBody RefreshTokenRequest request
	) {
		authService.logout(request);
		return ApiResult.ok(SuccessCode.AUTH_LOGOUT_SUCCESS);
	}
}

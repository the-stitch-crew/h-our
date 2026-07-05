package stitch.crew.hour.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.service.AuthService;
import stitch.crew.hour.auth.dto.LoginResponse;
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
	public ResponseEntity<ApiResponses<LoginResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		LoginResponse response = authService.login(request);
		return ApiResult.ok(SuccessCode.AUTH_LOGIN_SUCCESS, response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponses<LoginResponse>> refresh(
		@Valid @RequestBody RefreshTokenRequest request
	) {
		LoginResponse response = authService.refresh(request);
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

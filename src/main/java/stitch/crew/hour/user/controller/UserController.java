package stitch.crew.hour.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stitch.crew.hour.common.response.ApiResponse;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signUp(
		@Valid @RequestBody SignupRequest request
	){
		SignupResponse response = userService.signup(request);
		return ApiResult.created(SuccessCode.USER_CREATED, response);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(Authentication authentication) {
		UserInfoResponse response = userService.getMyInfo(authentication.getName());
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

}

package stitch.crew.hour.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.dto.MyPageResponse;
import stitch.crew.hour.user.dto.PasswordChangeRequest;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.UserUpdateRequest;
import stitch.crew.hour.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponses<SignupResponse>> signUp(
		@Valid @RequestBody SignupRequest request
	){
		SignupResponse response = userService.signup(request);
		return ApiResult.created(SuccessCode.USER_CREATED, response);
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponses<UserInfoResponse>> getMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		UserInfoResponse response = userService.getMyInfo(currentUser.getEmail());
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

	@GetMapping("/me/mypage")
	public ResponseEntity<ApiResponses<MyPageResponse>> getMyPage(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		MyPageResponse response = userService.getMyPage(currentUser.getEmail());
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}


	@PatchMapping("/me")
	public ResponseEntity<ApiResponses<UserInfoResponse>> updateMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody UserUpdateRequest request
	) {
		UserInfoResponse response = userService.updateMyInfo(currentUser.getEmail(), request);
		return ApiResult.ok(SuccessCode.USER_UPDATED, response);
	}

	@PatchMapping("/me/password")
	public ResponseEntity<ApiResponses<Void>> changePassword(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody PasswordChangeRequest request
	) {
		userService.changePassword(currentUser.getEmail(), request);
		return ApiResult.ok(SuccessCode.USER_PASSWORD_CHANGED);
	}

	@DeleteMapping("/me")
	public ResponseEntity<ApiResponses<Void>> deleteMyAccount(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		userService.deleteMyAccount(currentUser.getEmail());
		return ApiResult.ok(SuccessCode.USER_DELETED);
	}

}

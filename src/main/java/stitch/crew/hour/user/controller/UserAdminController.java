package stitch.crew.hour.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class UserAdminController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponses<UserInfoResponse>> getUserInfo(
		@PathVariable Long userId
	) {
		UserInfoResponse response = userService.getUserInfo(userId);
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

	@PatchMapping("/{userId}/role")
	public ResponseEntity<ApiResponses<UserInfoResponse>> updateUserRole(
		@PathVariable Long userId,
		@Valid @RequestBody UserRoleUpdateRequest request
	) {
		UserInfoResponse response = userService.updateUserRole(userId, request);
		return ApiResult.ok(SuccessCode.USER_ROLE_UPDATED, response);
	}
}

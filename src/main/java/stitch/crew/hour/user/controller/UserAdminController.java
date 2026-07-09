package stitch.crew.hour.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.dto.AdminUserDetailResponse;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;
import stitch.crew.hour.user.dto.UserBlacklistUpdateRequest;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.service.UserAdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class UserAdminController {

	private final UserAdminService userAdminService;

	@GetMapping
	public ResponseEntity<ApiResponses<Page<AdminUserSearchResponse>>> getUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Role role,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) Boolean blacklisted,
		@RequestParam(defaultValue = "false") Boolean includeDeleted
	) {
		Page<AdminUserSearchResponse> response = userAdminService.getUsers(
			page,
			size,
			keyword,
			role,
			gender,
			blacklisted,
			includeDeleted
		);
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponses<AdminUserDetailResponse>> getUserInfo(
		@PathVariable Long userId
	) {
		AdminUserDetailResponse response = userAdminService.getUser(userId);
		return ApiResult.ok(SuccessCode.USER_READ, response);
	}

	@PatchMapping("/{userId}/role")
	public ResponseEntity<ApiResponses<AdminUserDetailResponse>> updateUserRole(
		@PathVariable Long userId,
		@Valid @RequestBody UserRoleUpdateRequest request
	) {
		AdminUserDetailResponse response = userAdminService.updateUserRole(userId, request);
		return ApiResult.ok(SuccessCode.USER_ROLE_UPDATED, response);
	}

	@PatchMapping("/{userId}/blacklist")
	public ResponseEntity<ApiResponses<AdminUserDetailResponse>> updateBlacklist(
		@PathVariable Long userId,
		@Valid @RequestBody UserBlacklistUpdateRequest request
	) {
		AdminUserDetailResponse response = userAdminService.updateBlacklist(userId, request);
		return ApiResult.ok(SuccessCode.USER_UPDATED, response);
	}
}

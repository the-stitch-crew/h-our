package stitch.crew.hour.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.dto.UserInfoResponse;
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
}

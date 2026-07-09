package stitch.crew.hour.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.dto.AdminUserDetailResponse;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;
import stitch.crew.hour.user.dto.UserBlacklistUpdateRequest;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserAdminService;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserAdminController의")
class UserAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserAdminService userAdminService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserRepository userRepository;

	@Nested
	@DisplayName("Describe : GET /api/admin/users 엔드포인트는")
	class Describe_getUsers {

		@Test
		@DisplayName("It : 유저 목록을 반환한다")
		void it_returns_users() throws Exception {
			// given
			AdminUserSearchResponse user = new AdminUserSearchResponse(
				1L,
				"대정수",
				"legend@naver.com",
				"010-1234-5678",
				Role.USER.name(),
				Gender.MALE.name(),
				"KOREA",
				false,
				false,
				LocalDateTime.of(2026, 7, 9, 1, 0),
				null
			);
			Page<AdminUserSearchResponse> response = new PageImpl<>(List.of(user));

			given(userAdminService.getUsers(0, 20, null, null, null, null, false)).willReturn(response);

			// when & then
			mockMvc.perform(get("/api/admin/users"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_READ.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_READ.getSuccessMessage()))
				.andExpect(jsonPath("$.data.content[0].userId").value(1L))
				.andExpect(jsonPath("$.data.content[0].email").value("legend@naver.com"))
				.andExpect(jsonPath("$.data.content[0].blacklisted").value(false))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe : GET /api/admin/users/{userId} 엔드포인트는")
	class Describe_getUserInfo {

		@Test
		@DisplayName("It : 유저 정보를 반환한다")
		void it_returns_user_info() throws Exception {
			// given
			Long userId = 1L;
			AdminUserDetailResponse response = createDetailResponse(userId, Role.USER, false, null);

			given(userAdminService.getUser(userId)).willReturn(response);

			// when & then
			mockMvc.perform(get("/api/admin/users/{userId}", userId))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_READ.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_READ.getSuccessMessage()))
				.andExpect(jsonPath("$.data.userId").value(userId))
				.andExpect(jsonPath("$.data.userName").value("대정수"))
				.andExpect(jsonPath("$.data.email").value("legend@naver.com"))
				.andExpect(jsonPath("$.data.birthDate").value("2000-01-01"))
				.andExpect(jsonPath("$.data.gender").value(Gender.MALE.name()))
				.andExpect(jsonPath("$.data.role").value(Role.USER.name()))
				.andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
				.andExpect(jsonPath("$.data.nationality").value("KOREA"))
				.andExpect(jsonPath("$.data.isAuthLinked").value(false))
				.andExpect(jsonPath("$.data.blacklisted").value(false))
				.andDo(print());
		}

		@Test
		@DisplayName("It : 유저가 존재하지 않으면 404 상태를 반환한다")
		void it_returns_404_when_user_does_not_exist() throws Exception {
			// given
			Long userId = 1L;
			willThrow(new BusinessException(ErrorCode.USER_DONT_EXISTS))
				.given(userAdminService)
				.getUser(userId);

			// when & then
			mockMvc.perform(get("/api/admin/users/{userId}", userId))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(ErrorCode.USER_DONT_EXISTS.name()))
				.andExpect(jsonPath("$.message").value(ErrorCode.USER_DONT_EXISTS.getMessage()))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe : PATCH /api/admin/users/{userId}/role 엔드포인트는")
	class Describe_updateUserRole {

		@Test
		@DisplayName("It : 유저 역할을 변경하고 변경된 정보를 반환한다")
		void it_updates_user_role() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			AdminUserDetailResponse response = createDetailResponse(userId, Role.ADMIN, false, null);

			given(userAdminService.updateUserRole(userId, request)).willReturn(response);

			// when & then
			mockMvc.perform(
					patch("/api/admin/users/{userId}/role", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "role": "ADMIN"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_ROLE_UPDATED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_ROLE_UPDATED.getSuccessMessage()))
				.andExpect(jsonPath("$.data.userId").value(userId))
				.andExpect(jsonPath("$.data.role").value(Role.ADMIN.name()))
				.andDo(print());
		}

		@Test
		@DisplayName("It : 유저가 존재하지 않으면 404 상태를 반환한다")
		void it_returns_404_when_user_does_not_exist() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			willThrow(new BusinessException(ErrorCode.USER_DONT_EXISTS))
				.given(userAdminService)
				.updateUserRole(userId, request);

			// when & then
			mockMvc.perform(
					patch("/api/admin/users/{userId}/role", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "role": "ADMIN"
							}
							""")
				)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(ErrorCode.USER_DONT_EXISTS.name()))
				.andExpect(jsonPath("$.message").value(ErrorCode.USER_DONT_EXISTS.getMessage()))
				.andDo(print());
		}

		@Test
		@DisplayName("It : 변경할 수 없는 역할이면 400 상태를 반환한다")
		void it_returns_400_when_role_change_is_not_allowed() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.SUPER_ADMIN);
			willThrow(new BusinessException(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED))
				.given(userAdminService)
				.updateUserRole(userId, request);

			// when & then
			mockMvc.perform(
					patch("/api/admin/users/{userId}/role", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "role": "SUPER_ADMIN"
							}
							""")
				)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED.name()))
				.andExpect(jsonPath("$.message").value(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED.getMessage()))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe : PATCH /api/admin/users/{userId}/blacklist 엔드포인트는")
	class Describe_updateBlacklist {

		@Test
		@DisplayName("It : 유저 차단 상태를 변경하고 변경된 정보를 반환한다")
		void it_updates_user_blacklist() throws Exception {
			// given
			Long userId = 1L;
			UserBlacklistUpdateRequest request = new UserBlacklistUpdateRequest(true);
			AdminUserDetailResponse response = createDetailResponse(userId, Role.USER, true, null);

			given(userAdminService.updateBlacklist(userId, request)).willReturn(response);

			// when & then
			mockMvc.perform(
					patch("/api/admin/users/{userId}/blacklist", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "blacklisted": true
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_UPDATED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_UPDATED.getSuccessMessage()))
				.andExpect(jsonPath("$.data.userId").value(userId))
				.andExpect(jsonPath("$.data.blacklisted").value(true))
				.andDo(print());
		}
	}

	private AdminUserDetailResponse createDetailResponse(
		Long userId,
		Role role,
		Boolean blacklisted,
		LocalDateTime deletedAt
	) {
		return new AdminUserDetailResponse(
			userId,
			"대정수",
			"legend@naver.com",
			"010-1234-5678",
			LocalDate.of(2000, 1, 1),
			role.name(),
			Gender.MALE.name(),
			"KOREA",
			null,
			false,
			blacklisted,
			LocalDateTime.of(2026, 7, 9, 1, 0),
			LocalDateTime.of(2026, 7, 9, 1, 0),
			deletedAt
		);
	}
}

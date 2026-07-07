package stitch.crew.hour.user.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserAdminController의")
class UserAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserRepository userRepository;

	@Nested
	@DisplayName("Describe: GET /api/admin/users/{userId} 엔드포인트는")
	class Describe_getUserInfo {

		@Test
		@DisplayName("It: 유저 정보를 반환한다")
		void it_returns_user_info() throws Exception {
			// given
			Long userId = 1L;
			UserInfoResponse response = new UserInfoResponse(
				userId,
				"대정수",
				"legend@naver.com",
				LocalDate.of(2000, 1, 1),
				Gender.MALE,
				Role.USER,
				"010-1234-5678",
				"KOREA",
				false
			);

			given(userService.getUserInfo(userId)).willReturn(response);

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
				.andDo(print());
		}

		@Test
		@DisplayName("It: 유저가 존재하지 않으면 404 상태를 반환한다")
		void it_returns_404_when_user_does_not_exist() throws Exception {
			// given
			Long userId = 1L;
			willThrow(new BusinessException(ErrorCode.USER_DONT_EXISTS))
				.given(userService)
				.getUserInfo(userId);

			// when & then
			mockMvc.perform(get("/api/admin/users/{userId}", userId))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(ErrorCode.USER_DONT_EXISTS.name()))
				.andExpect(jsonPath("$.message").value(ErrorCode.USER_DONT_EXISTS.getMessage()))
				.andDo(print());
		}

		@Test
		@DisplayName("It: 삭제된 유저이면 400 상태를 반환한다")
		void it_returns_400_when_user_is_deleted() throws Exception {
			// given
			Long userId = 1L;
			willThrow(new BusinessException(ErrorCode.ALREADY_DELETED))
				.given(userService)
				.getUserInfo(userId);

			// when & then
			mockMvc.perform(get("/api/admin/users/{userId}", userId))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_DELETED.name()))
				.andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_DELETED.getMessage()))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe: PATCH /api/admin/users/{userId}/role 엔드포인트는")
	class Describe_updateUserRole {

		@Test
		@DisplayName("It: 유저 역할을 변경하고 변경된 정보를 반환한다")
		void it_updates_user_role() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			UserInfoResponse response = new UserInfoResponse(
				userId,
				"대정수",
				"legend@naver.com",
				LocalDate.of(2000, 1, 1),
				Gender.MALE,
				Role.ADMIN,
				"010-1234-5678",
				"KOREA",
				false
			);

			given(userService.updateUserRole(userId, request)).willReturn(response);

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
		@DisplayName("It: 유저가 존재하지 않으면 404 상태를 반환한다")
		void it_returns_404_when_user_does_not_exist() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			willThrow(new BusinessException(ErrorCode.USER_DONT_EXISTS))
				.given(userService)
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
		@DisplayName("It: 변경할 수 없는 역할이면 400 상태를 반환한다")
		void it_returns_400_when_role_change_is_not_allowed() throws Exception {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.SUPER_ADMIN);
			willThrow(new BusinessException(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED))
				.given(userService)
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
}

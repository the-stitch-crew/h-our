package stitch.crew.hour.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.dto.PasswordChangeRequest;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.UserUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController의")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserRepository userRepository;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Nested
	@DisplayName("Describe: GET /api/users/me 엔드포인트는")
	class Describe_getMyInfo {

		@Test
		@DisplayName("It: 인증된 사용자의 내 정보를 반환한다")
		void it_returns_my_info() throws Exception {
			// given
			String email = "legend@naver.com";
			UserInfoResponse response = new UserInfoResponse(
				1L,
				"대정수",
				email,
				LocalDate.of(2000, 1, 1),
				Gender.MALE,
				Role.USER,
				"010-1234-5678",
				"KOREA",
				false
			);
			TestingAuthenticationToken authentication = createAuthentication(email);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			given(userService.getMyInfo(email)).willReturn(response);

			// when & then
			mockMvc.perform(
					get("/api/users/me")
						.principal(authentication)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_READ.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_READ.getSuccessMessage()))
				.andExpect(jsonPath("$.data.userId").value(1L))
				.andExpect(jsonPath("$.data.userName").value("대정수"))
				.andExpect(jsonPath("$.data.email").value(email))
				.andExpect(jsonPath("$.data.birthDate").value("2000-01-01"))
				.andExpect(jsonPath("$.data.gender").value(Gender.MALE.name()))
				.andExpect(jsonPath("$.data.role").value(Role.USER.name()))
				.andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
				.andExpect(jsonPath("$.data.nationality").value("KOREA"))
				.andExpect(jsonPath("$.data.isAuthLinked").value(false))
				.andDo(print());

			verify(userService).getMyInfo(email);
		}
	}

	@Nested
	@DisplayName("Describe: PATCH /api/users/me 엔드포인트는")
	class Describe_updateMyInfo {

		@Test
		@DisplayName("It: 인증된 사용자의 정보를 수정하고 반환한다")
		void it_updates_my_info() throws Exception {
			// given
			String email = "legend@naver.com";
			UserInfoResponse response = new UserInfoResponse(
				1L,
				"정수",
				email,
				LocalDate.of(2000, 1, 1),
				Gender.MALE,
				Role.USER,
				"010-1111-2222",
				"KOREA",
				false
			);

			given(userService.updateMyInfo(eq(email), any(UserUpdateRequest.class))).willReturn(response);
			SecurityContextHolder.getContext().setAuthentication(createAuthentication(email));

			// when & then
			mockMvc.perform(
					patch("/api/users/me")
						.principal(createAuthentication(email))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "userName": "정수",
							  "phoneNumber": "010-1111-2222"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_UPDATED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_UPDATED.getSuccessMessage()))
				.andExpect(jsonPath("$.data.userName").value("정수"))
				.andExpect(jsonPath("$.data.phoneNumber").value("010-1111-2222"))
				.andDo(print());

			verify(userService).updateMyInfo(eq(email), any(UserUpdateRequest.class));
		}
	}

	@Nested
	@DisplayName("Describe: PATCH /api/users/me/password 엔드포인트는")
	class Describe_changePassword {

		@Test
		@DisplayName("It: 인증된 사용자의 비밀번호를 변경한다")
		void it_changes_password() throws Exception {
			// given
			String email = "legend@naver.com";
			doNothing().when(userService).changePassword(eq(email), any(PasswordChangeRequest.class));
			SecurityContextHolder.getContext().setAuthentication(createAuthentication(email));

			// when & then
			mockMvc.perform(
					patch("/api/users/me/password")
						.principal(createAuthentication(email))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "currentPassword": "password123",
							  "newPassword": "newPassword123"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_PASSWORD_CHANGED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_PASSWORD_CHANGED.getSuccessMessage()))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andDo(print());

			verify(userService).changePassword(eq(email), any(PasswordChangeRequest.class));
		}
	}

	@Nested
	@DisplayName("Describe: DELETE /api/users/me 엔드포인트는")
	class Describe_deleteMyAccount {

		@Test
		@DisplayName("It: 인증된 사용자를 탈퇴 처리한다")
		void it_deletes_my_account() throws Exception {
			// given
			String email = "legend@naver.com";
			doNothing().when(userService).deleteMyAccount(email);
			SecurityContextHolder.getContext().setAuthentication(createAuthentication(email));

			// when & then
			mockMvc.perform(
					delete("/api/users/me")
						.principal(createAuthentication(email))
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_DELETED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_DELETED.getSuccessMessage()))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andDo(print());

			verify(userService).deleteMyAccount(email);
		}
	}

	private TestingAuthenticationToken createAuthentication(String email) {
		return new TestingAuthenticationToken(
			new CurrentUser(1L, email, Role.USER),
			null,
			"ROLE_USER"
		);
	}
}

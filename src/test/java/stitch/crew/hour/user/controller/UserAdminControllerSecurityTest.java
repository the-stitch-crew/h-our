package stitch.crew.hour.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.dto.AdminUserDetailResponse;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserAdminService;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserAdminController.class)
@Import({
	SecurityConfig.class,
	JwtAuthenticationFilter.class,
	JwtAuthenticationEntryPoint.class,
	JwtAccessDeniedHandler.class
})
@AutoConfigureMockMvc
@DisplayName("UserAdminController 보안 설정은")
class UserAdminControllerSecurityTest {

	private static final String BASE_URL = "/api/admin/users";

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

	@Test
	@DisplayName("It : 인증이 없으므로 회원 목록 조회 시 401을 반환")
	void it_rejects_get_users_without_authentication() throws Exception {
		mockMvc.perform(get(BASE_URL))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 회원 목록 조회 시 403을 반환")
	void it_rejects_get_users_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 회원 목록을 조회")
	void it_returns_get_users_with_admin_authority() throws Exception {
		String token = "valid-admin-token";
		Page<AdminUserSearchResponse> response = new PageImpl<>(List.of());

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(userAdminService.getUsers(anyInt(), anyInt(), any(), any(), any(), any(), any())).willReturn(response);

		mockMvc.perform(
				get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.USER_READ.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.USER_READ.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 회원 상세 조회 시 401을 반환")
	void it_rejects_get_user_info_without_authentication() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{userId}", 1L))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 회원 상세 조회 시 403을 반환")
	void it_rejects_get_user_info_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				get(BASE_URL + "/{userId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 회원 상세를 조회")
	void it_returns_get_user_info_with_admin_authority() throws Exception {
		String token = "valid-admin-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(userAdminService.getUser(1L)).willReturn(detailResponse(Role.USER, false));

		mockMvc.perform(
				get(BASE_URL + "/{userId}", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.USER_READ.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.USER_READ.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 회원 권한 변경 시 401을 반환")
	void it_rejects_update_user_role_without_authentication() throws Exception {
		mockMvc.perform(
				patch(BASE_URL + "/{userId}/role", 1L)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "role": "ADMIN"
						}
						""")
			)
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 회원 권한 변경 시 403을 반환")
	void it_rejects_update_user_role_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				patch(BASE_URL + "/{userId}/role", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "role": "ADMIN"
						}
						""")
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 회원 권한을 변경")
	void it_returns_update_user_role_with_admin_authority() throws Exception {
		String token = "valid-admin-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(userAdminService.updateUserRole(eq(1L), any())).willReturn(detailResponse(Role.ADMIN, false));

		mockMvc.perform(
				patch(BASE_URL + "/{userId}/role", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "role": "ADMIN"
						}
						""")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.USER_ROLE_UPDATED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.USER_ROLE_UPDATED.getSuccessMessage()));
	}

	@Test
	@DisplayName("It : 인증이 없으므로 회원 차단 상태 변경 시 401을 반환")
	void it_rejects_update_blacklist_without_authentication() throws Exception {
		mockMvc.perform(
				patch(BASE_URL + "/{userId}/blacklist", 1L)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "blacklisted": true
						}
						""")
			)
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한이 없으므로 회원 차단 상태 변경 시 403을 반환")
	void it_rejects_update_blacklist_without_admin_authority() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("user@test.com"));
		given(userService.loadCurrentUserByEmail("user@test.com"))
			.willReturn(new CurrentUser(1L, "user@test.com", Role.USER));

		mockMvc.perform(
				patch(BASE_URL + "/{userId}/blacklist", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "blacklisted": true
						}
						""")
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("권한이 없습니다."));
	}

	@Test
	@DisplayName("It : 어드민 권한으로 회원 차단 상태를 변경")
	void it_returns_update_blacklist_with_admin_authority() throws Exception {
		String token = "valid-admin-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("admin@test.com"));
		given(userService.loadCurrentUserByEmail("admin@test.com"))
			.willReturn(new CurrentUser(1L, "admin@test.com", Role.ADMIN));
		given(userAdminService.updateBlacklist(eq(1L), any())).willReturn(detailResponse(Role.USER, true));

		mockMvc.perform(
				patch(BASE_URL + "/{userId}/blacklist", 1L)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "blacklisted": true
						}
						""")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(SuccessCode.USER_UPDATED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.USER_UPDATED.getSuccessMessage()));
	}

	private AdminUserDetailResponse detailResponse(
		Role role,
		Boolean blacklisted
	) {
		return new AdminUserDetailResponse(
			1L,
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
			null
		);
	}
}

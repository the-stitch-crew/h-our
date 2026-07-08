package stitch.crew.hour.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.repository.UserRepository;
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

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserRepository userRepository;

	@Test
	@DisplayName("GET /api/admin/users/{userId} 요청에 인증이 없으면 401을 반환한다")
	void it_rejects_get_user_info_without_authentication() throws Exception {
		mockMvc.perform(get("/api/admin/users/{userId}", 1L))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
			.andExpect(jsonPath("$.message").value("계정 인증이 필요합니다."));
	}

	@Test
	@DisplayName("PATCH /api/admin/users/{userId}/role 요청에 USER 권한이면 403을 반환한다")
	void it_rejects_update_user_role_without_authentication() throws Exception {
		String token = "valid-user-token";

		given(jwtTokenProvider.validate(token)).willReturn(true);
		given(jwtTokenProvider.parseJwt(token)).willReturn(new TokenBody("daeint@text.com"));
		given(userService.loadCurrentUserByEmail("daeint@text.com"))
			.willReturn(new CurrentUser(1L, "daeint@text.com", Role.USER));

		mockMvc.perform(
				patch("/api/admin/users/{userId}/role", 1L)
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
}

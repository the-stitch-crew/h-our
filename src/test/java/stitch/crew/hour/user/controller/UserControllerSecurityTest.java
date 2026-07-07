package stitch.crew.hour.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
@DisplayName("UserController 보안 설정은")
class UserControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private UserRepository userRepository;

	@Test
	@DisplayName("GET /api/users/me 요청에 인증이 없으면 차단한다")
	void it_rejects_get_my_info_without_authentication() throws Exception {
		mockMvc.perform(get("/api/users/me"))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("PATCH /api/users/me 요청에 인증이 없으면 차단한다")
	void it_rejects_update_my_info_without_authentication() throws Exception {
		mockMvc.perform(
				patch("/api/users/me")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}")
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("PATCH /api/users/me/password 요청에 인증이 없으면 차단한다")
	void it_rejects_change_password_without_authentication() throws Exception {
		mockMvc.perform(
				patch("/api/users/me/password")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
						  "currentPassword": "password123",
						  "newPassword": "newPassword123"
						}
						""")
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("DELETE /api/users/me 요청에 인증이 없으면 차단한다")
	void it_rejects_delete_my_account_without_authentication() throws Exception {
		mockMvc.perform(delete("/api/users/me"))
			.andExpect(status().is4xxClientError());
	}
}

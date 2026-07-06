package stitch.crew.hour.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserAdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
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
	@DisplayName("GET /api/admin/users/{userId} 요청에 인증이 없으면 차단한다")
	void it_rejects_get_user_info_without_authentication() throws Exception {
		mockMvc.perform(get("/api/admin/users/{userId}", 1L))
			.andExpect(status().is4xxClientError());
	}
}

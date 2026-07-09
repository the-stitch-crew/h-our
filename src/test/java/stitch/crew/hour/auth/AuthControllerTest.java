package stitch.crew.hour.auth;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.auth.controller.AuthController;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.auth.dto.OAuthSignupRequest;
import stitch.crew.hour.auth.dto.RefreshTokenRequest;
import stitch.crew.hour.auth.service.AuthService;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.response.SuccessCode;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController의")
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Nested
	@DisplayName("Describe: POST /api/auth/login 엔드포인트는")
	class Describe_login {

		@Test
		@DisplayName("It: 로그인 성공 시 200 상태와 토큰 정보를 반환한다")
		void it_returns_200_ok_and_tokens() throws Exception {
			// given
			LoginRequest request = new LoginRequest("legend@naver.com", "password123");
			KeyPair response = new KeyPair(
				"access-token",
				"refresh-token"
			);

			given(authService.login(request)).willReturn(response);

			// when & then
			mockMvc.perform(
					post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "email": "legend@naver.com",
							  "password": "password123"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.AUTH_LOGIN_SUCCESS.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.AUTH_LOGIN_SUCCESS.getSuccessMessage()))
				.andExpect(jsonPath("$.data.accessToken").value("access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe: POST /api/auth/oauth/signup 엔드포인트는")
	class Describe_oauthSignup {

		@Test
		@DisplayName("It: OAuth 회원가입 성공 시 201 상태와 토큰 정보를 반환한다")
		void it_returns_201_created_and_tokens() throws Exception {
			// given
			OAuthSignupRequest request = new OAuthSignupRequest(
				"signup-token",
				java.time.LocalDate.of(2000, 1, 1),
				stitch.crew.hour.user.constant.Gender.MALE,
				"010-9999-8888",
				"KOREA"
			);
			KeyPair response = new KeyPair(
				"oauth-access-token",
				"oauth-refresh-token"
			);

			given(authService.oauthSignup(request)).willReturn(response);

			// when & then
			mockMvc.perform(
					post("/api/auth/oauth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "signupToken": "signup-token",
							  "birthDate": "2000-01-01",
							  "gender": "MALE",
							  "phoneNumber": "010-9999-8888",
							  "nationality": "KOREA"
							}
							""")
				)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.USER_CREATED.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.USER_CREATED.getSuccessMessage()))
				.andExpect(jsonPath("$.data.accessToken").value("oauth-access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("oauth-refresh-token"))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe: POST /api/auth/refresh 엔드포인트는")
	class Describe_refresh {

		@Test
		@DisplayName("It: 토큰 갱신 성공 시 200 상태와 새 토큰 정보를 반환한다")
		void it_returns_200_ok_and_new_tokens() throws Exception {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			KeyPair response = new KeyPair(
				"new-access-token",
				"new-refresh-token"
			);

			given(authService.refresh(request)).willReturn(response);

			// when & then
			mockMvc.perform(
					post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "refreshToken": "refresh-token"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.AUTH_REFRESH_SUCCESS.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.AUTH_REFRESH_SUCCESS.getSuccessMessage()))
				.andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
				.andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("Describe: POST /api/auth/logout 엔드포인트는")
	class Describe_logout {

		@Test
		@DisplayName("It: 로그아웃 성공 시 200 상태를 반환한다")
		void it_returns_200_ok() throws Exception {
			// when & then
			mockMvc.perform(
					post("/api/auth/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
							{
							  "refreshToken": "refresh-token"
							}
							""")
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value(SuccessCode.AUTH_LOGOUT_SUCCESS.name()))
				.andExpect(jsonPath("$.message").value(SuccessCode.AUTH_LOGOUT_SUCCESS.getSuccessMessage()))
				.andExpect(jsonPath("$.data").doesNotExist())
				.andDo(print());
		}
	}
}

package stitch.crew.hour.user.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController의")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

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
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(
					email,
					null,
					List.of(new SimpleGrantedAuthority("ROLE_USER"))
				);

			given(userService.getMyInfo(email)).willReturn(response);

			// when & then
			mockMvc.perform(
					get("/api/users/me")
						.with(authentication(authentication))
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
}

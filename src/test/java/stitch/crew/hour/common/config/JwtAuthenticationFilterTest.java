package stitch.crew.hour.common.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter의")
class JwtAuthenticationFilterTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private Jws<Claims> claimsJws;

	@Mock
	private Claims claims;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Nested
	@DisplayName("Describe: doFilterInternal 메서드는")
	class Describe_doFilterInternal {

		@Test
		@DisplayName("It: 유효한 Bearer 토큰이면 SecurityContext에 인증 정보를 저장한다")
		void it_sets_authentication_when_token_is_valid() throws Exception {
			// given
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			MockFilterChain filterChain = new MockFilterChain();
			String token = "valid-token";

			request.addHeader("Authorization", "Bearer " + token);
			given(jwtTokenProvider.validate(token)).willReturn(true);
			given(jwtTokenProvider.parseClaims(token)).willReturn(claimsJws);
			given(claimsJws.getPayload()).willReturn(claims);
			given(claims.get("email")).willReturn("legend@naver.com");
			given(claims.get("role")).willReturn("USER");

			// when
			filter.doFilter(request, response, filterChain);

			// then
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			assertThat(authentication.getName()).isEqualTo("legend@naver.com");
			assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
		}

		@Test
		@DisplayName("It: Authorization 헤더가 없으면 인증 정보를 만들지 않는다")
		void it_does_not_set_authentication_without_authorization_header() throws Exception {
			// given
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			MockFilterChain filterChain = new MockFilterChain();

			// when
			filter.doFilter(request, response, filterChain);

			// then
			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(jwtTokenProvider, never()).validate("valid-token");
		}

		@Test
		@DisplayName("It: 잘못된 토큰이면 예외가 발생하고 인증 정보를 만들지 않는다")
		void it_throws_exception_when_token_is_invalid() {
			// given
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			MockFilterChain filterChain = new MockFilterChain();
			String token = "invalid-token";

			request.addHeader("Authorization", "Bearer " + token);
			given(jwtTokenProvider.validate(token))
				.willThrow(new BusinessException(ErrorCode.ERROR_FROM_TOKEN));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> filter.doFilter(request, response, filterChain)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ERROR_FROM_TOKEN);
			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		}
	}
}

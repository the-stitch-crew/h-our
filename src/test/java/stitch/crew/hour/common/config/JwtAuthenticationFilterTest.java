package stitch.crew.hour.common.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

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
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter의")
class JwtAuthenticationFilterTest {

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private UserRepository userRepository;

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
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			MockFilterChain filterChain = new MockFilterChain();
			String token = "valid-token";
			User user = createUser();

			request.addHeader("Authorization", "Bearer " + token);
			given(jwtTokenProvider.validate(token)).willReturn(true);
			given(jwtTokenProvider.parseClaims(token)).willReturn(claimsJws);
			given(claimsJws.getPayload()).willReturn(claims);
			given(claims.get("email")).willReturn(user.getEmail());
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			filter.doFilter(request, response, filterChain);

			// then
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			assertThat(authentication.getName()).isEqualTo("legend@naver.com");
			assertThat(authentication.getPrincipal()).isInstanceOf(CurrentUser.class);
			assertThat(((CurrentUser)authentication.getPrincipal()).getId()).isEqualTo(1L);
			assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
		}

		@Test
		@DisplayName("It: Authorization 헤더가 없으면 인증 정보를 만들지 않는다")
		void it_does_not_set_authentication_without_authorization_header() throws Exception {
			// given
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			MockFilterChain filterChain = new MockFilterChain();

			// when
			filter.doFilter(request, response, filterChain);

			// then
			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(jwtTokenProvider, never()).validate("valid-token");
			verify(userRepository, never()).findByEmail("legend@naver.com");
		}

		@Test
		@DisplayName("It: 잘못된 토큰이면 예외가 발생하고 인증 정보를 만들지 않는다")
		void it_throws_exception_when_token_is_invalid() {
			// given
			JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
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
			verify(userRepository, never()).findByEmail("legend@naver.com");
		}
	}

	private User createUser() {
		User user = new User(
			"대정수",
			"legend@naver.com",
			"encodedPassword",
			LocalDate.of(2000, 1, 1),
			Role.USER,
			Gender.MALE,
			null,
			"010-1234-5678",
			"KOREA",
			false,
			false
		);
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}
}

package stitch.crew.hour.auth.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Value("${app.frontend.base-url:http://localhost:5173}")
	private String frontendBaseUrl;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {

		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		OAuth2User oauthUser = oauthToken.getPrincipal();

		String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase(Locale.ROOT);
		String email = oauthUser.getAttribute("email");
		String name = oauthUser.getAttribute("name");

		if (email == null) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED, "OAuth2 사용자 정보에 필수 값이 없습니다.");
		}

		// 이메일 기반으로 찾아보고, 있으면 기존 계정으로 로그인
		Optional<User> user = userRepository.findByEmail(email)
			.filter(foundUser -> foundUser.getDeletedAt() == null);

		if (user.isPresent()) {
			KeyPair keyPair = setOAuthAndIssueToken(user.get(), provider);
			writeLoginResponse(response, keyPair);
			return;
		}

		response.sendRedirect(createOAuthSignupRedirectUrl(email, name, provider));

	}

	// 기존 회원일때 실행할 메서드 (토큰 발행, DB수정)
	private KeyPair setOAuthAndIssueToken(
		User user,
		String provider
	) {
		user.setOAuth(provider);
		userRepository.save(user);
		return jwtTokenProvider.issueKeyPair(user.getEmail(), user.getRole());
	}

	// 기존 회원일 시 로그인 응답 보내기
	private void writeLoginResponse(
		HttpServletResponse response,
		KeyPair keyPair
	) throws IOException {
		ApiResponses<KeyPair> body = new ApiResponses<>(
			true,
			SuccessCode.AUTH_LOGIN_SUCCESS.name(),
			SuccessCode.AUTH_LOGIN_SUCCESS.getSuccessMessage(),
			keyPair
		);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		objectMapper.writeValue(response.getWriter(), body);
	}


	// 신규회원
	private String createOAuthSignupRedirectUrl(
		String email,
		String name,
		String provider
	) {
		return UriComponentsBuilder.fromUriString(frontendBaseUrl+"/signup")
			.queryParam("oauth", true)
			.queryParam("email", email)
			.queryParam("name", name)
			.queryParam("provider", provider)
			.build()
			.encode()
			.toUriString();
	}
}

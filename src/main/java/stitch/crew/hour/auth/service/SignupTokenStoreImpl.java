package stitch.crew.hour.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.domain.SignupToken;
import stitch.crew.hour.auth.dto.OAuthSignupPayload;
import stitch.crew.hour.auth.repository.OAuthSignupTokenRepository;

@Service
@RequiredArgsConstructor
public class SignupTokenStoreImpl implements SignupTokenStore {

	private static final int TOKEN_BYTES = 32;

	private final OAuthSignupTokenRepository oauthSignupTokenRepository;
	private final SecureRandom secureRandom = new SecureRandom();

	@Value("${custom.oauth.signup-token-ttl-ms:600000}")
	private long signupTokenTtlMillis = 600000L;

	@Override
	@Transactional
	public String save(OAuthSignupPayload payload) {
		String token = generateToken();
		LocalDateTime expiresAt = LocalDateTime.now().plusNanos(signupTokenTtlMillis * 1_000_000);

		oauthSignupTokenRepository.save(
			new SignupToken(
				token,
				payload.email(),
				payload.userName(),
				payload.provider(),
				expiresAt
			)
		);

		return token;
	}

	@Override
	@Transactional
	public Optional<OAuthSignupPayload> find(String signupToken) {
		return oauthSignupTokenRepository.findByTokenForUpdate(signupToken)
			.flatMap(oauthSignupToken -> {
				if (oauthSignupToken.isExpired(LocalDateTime.now())) {
					oauthSignupTokenRepository.delete(oauthSignupToken);
					return Optional.empty();
				}

				return Optional.of(oauthSignupToken.toPayload());
			});
	}

	@Override
	@Transactional
	public void delete(String signupToken) {
		oauthSignupTokenRepository.deleteById(signupToken);
	}

	private String generateToken() {
		byte[] bytes = new byte[TOKEN_BYTES];
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder()
			.withoutPadding()
			.encodeToString(bytes);
	}
}

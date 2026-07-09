package stitch.crew.hour.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import stitch.crew.hour.auth.domain.SignupToken;
import stitch.crew.hour.auth.dto.OAuthSignupPayload;
import stitch.crew.hour.auth.repository.SignupTokenRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaOAuthSignupTokenStore의")
class JpaSignupTokenStoreImplTest {

	@InjectMocks
	private SignupTokenStoreImpl oauthSignupTokenStoreImpl;

	@Mock
	private SignupTokenRepository oauthSignupTokenRepository;

	@Nested
	@DisplayName("Describe: save 메서드는")
	class Describe_save {

		@Test
		@DisplayName("It: payload를 만료시간이 있는 opaque token으로 저장한다")
		void it_saves_payload_with_expiration() {
			// given
			ReflectionTestUtils.setField(oauthSignupTokenStoreImpl, "signupTokenTtlMillis", 600000L);
			OAuthSignupPayload payload = createPayload("GOOGLE");
			LocalDateTime beforeSave = LocalDateTime.now();

			// when
			String token = oauthSignupTokenStoreImpl.save(payload);

			// then
			assertThat(token).isNotBlank();

			ArgumentCaptor<SignupToken> tokenCaptor =
				ArgumentCaptor.forClass(SignupToken.class);
			verify(oauthSignupTokenRepository).save(tokenCaptor.capture());

			SignupToken savedToken = tokenCaptor.getValue();
			assertThat(savedToken.getToken()).isEqualTo(token);
			assertThat(savedToken.getEmail()).isEqualTo(payload.email());
			assertThat(savedToken.getUserName()).isEqualTo(payload.userName());
			assertThat(savedToken.getProvider()).isEqualTo(payload.provider());
			assertThat(savedToken.getExpiresAt()).isAfter(beforeSave);
		}
	}

	@Nested
	@DisplayName("Describe: find 메서드는")
	class Describe_find {

		@Test
		@DisplayName("It: 유효한 token이면 payload를 반환한다")
		void it_returns_payload_when_valid() {
			// given
			SignupToken signupToken = new SignupToken(
				"signup-token",
				"oauth@google.com",
				"OAuth User",
				"GOOGLE",
				LocalDateTime.now().plusMinutes(10)
			);
			given(oauthSignupTokenRepository.findByToken("signup-token"))
				.willReturn(Optional.of(signupToken));

			// when
			Optional<OAuthSignupPayload> payload = oauthSignupTokenStoreImpl.find("signup-token");

			// then
			assertThat(payload).isPresent();
			assertThat(payload.get().email()).isEqualTo("oauth@google.com");
			assertThat(payload.get().userName()).isEqualTo("OAuth User");
			assertThat(payload.get().provider()).isEqualTo("GOOGLE");
		}

		@Test
		@DisplayName("It: 만료된 token이면 삭제하고 빈 값을 반환한다")
		void it_deletes_and_returns_empty_when_expired() {
			// given
			SignupToken signupToken = new SignupToken(
				"signup-token",
				"oauth@google.com",
				"OAuth User",
				"GOOGLE",
				LocalDateTime.now().minusSeconds(1)
			);
			given(oauthSignupTokenRepository.findByToken("signup-token"))
				.willReturn(Optional.of(signupToken));

			// when
			Optional<OAuthSignupPayload> payload = oauthSignupTokenStoreImpl.find("signup-token");

			// then
			assertThat(payload).isEmpty();
			verify(oauthSignupTokenRepository).delete(signupToken);
		}

		@Test
		@DisplayName("It: 저장된 token이 없으면 빈 값을 반환한다")
		void it_returns_empty_when_not_found() {
			// given
			given(oauthSignupTokenRepository.findByToken("signup-token")).willReturn(Optional.empty());

			// when
			Optional<OAuthSignupPayload> payload = oauthSignupTokenStoreImpl.find("signup-token");

			// then
			assertThat(payload).isEmpty();
			verify(oauthSignupTokenRepository).findByToken("signup-token");
		}
	}

	@Nested
	@DisplayName("Describe: delete 메서드는")
	class Describe_delete {

		@Test
		@DisplayName("It: token을 삭제한다")
		void it_deletes_token() {
			// when
			oauthSignupTokenStoreImpl.delete("signup-token");

			// then
			verify(oauthSignupTokenRepository).deleteById("signup-token");
		}
	}

	private OAuthSignupPayload createPayload(String provider) {
		return new OAuthSignupPayload(
			"oauth@google.com",
			"OAuth User",
			provider
		);
	}
}

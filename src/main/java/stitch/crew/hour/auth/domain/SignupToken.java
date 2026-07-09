package stitch.crew.hour.auth.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.auth.dto.OAuthSignupPayload;

@Entity
@Getter
@Table(name = "signup_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupToken {

	@Id
	@Column(length = 64)
	private String token;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String userName;

	@Column(nullable = false)
	private String provider;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	public SignupToken(
		String token,
		String email,
		String userName,
		String provider,
		LocalDateTime expiresAt
	) {
		this.token = token;
		this.email = email;
		this.userName = userName;
		this.provider = provider;
		this.expiresAt = expiresAt;
	}

	public boolean isExpired(LocalDateTime now) {
		return !expiresAt.isAfter(now);
	}

	public OAuthSignupPayload toPayload() {
		return new OAuthSignupPayload(email, userName, provider);
	}
}

package stitch.crew.hour.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.common.config.properties.JwtProperties;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.auth.domain.RefreshToken;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.user.constant.Role;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtProperties jwtProperties;

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(
			jwtProperties.getSecrets().getAppKey().getBytes(StandardCharsets.UTF_8)
		);
	}

	private String issueRefreshToken(
		String email
	) {
		String issuedRefreshToken = Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(jwtProperties.getPayload().getSubjectRefreshToken())
			.claim("email", email)
			.issuedAt(new Date())
			.expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getRefresh()))
			.signWith(getSecretKey())
			.compact();

		refreshTokenRepository.save(
			RefreshToken.builder()
				.refreshToken(issuedRefreshToken)
				.email(email)
				.build()
		);

		return issuedRefreshToken;
	}

	private String issueAccessToken(
		String email,
		Role role
	) {
		return Jwts.builder()
			.subject(jwtProperties.getPayload().getSubjectAccessToken())
			.claim("role", role.name())
			.issuer(jwtProperties.getPayload().getIssuer())
			.claim("email", email)
			.issuedAt(new Date())
			.expiration(new Date(new Date().getTime() + jwtProperties.getValidations().getAccess()))
			.signWith(getSecretKey())
			.compact();
	}

	public KeyPair issueKeyPair(
		String email,
		Role role
	) {
		return new KeyPair(
			issueAccessToken(email, role),
			issueRefreshToken(email)
		);
	}

	public boolean validate(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSecretKey())
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
		} catch (MalformedJwtException e) {
			throw new BusinessException(ErrorCode.ABNORMAL_TOKEN);
		} catch (JwtException e) {
			throw new BusinessException(ErrorCode.ERROR_FROM_TOKEN);
		}
	}

	public Jws<Claims> parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSecretKey())
			.build()
			.parseSignedClaims(token);
	}

	public void validateRefreshToken(String token) {
		validate(token);

		String subject = parseClaims(token).getPayload().getSubject();
		if (!jwtProperties.getPayload().getSubjectRefreshToken().equals(subject)) {
			throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	public TokenBody parseJwt(String token) {
		Jws<Claims> claimsJws = parseClaims(token);
		return new TokenBody(String.valueOf(claimsJws.getPayload().get("email")));
	}
}

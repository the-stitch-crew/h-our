package stitch.crew.hour.common.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.service.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String ROLE_PREFIX = "ROLE_";

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

		if (!hasBearerToken(authorizationHeader)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length());
		jwtTokenProvider.validate(token);

		Jws<Claims> claims = jwtTokenProvider.parseClaims(token);
		String email = String.valueOf(claims.getPayload().get("email"));
		String role = String.valueOf(claims.getPayload().get("role"));

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				email,
				null,
				List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role))
			);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}

	private boolean hasBearerToken(String authorizationHeader) {
		return authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX);
	}
}

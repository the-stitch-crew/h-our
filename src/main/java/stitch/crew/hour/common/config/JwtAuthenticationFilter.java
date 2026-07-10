package stitch.crew.hour.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.service.JwtTokenProvider;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String extractedToken = extractToken(request);

		if (extractedToken == null){
			filterChain.doFilter(request,response);
			return;
		}

		try {
			if (jwtTokenProvider.validate(extractedToken)) {
				TokenBody tokenBody = jwtTokenProvider.parseJwt(extractedToken);
				CurrentUser currentUser = userService.loadCurrentUserByEmail(tokenBody.getEmail());
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					currentUser,
					null,
					currentUser.getAuthorities()
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (BusinessException exception) {
			SecurityContextHolder.clearContext();
			writeErrorResponse(response, resolveAuthenticationErrorCode(exception.getErrorCode()));
			return;
		}

		filterChain.doFilter(request,response);
	}

	private ErrorCode resolveAuthenticationErrorCode(ErrorCode errorCode) {
		if (errorCode == ErrorCode.USER_DONT_EXISTS) {
			return ErrorCode.UNAUTHORIZED;
		}
		return errorCode;
	}

	private void writeErrorResponse(
		HttpServletResponse response,
		ErrorCode errorCode
	) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("""
			{
			  "success": false,
			  "code": "%s",
			  "message": "%s",
			  "data": null
			}
			""".formatted(errorCode.name(), errorCode.getMessage()));
	}

	public String extractToken(HttpServletRequest request){
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
		return null;
	}
}

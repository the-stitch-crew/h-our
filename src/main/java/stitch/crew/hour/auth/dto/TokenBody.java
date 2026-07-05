package stitch.crew.hour.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenBody {

	private final String email;

	@Builder
	public TokenBody(String email) {
		this.email = email;
	}
}

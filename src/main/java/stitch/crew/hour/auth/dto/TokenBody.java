package stitch.crew.hour.auth.dto;

import lombok.Getter;

@Getter
public class TokenBody {

	private final String email;

	public TokenBody(String email) {
		this.email = email;
	}
}

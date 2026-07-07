package stitch.crew.hour.user.domain;

import java.security.Principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.user.constant.Role;

@Getter
@RequiredArgsConstructor
public class CurrentUser implements Principal {

	private final Long id;
	private final String email;
	private final Role role;

	public static CurrentUser from(User user) {
		return new CurrentUser(
			user.getId(),
			user.getEmail(),
			user.getRole()
		);
	}

	@Override
	public String getName() {
		return email;
	}
}

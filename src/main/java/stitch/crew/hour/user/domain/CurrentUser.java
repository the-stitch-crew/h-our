package stitch.crew.hour.user.domain;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.user.constant.Role;

@Getter
@RequiredArgsConstructor
public class CurrentUser implements UserDetails {

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
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(
			new SimpleGrantedAuthority(role.getValue())
		);
	}

	@Override
	public @Nullable String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return email;
	}
}

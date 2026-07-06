package stitch.crew.hour.user.constant;

import lombok.Getter;

@Getter
public enum Role {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN"),
	SUPER_ADMIN("ROLE_SUPER_ADMIN");

	private final String value;

	Role(String value){
		this.value = value;
	}
}

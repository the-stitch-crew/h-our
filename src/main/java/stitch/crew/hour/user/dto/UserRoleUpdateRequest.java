package stitch.crew.hour.user.dto;

import jakarta.validation.constraints.NotNull;
import stitch.crew.hour.user.constant.Role;

public record UserRoleUpdateRequest(

	@NotNull(message = "역할은 필수입니다.")
	Role role

) {
}

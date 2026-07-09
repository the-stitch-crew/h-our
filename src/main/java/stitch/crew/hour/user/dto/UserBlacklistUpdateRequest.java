package stitch.crew.hour.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserBlacklistUpdateRequest(

	@NotNull(message = "차단 여부는 필수입니다.")
	Boolean blacklisted

) {
}

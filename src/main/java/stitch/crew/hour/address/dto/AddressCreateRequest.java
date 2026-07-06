package stitch.crew.hour.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressCreateRequest(
	@NotBlank(message = "우편번호는 필수값입니다.")
	@Size(max = 20, message = "우편번호는 최대 20자입니다.")
	String zipCode,

	@Size(max = 255, message = "지번 주소는 최대 255자입니다.")
	String oldAddress,

	@NotBlank(message = "도로명 주소는 필수값입니다.")
	@Size(max = 255, message = "도로명 주소는 최대 255자입니다.")
	String roadAddress,

	@NotBlank(message = "상세주소는 필수값입니다.")
	@Size(max = 255, message = "상세주소는 최대 255자입니다.")
	String addressDetail,

	Boolean isMain
) {
}

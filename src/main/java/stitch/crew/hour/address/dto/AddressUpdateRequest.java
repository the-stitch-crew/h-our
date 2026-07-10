package stitch.crew.hour.address.dto;

import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
	@Size(min = 1, max = 20, message = "우편번호는 1자 이상 20자 이하입니다.")
	String zipCode,

	@Size(max = 255, message = "지번 주소는 최대 255자입니다.")
	String oldAddress,

	@Size(min = 1, max = 255, message = "도로명 주소는 1자 이상 255자 이하입니다.")
	String roadAddress,

	@Size(min = 1, max = 255, message = "상세주소는 1자 이상 255자 이하입니다.")
	String addressDetail
) {
}

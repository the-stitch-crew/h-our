package stitch.crew.hour.user.dto;

import java.util.List;

import stitch.crew.hour.address.domain.Address;
import stitch.crew.hour.address.dto.AddressResponse;
import stitch.crew.hour.user.domain.User;

public record MyPageResponse(
	UserInfoResponse userInfo,
	List<AddressResponse> addresses
) {

	public static MyPageResponse from(User user, List<Address> addresses) {
		return new MyPageResponse(
			UserInfoResponse.from(user),
			addresses.stream()
				.map(AddressResponse::from)
				.toList()
		);
	}
}

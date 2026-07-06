package stitch.crew.hour.address.dto;

import stitch.crew.hour.address.domain.Address;

public record AddressResponse(
	Long id,
	String zipCode,
	String oldAddress,
	String roadAddress,
	String addressDetail,
	Boolean isMain
) {

	public static AddressResponse from(Address address) {
		return new AddressResponse(
			address.getId(),
			address.getZipCode(),
			address.getOldAddress(),
			address.getRoadAddress(),
			address.getAddressDetail(),
			address.getIsMain()
		);
	}
}

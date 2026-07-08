package stitch.crew.hour.address.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.address.domain.Address;
import stitch.crew.hour.address.dto.AddressCreateRequest;
import stitch.crew.hour.address.dto.AddressResponse;
import stitch.crew.hour.address.dto.AddressUpdateRequest;
import stitch.crew.hour.address.repository.AddressRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	@Transactional
	public AddressResponse createAddress(Long userId, AddressCreateRequest request) {
		User user = getUser(userId);
		boolean hasAddress = addressRepository.existsByUserIdAndDeletedAtIsNull(userId);
		// 첫 주소이고 (!hasAddress)
		boolean shouldBeMain = !hasAddress || Boolean.TRUE.equals(request.isMain());

		if (shouldBeMain) {
			unsetMainAddresses(userId);
		}

		Address address = new Address(
			user,
			request.zipCode(),
			request.oldAddress(),
			request.roadAddress(),
			request.addressDetail(),
			shouldBeMain
		);

		return AddressResponse.from(addressRepository.save(address));
	}

	public List<AddressResponse> getMyAddresses(Long userId) {
		getUser(userId);
		return addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByIsMainDescCreatedAtDesc(userId)
			.stream()
			.map(AddressResponse::from)
			.toList();
	}

	public AddressResponse getMyAddress(Long userId, Long addressId) {
		getUser(userId);
		return AddressResponse.from(getOwnedAddress(userId, addressId));
	}

	public AddressResponse getMainAddress(Long userId) {
		getUser(userId);
		Address address = addressRepository.findFirstByUserIdAndIsMainTrueAndDeletedAtIsNull(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
		return AddressResponse.from(address);
	}

	@Transactional
	public AddressResponse updateAddress(Long userId, Long addressId, AddressUpdateRequest request) {
		getUser(userId);
		Address address = getOwnedAddress(userId, addressId);
		address.update(
			request.zipCode(),
			request.oldAddress(),
			request.roadAddress(),
			request.addressDetail()
		);
		return AddressResponse.from(address);
	}

	@Transactional
	public AddressResponse setMainAddress(Long userId, Long addressId) {
		getUser(userId);
		Address address = getOwnedAddress(userId, addressId);
		unsetMainAddresses(userId);
		address.setMain();
		return AddressResponse.from(address);
	}

	@Transactional
	public void deleteAddress(Long userId, Long addressId) {
		getUser(userId);
		Address address = getOwnedAddress(userId, addressId);
		address.delete();
	}

	private User getUser(Long userId) {
		User user = userRepository.findByIdOrthrow(userId);
		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.USER_DONT_EXISTS
		);
		return user;
	}

	private Address getOwnedAddress(Long userId, Long addressId) {
		return addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
	}

	private void unsetMainAddresses(Long userId) {
		addressRepository.findAllByUserIdAndIsMainTrueAndDeletedAtIsNull(userId)
			.forEach(Address::unsetMain);
	}
}

package stitch.crew.hour.address.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stitch.crew.hour.address.dto.AddressCreateRequest;
import stitch.crew.hour.address.dto.AddressResponse;
import stitch.crew.hour.address.dto.AddressUpdateRequest;
import stitch.crew.hour.address.service.AddressService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@PostMapping
	public ResponseEntity<ApiResponses<AddressResponse>> createAddress(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody AddressCreateRequest request
	){
		AddressResponse response = addressService.createAddress(currentUser.getId(), request);
		return ApiResult.created(SuccessCode.ADDRESS_CREATED, response);
	}

	@GetMapping
	public ResponseEntity<ApiResponses<List<AddressResponse>>> getMyAddresses(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		List<AddressResponse> response = addressService.getMyAddresses(currentUser.getId());
		return ApiResult.ok(SuccessCode.ADDRESS_READ, response);
	}

	@GetMapping("/main")
	public ResponseEntity<ApiResponses<AddressResponse>> getMainAddress(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		AddressResponse response = addressService.getMainAddress(currentUser.getId());
		return ApiResult.ok(SuccessCode.ADDRESS_READ, response);
	}

	@GetMapping("/{addressId}")
	public ResponseEntity<ApiResponses<AddressResponse>> getMyAddress(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long addressId
	) {
		AddressResponse response = addressService.getMyAddress(currentUser.getId(), addressId);
		return ApiResult.ok(SuccessCode.ADDRESS_READ, response);
	}

	@PatchMapping("/{addressId}")
	public ResponseEntity<ApiResponses<AddressResponse>> updateAddress(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long addressId,
		@Valid @RequestBody AddressUpdateRequest request
	) {
		AddressResponse response = addressService.updateAddress(currentUser.getId(), addressId, request);
		return ApiResult.ok(SuccessCode.ADDRESS_UPDATED, response);
	}

	@PatchMapping("/{addressId}/main")
	public ResponseEntity<ApiResponses<AddressResponse>> setMainAddress(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long addressId
	) {
		AddressResponse response = addressService.setMainAddress(currentUser.getId(), addressId);
		return ApiResult.ok(SuccessCode.ADDRESS_MAIN_UPDATED, response);
	}

	@DeleteMapping("/{addressId}")
	public ResponseEntity<ApiResponses<Void>> deleteAddress(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long addressId
	) {
		addressService.deleteAddress(currentUser.getId(), addressId);
		return ApiResult.ok(SuccessCode.ADDRESS_DELETED);
	}
}

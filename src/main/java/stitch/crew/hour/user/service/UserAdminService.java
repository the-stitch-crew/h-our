package stitch.crew.hour.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.AdminUserDetailResponse;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;
import stitch.crew.hour.user.dto.UserBlacklistUpdateRequest;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAdminService {

	private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

	private final UserRepository userRepository;

	public Page<AdminUserSearchResponse> getUsers(
		int page,
		int size,
		String keyword,
		Role role,
		Gender gender,
		Boolean blacklisted,
		Boolean includeDeleted
	) {
		return userRepository.getAdminUsers(
			PageRequest.of(page, size, DEFAULT_SORT),
			keyword,
			role,
			gender,
			blacklisted,
			includeDeleted
		);
	}

	public AdminUserDetailResponse getUser(Long userId) {
		User user = userRepository.findByIdOrthrow(userId);
		return AdminUserDetailResponse.from(user);
	}

	@Transactional
	public AdminUserDetailResponse updateUserRole(
		Long userId,
		UserRoleUpdateRequest request
	) {
		User user = userRepository.findByIdOrthrow(userId);

		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.USER_DONT_EXISTS
		);

		PreConditions.validate(
			request.role() == Role.USER || request.role() == Role.ADMIN,
			ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED
		);

		user.changeRole(request.role());
		return AdminUserDetailResponse.from(user);
	}

	@Transactional
	public AdminUserDetailResponse updateBlacklist(
		Long userId,
		UserBlacklistUpdateRequest request
	) {
		User user = userRepository.findByIdOrthrow(userId);
		user.changeBlacklisted(request.blacklisted());
		return AdminUserDetailResponse.from(user);
	}
}

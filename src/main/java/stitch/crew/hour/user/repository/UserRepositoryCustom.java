package stitch.crew.hour.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;

public interface UserRepositoryCustom {

	Page<AdminUserSearchResponse> getAdminUsers(
		Pageable pageable,
		String keyword,
		Role role,
		Gender gender,
		Boolean blacklisted,
		Boolean includeDeleted
	);
}

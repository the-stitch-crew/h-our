package stitch.crew.hour.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
	boolean existsByPhoneNumber(String phoneNumber);
	boolean existsByPhoneNumberAndEmailNot(String phoneNumber, String email);

	Optional<User> findByEmail(String email);

	default User findByIdOrthrow(Long id) {
		return findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.NO_USER));
	}
}

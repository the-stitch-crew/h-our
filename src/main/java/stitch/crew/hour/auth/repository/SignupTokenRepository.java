package stitch.crew.hour.auth.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.auth.domain.SignupToken;

@Repository
public interface SignupTokenRepository extends JpaRepository<SignupToken, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SignupToken> findByToken(String token);
}

package stitch.crew.hour.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stitch.crew.hour.address.domain.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	boolean existsByUserIdAndDeletedAtIsNull(Long userId);

	List<Address> findAllByUserIdAndDeletedAtIsNullOrderByIsMainDescCreatedAtDesc(Long userId);

	List<Address> findAllByUserIdAndIsMainTrueAndDeletedAtIsNull(Long userId);

	Optional<Address> findByIdAndUserIdAndDeletedAtIsNull(Long addressId, Long userId);

	Optional<Address> findFirstByUserIdAndIsMainTrueAndDeletedAtIsNull(Long userId);
}

package stitch.crew.hour.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.policy.domain.LessonPolicy;

public interface LessonPolicyRepository extends JpaRepository<LessonPolicy, Long> {
}

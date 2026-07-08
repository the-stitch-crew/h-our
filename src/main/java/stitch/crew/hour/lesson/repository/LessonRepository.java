package stitch.crew.hour.lesson.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.lesson.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}

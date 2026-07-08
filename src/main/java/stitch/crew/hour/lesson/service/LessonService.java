package stitch.crew.hour.lesson.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.dto.LessonRequest;
import stitch.crew.hour.lesson.repository.LessonRepository;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    @Transactional
    public void saveLesson(@Valid LessonRequest request) {
        Lesson lesson = new Lesson(request.name(), request.price(),  request.duration());
        lessonRepository.save(lesson);
    }
}

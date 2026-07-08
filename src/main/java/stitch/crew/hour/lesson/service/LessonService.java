package stitch.crew.hour.lesson.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.dto.LessonRequest;
import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.lesson.repository.LessonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    @Transactional
    public void saveLesson(LessonRequest request) {
        PreConditions.check(lessonRepository.existsByName(request.name()), ErrorCode.EXIST_LESSON);
        Lesson lesson = new Lesson(request.name(), request.price(),  request.duration());
        lessonRepository.save(lesson);
    }

    public List<LessonResponse> getLessons() {
        List<Lesson> lessons = lessonRepository.findAll();
        return lessons.stream().map(LessonResponse::from).toList();
    }

    @Transactional
    public void updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND));
        PreConditions.check(lessonRepository.existsByName(request.name()), ErrorCode.EXIST_LESSON);
        lesson.update(request);
    }

    public LessonResponse getLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new BusinessException(ErrorCode.LESSON_NOT_FOUND));
        return LessonResponse.from(lesson);
    }
}

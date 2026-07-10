package stitch.crew.hour.lesson.dto;

import stitch.crew.hour.lesson.domain.Lesson;

public record LessonResponse(
        Long id,
        String name,
        Integer price,
        Integer duration
) {
    public static LessonResponse from(Lesson lesson) {
        return new LessonResponse(lesson.getId(), lesson.getName(), lesson.getPrice(), lesson.getDuration());
    }
}

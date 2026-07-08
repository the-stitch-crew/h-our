package stitch.crew.hour.lesson.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.lesson.dto.LessonRequest;
import stitch.crew.hour.lesson.service.LessonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/lessons")
public class LessonAdminController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<ApiResponses<Void>> saveLesson(@RequestBody @Valid LessonRequest request) {
        lessonService.saveLesson(request);
        return ApiResult.created(SuccessCode.LESSON_CREATED);
    }
}

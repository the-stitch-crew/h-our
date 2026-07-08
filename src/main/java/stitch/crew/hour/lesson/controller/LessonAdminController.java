package stitch.crew.hour.lesson.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PatchMapping("/{lessonId}")
    public ResponseEntity<ApiResponses<Void>> updateLesson(@PathVariable Long lessonId,
                                                           @RequestBody @Valid LessonRequest request) {
        lessonService.updateLesson(lessonId, request);
        return ApiResult.ok(SuccessCode.LESSON_UPDATED);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<ApiResponses<Void>> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ApiResult.ok(SuccessCode.LESSON_DELETED);
    }
}

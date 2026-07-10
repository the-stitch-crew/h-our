package stitch.crew.hour.lesson.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.lesson.service.LessonService;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;
import stitch.crew.hour.policy.service.LessonPolicyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;
    private final LessonPolicyService policyService;

    @GetMapping("/{lessonId}")
    public ResponseEntity<ApiResponses<LessonResponse>> method(@PathVariable Long lessonId) {
        LessonResponse response = lessonService.getLesson(lessonId);
        return ApiResult.ok(SuccessCode.LESSON_READ, response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponses<List<LessonResponse>>> method() {
        List<LessonResponse> response = lessonService.getLessons();
        return ApiResult.ok(SuccessCode.LESSON_READ, response);
    }

    @GetMapping("/policy")
    public ResponseEntity<ApiResponses<LessonPolicyResponse>> getLessonPolicy() {
        LessonPolicyResponse response = policyService.getLessonPolicy();
        return ApiResult.ok(SuccessCode.LESSON_POLICY_READ, response);
    }
}

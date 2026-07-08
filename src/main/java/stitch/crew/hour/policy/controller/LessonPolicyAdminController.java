package stitch.crew.hour.policy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.dto.LessonPolicyRequest;
import stitch.crew.hour.policy.service.LessonPolicyService;

@RestController
@RequestMapping("/api/admin/lessons/policy")
@RequiredArgsConstructor
public class LessonPolicyAdminController {
    private final LessonPolicyService lessonPolicyService;

    @PutMapping
    public ResponseEntity<ApiResponses<Void>> updateLessonPolicy(@RequestBody @Valid LessonPolicyRequest request) {
        lessonPolicyService.updateLessonPolicy(request);
        return ApiResult.ok(SuccessCode.LESSON_POLICY_UPDATED);
    }

}

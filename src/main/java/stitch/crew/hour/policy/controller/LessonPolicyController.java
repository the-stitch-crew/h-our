package stitch.crew.hour.policy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;
import stitch.crew.hour.policy.service.LessonPolicyService;

@RestController
@RequestMapping("/api/lessons/policy")
@RequiredArgsConstructor
public class LessonPolicyController {

    private final LessonPolicyService policyService;

    @GetMapping
    public ResponseEntity<ApiResponses<LessonPolicyResponse>> getLessonPolicy() {
        LessonPolicyResponse response = policyService.getLessonPolicy();
        return ApiResult.ok(SuccessCode.LESSON_POLICY_READ, response);
    }

}

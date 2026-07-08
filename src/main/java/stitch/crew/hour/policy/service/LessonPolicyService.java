package stitch.crew.hour.policy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.policy.domain.LessonPolicy;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;
import stitch.crew.hour.policy.repository.LessonPolicyRepository;

@Service
@RequiredArgsConstructor
public class LessonPolicyService {
    private final LessonPolicyRepository policyRepository;

    @Transactional(readOnly = true)
    public LessonPolicyResponse getLessonPolicy() {
        LessonPolicy policy = policyRepository.findById(1L).orElseThrow(()-> new BusinessException(ErrorCode.NO_LESSON_POLICY));
        return LessonPolicy.from(policy);
    }
}

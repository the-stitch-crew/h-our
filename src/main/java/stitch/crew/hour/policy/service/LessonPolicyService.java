package stitch.crew.hour.policy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stitch.crew.hour.policy.repository.LessonPolicyRepository;

@Service
@RequiredArgsConstructor
public class LessonPolicyService {
    private final LessonPolicyRepository policyRepository;

}

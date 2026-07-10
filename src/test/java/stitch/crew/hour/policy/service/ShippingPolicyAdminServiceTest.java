package stitch.crew.hour.policy.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.dto.ShippingPolicyCreateRequest;
import stitch.crew.hour.policy.dto.ShippingPolicyResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingPolicyAdminService 클래스의")
class ShippingPolicyAdminServiceTest {
    @InjectMocks
    private ShippingPolicyAdminService shippingPolicyAdminService;

    @Mock
    private ShippingPolicyRepository shippingPolicyRepository;

    @Test
    @DisplayName("createShippingPolicy 메서드는 배송 정책을 생성한다")
    void createShippingPolicy() {
        ShippingPolicyCreateRequest request = new ShippingPolicyCreateRequest(3500L, 2000L, false);
        given(shippingPolicyRepository.save(any(ShippingPolicy.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ShippingPolicyResponse response = shippingPolicyAdminService.createShippingPolicy(request);

        assertThat(response.deliveryFee()).isEqualTo(3500L);
        assertThat(response.extraFee()).isEqualTo(2000L);
        assertThat(response.isActive()).isFalse();
    }

    @Test
    @DisplayName("getShippingPolicies 메서드는 삭제되지 않은 배송 정책 목록을 조회한다")
    void getShippingPolicies() {
        ShippingPolicy policy = policy(1L, 3500L, 2000L, true);
        given(shippingPolicyRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc())
                .willReturn(List.of(policy));

        List<ShippingPolicyResponse> response = shippingPolicyAdminService.getShippingPolicies();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().shippingPolicyId()).isEqualTo(1L);
        assertThat(response.getFirst().deliveryFee()).isEqualTo(3500L);
    }

    @Test
    @DisplayName("getShippingPolicy 메서드는 배송 정책 상세를 조회한다")
    void getShippingPolicy() {
        ShippingPolicy policy = policy(1L, 3500L, 2000L, true);
        given(shippingPolicyRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(policy));

        ShippingPolicyResponse response = shippingPolicyAdminService.getShippingPolicy(1L);

        assertThat(response.shippingPolicyId()).isEqualTo(1L);
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("updateShippingPolicy 메서드는 배송 정책을 수정한다")
    void updateShippingPolicy() {
        ShippingPolicy policy = policy(1L, 3500L, 2000L, false);
        ShippingPolicyUpdateRequest request = new ShippingPolicyUpdateRequest(4000L, null, null);
        given(shippingPolicyRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(policy));

        ShippingPolicyResponse response = shippingPolicyAdminService.updateShippingPolicy(1L, request);

        assertThat(response.deliveryFee()).isEqualTo(4000L);
        assertThat(response.extraFee()).isNull();
        assertThat(response.isActive()).isFalse();
    }

    @Test
    @DisplayName("activateShippingPolicy 메서드는 기존 활성 정책을 비활성화하고 대상 정책을 활성화한다")
    void activateShippingPolicy() {
        ShippingPolicy oldActivePolicy = policy(1L, 3500L, 2000L, true);
        ShippingPolicy targetPolicy = policy(2L, 4000L, null, false);
        given(shippingPolicyRepository.findByIdAndDeletedAtIsNull(2L))
                .willReturn(Optional.of(targetPolicy));
        given(shippingPolicyRepository.findAllByIsActiveTrueAndDeletedAtIsNull())
                .willReturn(List.of(oldActivePolicy));

        ShippingPolicyResponse response = shippingPolicyAdminService.activateShippingPolicy(2L);

        assertThat(oldActivePolicy.getIsActive()).isFalse();
        assertThat(targetPolicy.getIsActive()).isTrue();
        assertThat(response.shippingPolicyId()).isEqualTo(2L);
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("getShippingPolicy 메서드는 존재하지 않는 id면 SHIPPING_POLICY_NOT_FOUND 예외를 던진다")
    void getShippingPolicyNotFound() {
        given(shippingPolicyRepository.findByIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> shippingPolicyAdminService.getShippingPolicy(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SHIPPING_POLICY_NOT_FOUND);
    }

    @Test
    @DisplayName("createShippingPolicy 메서드는 활성 정책 생성 시 기존 활성 정책을 비활성화한다")
    void createActiveShippingPolicyDeactivatesExistingActivePolicy() {
        ShippingPolicy oldActivePolicy = policy(1L, 3500L, 2000L, true);
        ShippingPolicyCreateRequest request = new ShippingPolicyCreateRequest(4000L, null, true);
        given(shippingPolicyRepository.findAllByIsActiveTrueAndDeletedAtIsNull())
                .willReturn(List.of(oldActivePolicy));
        given(shippingPolicyRepository.save(any(ShippingPolicy.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        shippingPolicyAdminService.createShippingPolicy(request);

        ArgumentCaptor<ShippingPolicy> captor = ArgumentCaptor.forClass(ShippingPolicy.class);
        verify(shippingPolicyRepository).save(captor.capture());
        assertThat(oldActivePolicy.getIsActive()).isFalse();
        assertThat(captor.getValue().getIsActive()).isTrue();
    }

    private ShippingPolicy policy(Long id, Long deliveryFee, Long extraFee, Boolean isActive) {
        ShippingPolicy policy = new ShippingPolicy(deliveryFee, extraFee, isActive);
        ReflectionTestUtils.setField(policy, "id", id);
        return policy;
    }
}

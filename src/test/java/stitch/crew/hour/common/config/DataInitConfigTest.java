package stitch.crew.hour.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.repository.LessonPolicyRepository;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitConfig 클래스의")
class DataInitConfigTest {
    @Mock
    ShippingPolicyRepository shippingPolicyRepository;

    @Mock
    LessonPolicyRepository lessonPolicyRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderBoundaryRepository orderBoundaryRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Environment environment;

    @Test
    @DisplayName("init 메서드는 활성 배송 정책이 이미 있으면 배송 정책을 중복 생성하지 않는다")
    void initDoesNotCreateDuplicateActiveShippingPolicy() throws Exception {
        DataInitConfig dataInitConfig = new DataInitConfig(
                shippingPolicyRepository,
                lessonPolicyRepository,
                userRepository,
                categoryRepository,
                productRepository,
                orderBoundaryRepository,
                passwordEncoder,
                environment
        );
        given(shippingPolicyRepository.findActive())
                .willReturn(Optional.of(new ShippingPolicy(3500L, 2000L, true)));
        given(environment.matchesProfiles("dev")).willReturn(false);

        CommandLineRunner runner = dataInitConfig.init();
        runner.run();

        verify(shippingPolicyRepository, never()).save(any(ShippingPolicy.class));
    }

    @Test
    @DisplayName("init 메서드는 설정된 초기 어드민 이메일이 없으면 기존 유저 수와 무관하게 어드민을 생성한다")
    void initCreatesConfiguredAdminWhenEmailDoesNotExist() throws Exception {
        DataInitConfig dataInitConfig = new DataInitConfig(
                shippingPolicyRepository,
                lessonPolicyRepository,
                userRepository,
                categoryRepository,
                productRepository,
                orderBoundaryRepository,
                passwordEncoder,
                environment
        );
        given(shippingPolicyRepository.findActive())
                .willReturn(Optional.of(new ShippingPolicy(3500L, 2000L, true)));
        given(environment.getProperty("INITIAL_ADMIN_EMAIL")).willReturn("admin@naver.com");
        given(environment.getProperty("INITIAL_ADMIN_PASSWORD")).willReturn("password1234");
        given(environment.getProperty("INITIAL_ADMIN_USER_NAME", "Initial Admin")).willReturn("FirstAdmin");
        given(environment.getProperty("INITIAL_ADMIN_PHONE_NUMBER")).willReturn("010-1234-5678");
        given(environment.getProperty("INITIAL_ADMIN_NATIONALITY", "KOREA")).willReturn("KOREA");
        given(userRepository.existsByEmail("admin@naver.com")).willReturn(false);
        given(passwordEncoder.encode("password1234")).willReturn("encoded-password");
        given(environment.matchesProfiles("dev")).willReturn(false);

        CommandLineRunner runner = dataInitConfig.init();
        runner.run();

        verify(userRepository).save(any(User.class));
    }
}

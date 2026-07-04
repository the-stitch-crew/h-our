package stitch.crew.hour.common.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.shippingpolicy.domain.ShippingPolicy;
import stitch.crew.hour.shippingpolicy.repository.ShippingPolicyRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {
    private final ShippingPolicyRepository shippingPolicyRepository;
    @Bean
    @Transactional
    CommandLineRunner init() {
        return args -> {
            shippingPolicyRepository.save(
                    new ShippingPolicy(
                            3_500L,
                            2_000L,
                            true
                    )
            );
        };
    }
}

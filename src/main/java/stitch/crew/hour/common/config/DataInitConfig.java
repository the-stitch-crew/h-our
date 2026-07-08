package stitch.crew.hour.common.config;


import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitConfig {
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

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

            if (userRepository.count() < 1) {
                createInitialAdminIfConfigured();
            }
        };
    }

    private void createInitialAdminIfConfigured() {
        String email = environment.getProperty("INITIAL_ADMIN_EMAIL");
        String password = environment.getProperty("INITIAL_ADMIN_PASSWORD");
        String userName = environment.getProperty("INITIAL_ADMIN_USER_NAME", "Initial Admin");
        String phoneNumber = environment.getProperty("INITIAL_ADMIN_PHONE_NUMBER");
        String nationality = environment.getProperty("INITIAL_ADMIN_NATIONALITY", "KOREA");
        LocalDate birthDate = environment.getProperty(
                "INITIAL_ADMIN_BIRTH_DATE",
                LocalDate.class,
                LocalDate.of(2000, 1, 31)
        );
        Gender gender = environment.getProperty("INITIAL_ADMIN_GENDER", Gender.class, Gender.MALE);

        if (
                !StringUtils.hasText(email)
                        || !StringUtils.hasText(password)
                        || !StringUtils.hasText(userName)
                        || !StringUtils.hasText(phoneNumber)
                        || !StringUtils.hasText(nationality)
        ) {
            log.warn("초기 어드민 생성 패쓰.");
            return;
        }

        User admin = new User(
                userName,
                email,
                passwordEncoder.encode(password),
                birthDate,
                Role.ADMIN,
                gender,
                null,
                phoneNumber,
                nationality,
                false,
                false
        );

        userRepository.save(admin);
    }
}

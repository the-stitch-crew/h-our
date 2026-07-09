package stitch.crew.hour.shippingpolicy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.common.config.SecurityConfig;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.dto.DeliveryFeeRequest;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.product.domain.Product;import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ShippingPolicyAdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShippingPolicyRepository shippingPolicyRepository;

    @Autowired
    ObjectMapper objectMapper;

    ShippingPolicy saved;

    User testUser;

    TestingAuthenticationToken token;

    final String BASE_URL = "/api/admin/shippingpolicy";

    @BeforeEach
    void setUp(){

        testUser = userRepository.save(
                new User(
                        "이름",
                        "wjdtn747@naver.com",
                        "1234",
                        LocalDate.now(),
                        Role.ADMIN,
                        Gender.MALE,
                        "google",
                        "010",
                        "?",
                        false,
                        false
                )
        );

        token = new TestingAuthenticationToken(
                CurrentUser.from(testUser),
                null,
                Role.ADMIN.getValue()
        );

        saved = shippingPolicyRepository.save(
                new ShippingPolicy(
                        3500L,
                        2000L,
                        true
                )
        );

    }

    @Nested
    @DisplayName("Describe : POST /api/admin/shppingpolicy")
    class Describe_createShippingPolicy{

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_Valid_Data{

            @Test
            @DisplayName("It : 새로운 배송비 규칙이 등록")
            void It_배송비_규칙_등록() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                DeliveryFeeRequest request = new DeliveryFeeRequest(
                        4000,
                        500
                );

                String json = objectMapper.writeValueAsString(request);

                // when
                mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())

                // then
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.message").value(SuccessCode.DELIVERY_FEE_SAVEED_SUCCESS.getSuccessMessage()))
                        .andExpect(jsonPath("$.data.currentDeliveryFee").value(4500));


            }

        }

    }

}
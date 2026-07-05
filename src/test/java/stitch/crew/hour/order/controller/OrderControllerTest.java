package stitch.crew.hour.order.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class OrderControllerTest {

    final String BASE_URL = "/api/orders";

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    User testUser;

    TestingAuthenticationToken token;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(
                new User(
                        "이름",
                        "wjdtn747@naver.com",
                        "1234",
                        LocalDate.now(),
                        Role.USER,
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
                "ROLE_USER"
        );
    }

    @Nested
    @DisplayName("Describe : POST /api/orders")
    class Describe_Create_Order{
        OrderProductCreateRequest testOrderProduct1;
        OrderProductCreateRequest testOrderProduct2;
        List<OrderProductCreateRequest> orderProducts;
        OrderCreateRequest testOrderRequest;

        @BeforeEach
        void setUp(){
            testOrderProduct1 = TestUtil.orderProductCreateRequest(1L);
            testOrderProduct2 = TestUtil.orderProductCreateRequest(2L);

            orderProducts = new ArrayList<>();
            orderProducts.add(testOrderProduct1);
            orderProducts.add(testOrderProduct2);
            testOrderRequest = new OrderCreateRequest(
                    orderProducts,
                    "원주시",
                    "26421312",
                    "이정수",
                    "0107615022313619",
                    "요청이에용",
                    "이름",
                    "01041245512"
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 성공적으로 생성 후 201을 반환")
            void It_성공적으로_주문_생성() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                String json = objectMapper.writeValueAsString(testOrderRequest);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())
                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.ordererName").value(testOrderRequest.ordererName()));
            }

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("It : ReceiverName이 공백인 경우 Orderer Name으로 설정 및 주문을 성공적으로 생성 후 201을 반환")
            void It_ReceiverName이_공백_이더라도_성공적으로_주문_생성(String name) throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                testOrderRequest = new OrderCreateRequest(
                        orderProducts,
                        "원주시",
                        "26421312",
                        name,
                        "0107615022313619",
                        "요청이에용",
                        "이정수",
                        "01041245512"
                );

                String json = objectMapper.writeValueAsString(testOrderRequest);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())
                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.receiverName").value(testOrderRequest.ordererName()));
            }
        }
    }

}

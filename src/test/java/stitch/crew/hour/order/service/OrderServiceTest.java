package stitch.crew.hour.order.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.shippingpolicy.domain.ShippingPolicy;
import stitch.crew.hour.shippingpolicy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("OrderService의")
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ShippingPolicyRepository shippingPolicyRepository;

    @Nested
    @DisplayName("Describe : createOrder()는")
    class Describe_Create_Order{

        User testUser;
        OrderProductCreateRequest testOrderProduct1;
        OrderProductCreateRequest testOrderProduct2;
        OrderCreateRequest testOrderRequest;

        @BeforeEach
        void setUp(){
            testUser = userRepository.save(
                    new User(
                            "이름",
                            "wjdtn747@naver.com",
                            "1234",
                            LocalDate.now(),
                            "google",
                            "010",
                            "?",
                            false,
                            false
                    )
            );
            testOrderProduct1 = TestUtil.orderProductCreateRequest(1L);
            testOrderProduct2 = TestUtil.orderProductCreateRequest(2L);

            List<OrderProductCreateRequest> orderProducts = new ArrayList<>();
            orderProducts.add(testOrderProduct1);
            orderProducts.add(testOrderProduct2);
            testOrderRequest = new OrderCreateRequest(
                    orderProducts,
                    "원주시",
                    "26421312",
                    "이정수",
                    "0107615022313619",
                    "요청이에용",
                    "주문",
                    "01041245512"
            );
        }

        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{


            @Test
            @DisplayName("It : 성공적으로 주문을 생성")
            void it_성공적으로_주문을_생성(){
                // given
                ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

                // when
                OrderCreateResponse order = orderService.createOrder(
                        testUser.getId(),
                        testOrderRequest
                );

                // then
                Assertions.assertThat(order.orderProducts().size()).isEqualTo(2);
                Assertions.assertThat(order.totalPrice()).isEqualTo(
                        4000L + activeOrThrow.getDeliveryFee()
                );
            }

        }

    }
}
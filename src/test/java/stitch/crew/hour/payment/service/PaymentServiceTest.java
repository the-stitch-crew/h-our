package stitch.crew.hour.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.repository.OrderRepository;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.repository.OrderProductRepository;
import stitch.crew.hour.payment.constant.PaymentStatus;
import stitch.crew.hour.payment.domain.Payment;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;
import stitch.crew.hour.payment.dto.PaymentRequestBody;
import stitch.crew.hour.payment.repository.PaymentRepository;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;

    User testUser;
    TestingAuthenticationToken token;
    Category testCategory;
    Product testProduct;
    Order testOrder;
    OrderProduct testOrderProduct;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp(){
        testCategory = categoryRepository.save(
                new Category("카테고리명")
        );

        testProduct = productRepository.save(
                new Product(
                        "테스트용 상품",
                        2000L,
                        "상품요약",
                        "설명글",
                        testCategory
                )
        );

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

        testOrder = orderRepository.save(
                new Order(
                        testUser,
                        3500L,
                        "",
                        "",
                        "",
                        "",
                        ""
                )
        );

        testOrderProduct = orderProductRepository.save(
                new OrderProduct(
                        testProduct.getName(),
                        20L,
                        testProduct.getPrice(),
                        testProduct.getId(),
                        "",
                        testOrder
                )
        );

        testOrder.setOrderProduct(testOrderProduct);


        token = new TestingAuthenticationToken(
                CurrentUser.from(testUser),
                null,
                Role.ADMIN.getValue()
        );
    }


    @Nested
    @DisplayName("Describe : initPaymentByOrder()")
    class Describe_initPaymentByOrder{
        PaymentRequestBody paymentRequestBody;

        @BeforeEach
        void setUp(){
           paymentRequestBody = new PaymentRequestBody(
                UUID.randomUUID().toString(),
                   UUID.randomUUID().toString(),
                   testOrder.getOrderNumber().toString(),
                   Long.valueOf(testOrder.getTotalPrice())
           );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 초기 결제 생성 완료")
            void It_초기_결제_생성_완료(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                Payment payment = paymentService.initPaymentByOrder(paymentRequestBody);

                // then
                Assertions.assertThat(payment.getOrderNumber()).isEqualTo(UUID.fromString(paymentRequestBody.orderNumber()));
                Assertions.assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 데이터가 주어지는 경우")
        class Context_with_InValid_data{

            @ParameterizedTest
            @EnumSource(
                    value = OrderStatus.class,
                    names = { "ORDERED" },
                    mode = EnumSource.Mode.EXCLUDE
            )
            @DisplayName("It : Order가  ORDERED 상태가 아닌 경우 이미 결제가 수행된 것으로 간주하여 예외 발생 ")
            void It_결제_실패__이미_결제(OrderStatus status){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                testOrder.switchStatus(status);

                // when
                Assertions.assertThatThrownBy(
                        ()-> paymentService.initPaymentByOrder(paymentRequestBody)
                )
                        // then
                        .isInstanceOf(BusinessException.class)
                                .hasMessageContaining(ErrorCode.PAYMENT_ALREADY_PAYED.getMessage());
            }
        }

    }

    @Nested
    @DisplayName("Describe : confirmPayment()")
    class Describe_confirmPayment{
        PaymentRequestBody paymentRequestBody;
        Payment testPayment;
        @BeforeEach
        void setUp(){
            paymentRequestBody = new PaymentRequestBody(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    testOrder.getOrderNumber().toString(),
                    Long.valueOf(testOrder.getTotalPrice())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
            testPayment = paymentService.initPaymentByOrder(
                    paymentRequestBody
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 결제 확정 완료")
            void It_결제_확정_완료(){

                // when
                paymentService.confirmPayment(
                        testUser.getId(),
                        testPayment,
                        ""
                );

                // then
                Order foundedOrder = orderRepository.findByOrderNumberOrThrow(testOrder.getOrderNumber());

                Assertions.assertThat(foundedOrder.getOrderStatus()).isEqualTo(OrderStatus.PURCHASED);
                Assertions.assertThat(testPayment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 데이터가 주어지는 경우")
        class Context_with_InValid_data{

            @Test
            @DisplayName("It : 다른 사용자가 결제 확정을 수행하는 것을 차단")
            void It_결제_확정_실패__다른_사용자_이미_결제(){
                // given

                User userTester = userRepository.save(
                        new User(
                                "이d12름",
                                "wjdtDSAFDA2SFSDAn747@naver.com",
                                "1232214",
                                LocalDate.now(),
                                Role.USER,
                                Gender.MALE,
                                "google",
                                "012412312313423140",
                                "?",
                                false,
                                false
                        )
                );

                TestingAuthenticationToken testingToken = new TestingAuthenticationToken(
                        CurrentUser.from(userTester),
                        null,
                        Role.USER.getValue()
                );

                SecurityContextHolder.getContext().setAuthentication(testingToken);

                // when
                Assertions.assertThatThrownBy(
                                ()-> paymentService.confirmPayment(
                                        userTester.getId(),
                                        testPayment,
                                        ""
                                )
                        )
                        // then
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NO_AUTHORITY_ON_ORDER.getMessage());
            }
        }

    }

    @Nested
    @DisplayName("Describe : getPaymentDetail()")
    class Describe_getPaymentDetail{

        PaymentRequestBody paymentRequestBody;
        Payment testPayment;
        @BeforeEach
        void setUp(){
            paymentRequestBody = new PaymentRequestBody(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    testOrder.getOrderNumber().toString(),
                    Long.valueOf(testOrder.getTotalPrice())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
            testPayment = paymentService.initPaymentByOrder(
                    paymentRequestBody
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 단건 조회 성공")
            void It_단건_조회_성공(){

                // when
                PaymentDetailResponse paymentDetail = paymentService.getPaymentDetail(
                        testUser.getId(),
                        testPayment.getId()
                );

                // then
                Assertions.assertThat(paymentDetail.orderNumber()).isEqualTo(UUID.fromString(paymentRequestBody.orderNumber()));
                Assertions.assertThat(paymentDetail.paymentStatus()).isEqualTo(PaymentStatus.PENDING.toString());
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 데이터가 주어지는 경우")
        class Context_with_InValid_data{

            @Test
            @DisplayName("It : 다른 사용자가 조회 시 차단")
            void It_조회_실패__다른_사용자(){
                // given

                User userTester = userRepository.save(
                        new User(
                                "이d12름",
                                "wjdtDSAFDA2SFSDAn747@naver.com",
                                "1232214",
                                LocalDate.now(),
                                Role.USER,
                                Gender.MALE,
                                "google",
                                "012412312313423140",
                                "?",
                                false,
                                false
                        )
                );

                TestingAuthenticationToken testingToken = new TestingAuthenticationToken(
                        CurrentUser.from(userTester),
                        null,
                        Role.USER.getValue()
                );

                SecurityContextHolder.getContext().setAuthentication(testingToken);

                // when
                Assertions.assertThatThrownBy(
                                ()-> paymentService.getPaymentDetail(
                                        userTester.getId(),
                                        testPayment.getId()
                                )
                        )
                        // then
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.PAYMENT_PERMISSION_DENY.getMessage());
            }
        }

    }

    @Nested
    @DisplayName("Describe : getPaymentSearch()")
    class Describe_getPaymentSearch{
        @BeforeEach
        void setUp(){
            SecurityContextHolder.getContext().setAuthentication(token);

            for(int i = 0 ; i < 10 ; i ++){
                PaymentRequestBody paymentRequestBody = new PaymentRequestBody(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        testOrder.getOrderNumber().toString(),
                        Long.valueOf(testOrder.getTotalPrice())
                );
                paymentService.initPaymentByOrder(
                        paymentRequestBody
                );
            }
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 다건 조회 성공")
            void It_다건_조회_성공(){

                // when
                Page<PaymentDetailResponse> paymentSearch = paymentService.getPaymentSearch(
                        testUser.getId(),
                        PageRequest.of(0, 5)
                );
                ;

                // then
                Assertions.assertThat(paymentSearch.getContent().size()).isEqualTo(5);
            }
        }
    }

    @Nested
    @DisplayName("Describe : getPaymentDetailByOrderNumber()")
    class Describe_getPaymentDetailByOrderNumber{

        PaymentRequestBody paymentRequestBody;
        Payment testPayment;
        @BeforeEach
        void setUp(){
            paymentRequestBody = new PaymentRequestBody(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    testOrder.getOrderNumber().toString(),
                    Long.valueOf(testOrder.getTotalPrice())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
            testPayment = paymentService.initPaymentByOrder(
                    paymentRequestBody
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : Product Status : COMPLETE인 결제를 정상적으로 단건 조회 성공")
            void It_단건_조회_성공(){
                // given
                testPayment.switchPaymentStatus(PaymentStatus.COMPLETED);

                // when
                PaymentDetailResponse paymentDetail = paymentService.getPurchasedPaymentDetailByOrderNumber(
                        testUser.getId(),
                        testOrder.getOrderNumber()
                );

                // then
                Assertions.assertThat(paymentDetail.orderNumber()).isEqualTo(UUID.fromString(paymentRequestBody.orderNumber()));
                Assertions.assertThat(paymentDetail.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.toString());
            }
        }
    }

    @Nested
    @DisplayName("Describe : getReceipt()")
    class Describe_getReceipt{

        PaymentRequestBody paymentRequestBody;
        Payment testPayment;
        @BeforeEach
        void setUp(){
            paymentRequestBody = new PaymentRequestBody(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    testOrder.getOrderNumber().toString(),
                    Long.valueOf(testOrder.getTotalPrice())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
            testPayment = paymentService.initPaymentByOrder(
                    paymentRequestBody
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 단건 조회 성공")
            void It_단건_조회_성공(){
                // given
                testPayment.switchPaymentStatus(PaymentStatus.COMPLETED);

                // when
                PaymentDetailResponse paymentDetail = paymentService.getReceipt(
                        testUser.getId(),
                        testPayment.getId()
                );

                // then
                Assertions.assertThat(paymentDetail.orderNumber()).isEqualTo(UUID.fromString(paymentRequestBody.orderNumber()));
                Assertions.assertThat(paymentDetail.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED.toString());
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 데이터가 주어지는 경우")
        class Context_with_InValid_data{

            @Test
            @DisplayName("It : PENDING 상태 결제 조회 시 차단")
            void It_조회_실패__결제_PENDING(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                Assertions.assertThatThrownBy(
                                ()-> paymentService.getReceipt(
                                        testUser.getId(),
                                        testPayment.getId()
                                )
                        )
                        // then
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NO_ACCESS_ON_RECEIPT.getMessage());
            }
        }

    }

    @Nested
    @DisplayName("Describe : refundPaymentByPaymentId()")
    class Describe_refundPaymentByPaymentId{

        PaymentRequestBody paymentRequestBody;
        Payment testPayment;
        @BeforeEach
        void setUp(){
            paymentRequestBody = new PaymentRequestBody(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    testOrder.getOrderNumber().toString(),
                    Long.valueOf(testOrder.getTotalPrice())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
            testPayment = paymentService.initPaymentByOrder(
                    paymentRequestBody
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어지는 경우")
        class Context_with_Valid_data{

            @Test
            @DisplayName("It : 정상적으로 환불 성공")
            void It_단건_조회_성공(){

                // when
                paymentService.refundPaymentByPaymentId(
                        testUser.getId(),
                        testPayment.getId()
                );

                // then
                Payment founded = paymentRepository.findByIdOrThrow(testPayment.getId());

                Assertions.assertThat(founded.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 데이터가 주어지는 경우")
        class Context_with_InValid_data{

            @Test
            @DisplayName("It : 다른 사용자가 환불 시 차단")
            void It_환불_실패__다른_사용자(){
                // given

                User userTester = userRepository.save(
                        new User(
                                "이d12름",
                                "wjdtDSAFDA2SFSDAn747@naver.com",
                                "1232214",
                                LocalDate.now(),
                                Role.USER,
                                Gender.MALE,
                                "google",
                                "012412312313423140",
                                "?",
                                false,
                                false
                        )
                );

                TestingAuthenticationToken testingToken = new TestingAuthenticationToken(
                        CurrentUser.from(userTester),
                        null,
                        Role.USER.getValue()
                );

                SecurityContextHolder.getContext().setAuthentication(testingToken);

                // when
                Assertions.assertThatThrownBy(
                                ()-> paymentService.refundPaymentByPaymentId(
                                        userTester.getId(),
                                        testPayment.getId()
                                )
                        )
                        // then
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.PAYMENT_PERMISSION_DENY.getMessage());
            }
        }

    }

}
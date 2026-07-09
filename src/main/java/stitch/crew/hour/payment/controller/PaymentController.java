package stitch.crew.hour.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.OrderDetailResponse;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.payment.domain.Payment;
import stitch.crew.hour.payment.dto.CancelResponse;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;
import stitch.crew.hour.payment.dto.PaymentRequestBody;
import stitch.crew.hour.payment.dto.PaymentResponse;
import stitch.crew.hour.payment.service.PaymentService;
import stitch.crew.hour.user.domain.CurrentUser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController implements PaymentSwaggerSupporter{

    @Value("${payment.secretKey}")
    private String secretKey;

    @Value("${payment.baseUrl}")
    private String baseUrl;

    private final PaymentService paymentService;
    private final OrderService orderService;

    private final ObjectMapper om = new ObjectMapper();

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orders/{orderNumber}")
    public String showIndex(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable UUID orderNumber,
            Model model
    ) {
        OrderDetailResponse order = orderService.getOrderDetailFromPurchase(
                currentUser.getId(),
                orderNumber
        );

        model.addAttribute("order", order);
        model.addAttribute("orderName", getOrderName(order));
        return "index";
    }

    @GetMapping("/success")
    public String showSuccess() {
        return "success";
    }

    @GetMapping("/fail")
    public String showFail() {
        return "fail";
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PostMapping("/confirm")
    public String confirm(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody PaymentRequestBody requestBody
    ) throws Exception {
        Payment initPayment = paymentService.initPaymentByOrder(requestBody);

        if (!initPayment.getTotalPrice().equals(requestBody.amount())) {
            paymentService.cancelPayment(initPayment);
            return "fail";
        }

        ResponseEntity<String> response;

        try {
            response = requestConfirm(
                    requestBody,
                    initPayment.getIdempotencyKey()
            );
        } catch (Exception e) {
            paymentService.cancelPayment(initPayment);
            return "fail";
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            paymentService.cancelPayment(initPayment);
            return "fail";
        }

        PaymentResponse paymentResponse = om.readValue(
                response.getBody(),
                PaymentResponse.class
        );

        try {
            paymentService.confirmPayment(
                    currentUser.getId(),
                    initPayment,
                    paymentResponse.receipt().url()
            );

            return "success";
        } catch (Exception e) {
            requestPaymentCancel(
                    requestBody.paymentKey(),
                    "server payment handling failed",
                    initPayment.getIdempotencyKey()
            );

            paymentService.cancelPayment(initPayment);
            return "fail";
        }
    }

    public String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encode);
    }

    private String getOrderName(OrderDetailResponse order) {
        if (order.orderProducts().isEmpty()) {
            return "Order items";
        }

        String firstProductName = order.orderProducts().getFirst().name();
        int remainingProductCount = order.orderProducts().size() - 1;

        if (remainingProductCount == 0) {
            return firstProductName;
        }

        return "%s and %d more".formatted(firstProductName, remainingProductCount);
    }

    public ResponseEntity<String> requestConfirm(
            PaymentRequestBody requestBody,
            UUID idempotencyKey
    ) {
        Map<String, Object> requestMap = Map.of(
                "paymentKey", requestBody.paymentKey(),
                "orderId", requestBody.orderId(),
                "amount", requestBody.amount()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotency-Key",idempotencyKey.toString());
        headers.set(HttpHeaders.AUTHORIZATION, getAuthorization());

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestMap, headers);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                baseUrl + "/confirm",
                HttpMethod.POST,
                httpEntity,
                String.class
        );
    }

    public ResponseEntity<String> requestPaymentCancel(
            String paymentKey,
            String cancelReason,
            UUID idempotencyKey
    ) {
        Map<String, Object> requestMap = om.convertValue(new CancelResponse(cancelReason), new TypeReference<>() {});

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotency-Key",idempotencyKey.toString());
        headers.set(HttpHeaders.AUTHORIZATION, getAuthorization());

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestMap, headers);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                baseUrl + "/%s/cancel".formatted(paymentKey),
                HttpMethod.POST,
                httpEntity,
                String.class
        );
    }

    @GetMapping("/{paymentId}")
    @ResponseBody
    public ResponseEntity<ApiResponses<PaymentDetailResponse>> getPaymentDetail(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long paymentId
    ){
        return ApiResult.ok(
                SuccessCode.PAYMENT_READ_SUCCESS,
                paymentService.getPaymentDetail(
                        currentUser.getId(),
                        paymentId
                )
        );
    }

    @GetMapping("/orders/{orderNumber}/detail")
    @ResponseBody
    public ResponseEntity<ApiResponses<PaymentDetailResponse>> getPaymentDetailByOrderNumber(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable UUID orderNumber
    ){
        return ApiResult.ok(
                SuccessCode.PAYMENT_READ_SUCCESS,
                paymentService.getPurchasedPaymentDetailByOrderNumber(
                        currentUser.getId(),
                        orderNumber
                )
        );
    }


    @GetMapping
    @ResponseBody
    public ResponseEntity<ApiResponses<Page<PaymentDetailResponse>>> getPaymentSearch(
            @AuthenticationPrincipal CurrentUser currentUser,
            Paging paging
    ){
        return ApiResult.ok(
                SuccessCode.PAYMENT_READ_SUCCESS,
                paymentService.getPaymentSearch(
                        currentUser.getId(),
                        paging.toPageable()
                )
        );
    }

    @GetMapping("/{paymentId}/receipt")
    public String getReceiptUrl(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long paymentId,
            Model model
    ){
        PaymentDetailResponse payment = paymentService.getReceipt(
                currentUser.getId(),
                paymentId
        );

        model.addAttribute("payment", payment);
        return "receipt";
    }


    @DeleteMapping("/{paymentId}")
    @ResponseBody
    public ResponseEntity<ApiResponses<Void>> cancelPayment(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long paymentId
    ){
        paymentService.refundPaymentByPaymentId(
                currentUser.getId(),
                paymentId
        );

        Payment foundedPayment = paymentService.getPayment(paymentId);

        requestPaymentCancel(
                foundedPayment.getTossPaymentKey(),
                "사용자에 의해 결제가 취소되었습니다.",
                foundedPayment.getIdempotencyKey()
        );

        return ApiResult.ok(
                SuccessCode.PAYMENT_REFUND_COMPLETE
        );
    }
}

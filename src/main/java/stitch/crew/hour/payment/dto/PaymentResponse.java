package stitch.crew.hour.payment.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(

        String mId,
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,

        Long taxExemptionAmount,

        String status,
        String requestedAt,
        String approvedAt,

        Boolean useEscrow,
        Boolean cultureExpense,

        Card card,
        VirtualAccount virtualAccount,
        Transfer transfer,
        MobilePhone mobilePhone,
        GiftCertificate giftCertificate,
        CashReceipt cashReceipt,
        Object cashReceipts,
        Discount discount,
        Object cancels,

        String secret,
        String type,

        EasyPay easyPay,

        String country,

        Failure failure,

        Boolean isPartialCancelable,

        Receipt receipt,
        Checkout checkout,

        String currency,

        Long totalAmount,
        Long balanceAmount,
        Long suppliedAmount,
        Long vat,
        Long taxFreeAmount,

        Object metadata,

        String method,
        String version
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Card(
            String issuerCode,
            String acquirerCode,
            String number,
            Integer installmentPlanMonths,
            Boolean isInterestFree,
            String interestPayer,
            String approveNo,
            Boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            Long amount
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EasyPay(
            String provider,
            Long amount,
            Long discountAmount
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Receipt(
            String url
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Checkout(
            String url
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Failure(
            String code,
            String message
    ) {
    }

    // 현재 예시에서는 null이지만 추후 확장을 위해 선언
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VirtualAccount() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Transfer() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MobilePhone() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GiftCertificate() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CashReceipt() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Discount() {
    }
}

package stitch.crew.hour.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /*category*/
    EXIST_CATEGORY(HttpStatus.CONFLICT, "중복된 카테고리 이름입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),


    // 유저
    NO_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

    // 배송규칙
    NO_SHIPPING_POLICY(HttpStatus.NOT_FOUND, "활성화된 배송규칙이 없습니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자 권한이 없는 계정입니다."),



    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 객체입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "검증에 실패했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    BUSINESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류입니다.");

    private final HttpStatus status;
    private final String message;
}

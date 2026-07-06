package stitch.crew.hour.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    USER_PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 전화번호입니다."),
    USER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    USER_DONT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_ROLE_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "변경할 수 없는 역할입니다."),


    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    ABNORMAL_TOKEN(HttpStatus.UNAUTHORIZED, "비정상적인 토큰입니다."),
    ERROR_FROM_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 처리 중 오류가 발생했습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성에 실패했습니다."),


    /*category*/
    EXIST_CATEGORY(HttpStatus.CONFLICT, "중복된 카테고리 이름입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),




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

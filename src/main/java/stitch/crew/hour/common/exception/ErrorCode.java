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


    /*category*/
    EXIST_CATEGORY(HttpStatus.CONFLICT, "중복된 카테고리 이름입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.CONFLICT, "카테고리를 찾을 수 없습니다."),


    // 상품




    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 객체입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "검증에 실패했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    BUSINESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류입니다.");

    private final HttpStatus status;
    private final String message;
}

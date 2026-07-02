package stitch.crew.hour.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // USER
    USER_CREATED("계정이 정상적으로 생성되었습니다."),

    BUSINESS_SUCCESS("정상적으로 작성되었습니다.");

    private final String successMessage;
}

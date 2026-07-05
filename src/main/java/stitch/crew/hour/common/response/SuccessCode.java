package stitch.crew.hour.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // USER
    USER_CREATED("계정이 정상적으로 생성되었습니다."),
    USER_READ("계정이 정상적으로 조회되었습니다."),

    // AUTH
    AUTH_LOGIN_SUCCESS("로그인에 성공했습니다."),
    AUTH_REFRESH_SUCCESS("토큰 갱신에 성공했습니다."),
    AUTH_LOGOUT_SUCCESS("로그아웃에 성공했습니다."),


    /*CATEGORY*/
    CATEGORY_CREATED("카테고리가 정상적으로 생성되었습니다."),
    CATEGORY_READ("카테고리가 정상적으로 조회되었습니다."),
    CATEGORY_UPDATED("카테고리가 정상적으로 수정되었습니다."),

    BUSINESS_SUCCESS("정상적으로 작성되었습니다.");

    private final String successMessage;
}

package stitch.crew.hour.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

    // USER
    USER_CREATED("계정이 정상적으로 생성되었습니다."),
    USER_READ("계정이 정상적으로 조회되었습니다."),
    USER_UPDATED("계정이 정상적으로 수정되었습니다."),
    USER_PASSWORD_CHANGED("비밀번호가 정상적으로 변경되었습니다."),
    USER_DELETED("계정이 정상적으로 탈퇴되었습니다."),
    USER_ROLE_UPDATED("계정 권한이 정상적으로 변경되었습니다."),

    // AUTH
    AUTH_LOGIN_SUCCESS("로그인에 성공했습니다."),
    AUTH_REFRESH_SUCCESS("토큰 갱신에 성공했습니다."),
    AUTH_LOGOUT_SUCCESS("로그아웃에 성공했습니다."),

    // CART
    CART_RETRIEVE_SUCCESS("장바구니 조회에 성공했습니다."),
    CART_CREATED_SUCCESS("장바구니가 정상적으로 생성되었습니다."),
    CART_ADDITION_SUCCESS("장바구니에 상품이 정상적으로 추가되었습니다."),
    CART_UPDATE_SUCCESS("장바구니의 상품이 정상적으로 변경되었습니다."),
    CART_DELETE_SUCCESS("장바구니가 정상적으로 삭제되었습니다."),

    // Order
    ORDER_CREATED_SUCCESS("주문이 정상적으로 생성되었습니다."),

    // Product
    PRODUCT_CREATED_SUCCESS("상품이 정상적으로 생성되었습니다."),
    PRODUCT_READ("상품이 정상적으로 조회되었습니다."),
    PRODUCT_DELETE_SUCCESS("상품이 정상적으로 삭제되었습니다."),
    PRODUCT_UPDATE_SUCCESS("상품이 정상적으로 수정되었습니다."),
    PRODUCT_SWITCH_MAIN_SUCCESS("상품이 카테고리 메인 상품으로 수정되었습니다."),


    /*CATEGORY*/
    CATEGORY_CREATED("카테고리가 정상적으로 생성되었습니다."),
    CATEGORY_READ("카테고리가 정상적으로 조회되었습니다."),
    CATEGORY_UPDATED("카테고리가 정상적으로 수정되었습니다."),
    CATEGORY_DELETED("카테고리가 정상적으로 삭제되었습니다."),

    BUSINESS_SUCCESS("정상적으로 작성되었습니다.");

    private final String successMessage;
}

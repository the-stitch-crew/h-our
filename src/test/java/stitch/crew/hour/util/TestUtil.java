package stitch.crew.hour.util;

import org.springframework.security.authentication.TestingAuthenticationToken;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;

public class TestUtil {

    public static OrderProductCreateRequest orderProductCreateRequest(Long productId){
        return new OrderProductCreateRequest(
                "상품명1",
                2000L,
                    productId,
                1L,
                "옵션입니다."
        );
    }

    public static TestingAuthenticationToken createAdminAuthentication(String email) {
        return new TestingAuthenticationToken(
                new CurrentUser(1L, email, Role.ADMIN),
                null,
                "ROLE_ADMIN"
        );
    }
    public static TestingAuthenticationToken createUserAuthentication(String email) {
        return new TestingAuthenticationToken(
                new CurrentUser(2L, email, Role.USER),
                null,
                "ROLE_USER"
        );
    }
}

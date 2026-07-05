package stitch.crew.hour.util;

import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;

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
}

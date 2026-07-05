package stitch.crew.hour.util;

import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateRequest;

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

    public static ProductCreateRequest productCreateRequest(
            String summary,
            String description
    ){
        return new ProductCreateRequest(
                "상품명",
                2000L,
                summary,
                description
        );
    }
}

package stitch.crew.hour.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.category.domain.QCategory;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.QProduct;
import stitch.crew.hour.product.dto.ProductSearchResponse;
import stitch.crew.hour.product.dto.QProductSearchResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private final QProduct qProduct = QProduct.product;
    private final QCategory qCategory = QCategory.category;

    @Override
    public Page<ProductSearchResponse> getAllProduct(
            Pageable pageable,
            String categoryName
    ) {
        BooleanBuilder booleanBuilder = new BooleanBuilder()
                .and(containsCategoryName(categoryName))
                .and(qProduct.status.ne(ProductStatus.DELETED))
                .and(qProduct.status.ne(ProductStatus.DEACTIVATED));

        List<ProductSearchResponse> founded = jpaQueryFactory.select(
                        new QProductSearchResponse(
                                qProduct.id,
                                qProduct.name,
                                qProduct.price,
                                qProduct.thumbnail,
                                qProduct.status.stringValue(),
                                qProduct.summary
                        )
                ).from(qProduct)
                .where(booleanBuilder)
                .orderBy(qProduct.name.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Integer totalCnt = jpaQueryFactory.selectFrom(qProduct)
                .where(containsCategoryName(categoryName))
                .fetch().size();

        return new PageImpl<ProductSearchResponse>(
                founded,
                pageable,
                totalCnt
        );
    }

    public BooleanExpression containsCategoryName(String categoryName){
        return (Strings.isNotBlank(categoryName))?
                qProduct.category.name.containsIgnoreCase(categoryName)
                : null;
    }
}

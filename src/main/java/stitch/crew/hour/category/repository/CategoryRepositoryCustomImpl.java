package stitch.crew.hour.category.repository;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.domain.QCategory;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QCategory qCategory = QCategory.category;

	@Override
	public Page<Category> getAdminCategories(
		Pageable pageable,
		String keyword,
		Boolean includeDeleted
	) {
		BooleanBuilder booleanBuilder = new BooleanBuilder()
			.and(containsName(keyword))
			.and(excludeDeleted(includeDeleted));

		List<Category> categories = jpaQueryFactory.selectFrom(qCategory)
			.where(booleanBuilder)
			.orderBy(qCategory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCnt = jpaQueryFactory.select(qCategory.count())
			.from(qCategory)
			.where(booleanBuilder)
			.fetchOne();

		return new PageImpl<>(
			categories,
			pageable,
			totalCnt == null ? 0L : totalCnt
		);
	}

	private BooleanExpression containsName(String keyword) {
		return Strings.isNotBlank(keyword)
			? qCategory.name.containsIgnoreCase(keyword)
			: null;
	}

	private BooleanExpression excludeDeleted(Boolean includeDeleted) {
		return Boolean.TRUE.equals(includeDeleted) ? null : qCategory.deletedAt.isNull();
	}
}

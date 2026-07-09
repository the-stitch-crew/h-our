package stitch.crew.hour.user.repository;

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
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.QUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QUser qUser = QUser.user;

	@Override
	public Page<AdminUserSearchResponse> getAdminUsers(
		Pageable pageable,
		String keyword,
		Role role,
		Gender gender,
		Boolean blacklisted,
		Boolean includeDeleted
	) {
		BooleanBuilder booleanBuilder = new BooleanBuilder()
			.and(containsKeyword(keyword))
			.and(eqRole(role))
			.and(eqGender(gender))
			.and(eqBlacklisted(blacklisted))
			.and(excludeDeleted(includeDeleted));

		List<User> users = jpaQueryFactory.selectFrom(qUser)
			.where(booleanBuilder)
			.orderBy(qUser.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCnt = jpaQueryFactory.select(qUser.count())
			.from(qUser)
			.where(booleanBuilder)
			.fetchOne();

		return new PageImpl<>(
			users.stream()
				.map(AdminUserSearchResponse::from)
				.toList(),
			pageable,
			totalCnt == null ? 0L : totalCnt
		);
	}

	private BooleanExpression containsKeyword(String keyword) {
		return Strings.isNotBlank(keyword)
			? qUser.userName.containsIgnoreCase(keyword)
			.or(qUser.email.containsIgnoreCase(keyword))
			.or(qUser.phoneNumber.containsIgnoreCase(keyword))
			: null;
	}

	private BooleanExpression eqRole(Role role) {
		return role == null ? null : qUser.role.eq(role);
	}

	private BooleanExpression eqGender(Gender gender) {
		return gender == null ? null : qUser.gender.eq(gender);
	}

	private BooleanExpression eqBlacklisted(Boolean blacklisted) {
		return blacklisted == null ? null : qUser.idBlack.eq(blacklisted);
	}

	private BooleanExpression excludeDeleted(Boolean includeDeleted) {
		return Boolean.TRUE.equals(includeDeleted) ? null : qUser.deletedAt.isNull();
	}
}

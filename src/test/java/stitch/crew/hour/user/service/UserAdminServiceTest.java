package stitch.crew.hour.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.AdminUserDetailResponse;
import stitch.crew.hour.user.dto.AdminUserSearchResponse;
import stitch.crew.hour.user.dto.UserBlacklistUpdateRequest;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("UserAdminService의")
class UserAdminServiceTest {

	@Autowired
	UserAdminService userAdminService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EntityManager entityManager;

	int sequence;

	@Nested
	@DisplayName("Describe : getUsers()는")
	class Describe_getUsers {

		@Nested
		@DisplayName("Context : 필터 조건이 없는 경우")
		class Context_without_filter {

			@Test
			@DisplayName("It : 회원 목록을 최신 가입순으로 조회")
			void It_회원_목록을_최신가입순으로_조회() {
				// given
				User firstUser = saveUser("sort-first", Role.USER, Gender.MALE, false, todayAt(9));
				User thirdUser = saveUser("sort-third", Role.USER, Gender.MALE, false, todayAt(10));
				User secondUser = saveUser("sort-second", Role.USER, Gender.MALE, false, todayAt(11));

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"sort-",
					null,
					null,
					null,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(
						secondUser.getId(),
						thirdUser.getId(),
						firstUser.getId()
					);
			}

			@Test
			@DisplayName("It : 삭제된 회원을 제외하고 조회")
			void It_삭제된_회원을_제외하고_조회() {
				// given
				User activeUser = saveUser("deleted-filter-active", Role.USER, Gender.MALE, false, todayAt(9));
				User deletedUser = saveUser("deleted-filter-deleted", Role.USER, Gender.MALE, false, todayAt(10));
				deletedUser.delete();

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"deleted-filter-",
					null,
					null,
					null,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(activeUser.getId());
			}
		}

		@Nested
		@DisplayName("Context : includeDeleted=true가 주어진 경우")
		class Context_with_include_deleted {

			@Test
			@DisplayName("It : 삭제된 회원도 함께 조회")
			void It_삭제된_회원도_함께_조회() {
				// given
				User activeUser = saveUser("include-deleted-active", Role.USER, Gender.MALE, false, todayAt(9));
				User deletedUser = saveUser("include-deleted-deleted", Role.USER, Gender.MALE, false, todayAt(10));
				deletedUser.delete();

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"include-deleted-",
					null,
					null,
					null,
					true
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(
						deletedUser.getId(),
						activeUser.getId()
					);
			}
		}

		@Nested
		@DisplayName("Context : keyword가 주어진 경우")
		class Context_with_keyword {

			@Test
			@DisplayName("It : 이름, 이메일, 전화번호로 회원을 조회")
			void It_이름_이메일_전화번호로_회원을_조회() {
				// given
				User nameMatchedUser = saveUser("keyword-name-target", Role.USER, Gender.MALE, false, todayAt(9));
				User emailMatchedUser = saveUser("email-target", Role.USER, Gender.MALE, false, todayAt(10));
				User phoneMatchedUser = saveUser("phone-target", Role.USER, Gender.MALE, false, todayAt(11));
				saveUser("nothing", Role.USER, Gender.MALE, false, todayAt(12));

				// when
				Page<AdminUserSearchResponse> nameResponse = userAdminService.getUsers(0, 20, "keyword-name", null, null, null, false);
				Page<AdminUserSearchResponse> emailResponse = userAdminService.getUsers(0, 20, "email-target", null, null, null, false);
				Page<AdminUserSearchResponse> phoneResponse = userAdminService.getUsers(0, 20, phoneMatchedUser.getPhoneNumber(), null, null, null, false);

				// then
				Assertions.assertThat(nameResponse.getContent().getFirst().userId()).isEqualTo(nameMatchedUser.getId());
				Assertions.assertThat(emailResponse.getContent().getFirst().userId()).isEqualTo(emailMatchedUser.getId());
				Assertions.assertThat(phoneResponse.getContent().getFirst().userId()).isEqualTo(phoneMatchedUser.getId());
			}
		}

		@Nested
		@DisplayName("Context : role이 주어진 경우")
		class Context_with_role {

			@Test
			@DisplayName("It : USER 권한 회원만 조회")
			void It_USER_권한_회원만_조회() {
				// given
				User user = saveUser("role-user", Role.USER, Gender.MALE, false, todayAt(9));
				saveUser("role-admin", Role.ADMIN, Gender.MALE, false, todayAt(10));

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"role-",
					Role.USER,
					null,
					null,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(user.getId());
			}

			@Test
			@DisplayName("It : ADMIN 권한 회원만 조회")
			void It_ADMIN_권한_회원만_조회() {
				// given
				saveUser("role-user", Role.USER, Gender.MALE, false, todayAt(9));
				User admin = saveUser("role-admin", Role.ADMIN, Gender.MALE, false, todayAt(10));

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"role-",
					Role.ADMIN,
					null,
					null,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(admin.getId());
			}
		}

		@Nested
		@DisplayName("Context : gender가 주어진 경우")
		class Context_with_gender {

			@Test
			@DisplayName("It : 성별 조건에 맞는 회원만 조회")
			void It_성별_조건에_맞는_회원만_조회() {
				// given
				User maleUser = saveUser("gender-male", Role.USER, Gender.MALE, false, todayAt(9));
				saveUser("gender-female", Role.USER, Gender.FEMALE, false, todayAt(10));

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"gender-",
					null,
					Gender.MALE,
					null,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(maleUser.getId());
			}
		}

		@Nested
		@DisplayName("Context : blacklisted=true가 주어진 경우")
		class Context_with_blacklisted {

			@Test
			@DisplayName("It : 차단된 회원만 조회")
			void It_차단된_회원만_조회() {
				// given
				User blacklistedUser = saveUser("blacklisted-true", Role.USER, Gender.MALE, true, todayAt(9));
				saveUser("blacklisted-false", Role.USER, Gender.MALE, false, todayAt(10));

				// when
				Page<AdminUserSearchResponse> response = userAdminService.getUsers(
					0,
					20,
					"blacklisted-",
					null,
					null,
					true,
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminUserSearchResponse::userId)
					.containsExactly(blacklistedUser.getId());
			}
		}
	}

	@Nested
	@DisplayName("Describe : getUser()는")
	class Describe_getUser {

		@Nested
		@DisplayName("Context : 존재하는 회원 ID가 주어진 경우")
		class Context_with_existing_user_id {

			@Test
			@DisplayName("It : 관리자용 회원 상세 정보를 조회")
			void It_관리자용_회원상세를_조회() {
				// given
				User user = saveUser("detail-user", Role.ADMIN, Gender.FEMALE, true, todayAt(9));

				// when
				AdminUserDetailResponse response = userAdminService.getUser(user.getId());

				// then
				Assertions.assertThat(response.userId()).isEqualTo(user.getId());
				Assertions.assertThat(response.email()).isEqualTo(user.getEmail());
				Assertions.assertThat(response.role()).isEqualTo(Role.ADMIN.name());
				Assertions.assertThat(response.gender()).isEqualTo(Gender.FEMALE.name());
				Assertions.assertThat(response.blacklisted()).isTrue();
			}

			@Test
			@DisplayName("It : 삭제된 회원 상세에 deletedAt을 포함")
			void It_삭제된_회원상세에_deletedAt을_포함() {
				// given
				User user = saveUser("detail-deleted", Role.USER, Gender.MALE, false, todayAt(9));
				user.delete();

				// when
				AdminUserDetailResponse response = userAdminService.getUser(user.getId());

				// then
				Assertions.assertThat(response.deletedAt()).isNotNull();
			}
		}

		@Nested
		@DisplayName("Context : 존재하지 않는 회원 ID가 주어진 경우")
		class Context_with_not_existing_user_id {

			@Test
			@DisplayName("It : USER_DONT_EXISTS 예외가 발생")
			void It_USER_DONT_EXISTS_예외_발생() {
				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userAdminService.getUser(Long.MAX_VALUE)
				);

				// then
				Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
			}
		}
	}

	@Nested
	@DisplayName("Describe : updateUserRole()는")
	class Describe_updateUserRole {

		@Nested
		@DisplayName("Context : USER 또는 ADMIN 권한이 주어진 경우")
		class Context_with_allowed_role {

			@Test
			@DisplayName("It : 회원 권한을 변경")
			void It_회원권한을_변경() {
				// given
				User user = saveUser("role-update", Role.USER, Gender.MALE, false, todayAt(9));

				// when
				AdminUserDetailResponse response = userAdminService.updateUserRole(
					user.getId(),
					new UserRoleUpdateRequest(Role.ADMIN)
				);

				// then
				Assertions.assertThat(response.role()).isEqualTo(Role.ADMIN.name());
				Assertions.assertThat(user.getRole()).isEqualTo(Role.ADMIN);
			}
		}

		@Nested
		@DisplayName("Context : SUPER_ADMIN 권한이 주어진 경우")
		class Context_with_not_allowed_role {

			@Test
			@DisplayName("It : USER_ROLE_CHANGE_NOT_ALLOWED 예외가 발생")
			void It_USER_ROLE_CHANGE_NOT_ALLOWED_예외_발생() {
				// given
				User user = saveUser("role-denied", Role.USER, Gender.MALE, false, todayAt(9));

				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userAdminService.updateUserRole(
						user.getId(),
						new UserRoleUpdateRequest(Role.SUPER_ADMIN)
					)
				);

				// then
				Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED);
			}
		}
	}

	@Nested
	@DisplayName("Describe : updateBlacklist()는")
	class Describe_updateBlacklist {

		@Nested
		@DisplayName("Context : 차단 여부가 주어진 경우")
		class Context_with_blacklisted {

			@Test
			@DisplayName("It : 회원 차단 상태를 변경")
			void It_회원차단상태를_변경() {
				// given
				User user = saveUser("blacklist-update", Role.USER, Gender.MALE, false, todayAt(9));

				// when
				AdminUserDetailResponse response = userAdminService.updateBlacklist(
					user.getId(),
					new UserBlacklistUpdateRequest(true)
				);

				// then
				Assertions.assertThat(response.blacklisted()).isTrue();
				Assertions.assertThat(user.getIdBlack()).isTrue();
			}

			@Test
			@DisplayName("It : 회원 차단 상태를 해제")
			void It_회원차단상태를_해제() {
				// given
				User user = saveUser("blacklist-release", Role.USER, Gender.MALE, true, todayAt(9));

				// when
				AdminUserDetailResponse response = userAdminService.updateBlacklist(
					user.getId(),
					new UserBlacklistUpdateRequest(false)
				);

				// then
				Assertions.assertThat(response.blacklisted()).isFalse();
				Assertions.assertThat(user.getIdBlack()).isFalse();
			}
		}
	}

	private User saveUser(
		String prefix,
		Role role,
		Gender gender,
		Boolean blacklisted,
		LocalDateTime createdAt
	) {
		int id = ++sequence;
		User user = userRepository.save(
			new User(
				prefix + id,
				prefix + id + "@test.com",
				"1234",
				LocalDate.of(1990, 1, 1),
				role,
				gender,
				null,
				"0101234" + String.format("%04d", id),
				"KR",
				false,
				blacklisted
			)
		);
		setUserCreatedAt(user, createdAt);
		return user;
	}

	private void setUserCreatedAt(User user, LocalDateTime createdAt) {
		entityManager.flush();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<User> update = criteriaBuilder.createCriteriaUpdate(User.class);
		Root<User> root = update.from(User.class);
		Path<LocalDateTime> createdAtPath = root.get("createdAt");
		Path<Long> idPath = root.get("id");

		update.set(createdAtPath, createdAt);
		update.where(criteriaBuilder.equal(idPath, user.getId()));

		entityManager.createQuery(update).executeUpdate();
	}

	private LocalDateTime todayAt(int hour) {
		return LocalDate.now().atTime(hour, 0);
	}
}

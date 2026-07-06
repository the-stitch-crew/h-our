package stitch.crew.hour.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService의")
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Nested
	@DisplayName("Describe: signup 메서드는")
	class Describe_signup {

		@Nested
		@DisplayName("Context: 올바른 회원가입 요청이 주어지면")
		class Context_with_valid_request {

			SignupRequest request;

			@BeforeEach
			void setUp() {
				request = createSignupRequest();
			}

			@Test
			@DisplayName("It: 회원을 저장하고 userId를 반환한다")
			void it_saves_user_and_returns_user_id() {
				// given
				given(userRepository.existsByEmail(request.email())).willReturn(false);
				given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(false);
				given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
				given(userRepository.save(any(User.class))).willAnswer(invocation -> {
					User user = invocation.getArgument(0);
					ReflectionTestUtils.setField(user, "id", 1L);
					return user;
				});

				// when
				SignupResponse response = userService.signup(request);

				// then
				assertThat(response.userId()).isEqualTo(1L);

				ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
				verify(userRepository).save(captor.capture());
				User savedUser = captor.getValue();

				assertThat(savedUser.getUserName()).isEqualTo(request.userName());
				assertThat(savedUser.getEmail()).isEqualTo(request.email());
				assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
				assertThat(savedUser.getBirthDate()).isEqualTo(request.birthDate());
				assertThat(savedUser.getRole()).isEqualTo(Role.USER);
				assertThat(savedUser.getGender()).isEqualTo(request.gender());
				assertThat(savedUser.getPhoneNumber()).isEqualTo(request.phoneNumber());
				assertThat(savedUser.getNationality()).isEqualTo(request.nationality());
				assertThat(savedUser.getIsAuthLinked()).isFalse();
				assertThat(savedUser.getIdBlack()).isFalse();
			}
		}

		@Nested
		@DisplayName("Context: 이미 존재하는 이메일이 주어지면")
		class Context_with_existing_email {

			@Test
			@DisplayName("It: USER_EMAIL_ALREADY_EXISTS 예외가 발생한다")
			void it_throws_email_already_exists() {
				// given
				SignupRequest request = createSignupRequest();
				given(userRepository.existsByEmail(request.email())).willReturn(true);

				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userService.signup(request)
				);

				// then
				assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
				verify(userRepository, never()).existsByPhoneNumber(any());
				verify(passwordEncoder, never()).encode(any());
				verify(userRepository, never()).save(any(User.class));
			}
		}

		@Nested
		@DisplayName("Context: 이미 존재하는 전화번호가 주어지면")
		class Context_with_existing_phone_number {

			@Test
			@DisplayName("It: USER_PHONE_ALREADY_EXISTS 예외가 발생한다")
			void it_throws_phone_already_exists() {
				// given
				SignupRequest request = createSignupRequest();
				given(userRepository.existsByEmail(request.email())).willReturn(false);
				given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(true);

				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userService.signup(request)
				);

				// then
				assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_PHONE_ALREADY_EXISTS);
				verify(passwordEncoder, never()).encode(any());
				verify(userRepository, never()).save(any(User.class));
			}
		}
	}

	@Nested
	@DisplayName("Describe: getMyInfo 메서드는")
	class Describe_getMyInfo {

		@Test
		@DisplayName("It: 이메일에 해당하는 회원 정보를 반환한다")
		void it_returns_user_info() {
			// given
			User user = createUser();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.getMyInfo(user.getEmail());

			// then
			assertThat(response.userId()).isEqualTo(1L);
			assertThat(response.userName()).isEqualTo(user.getUserName());
			assertThat(response.email()).isEqualTo(user.getEmail());
			assertThat(response.birthDate()).isEqualTo(user.getBirthDate());
			assertThat(response.gender()).isEqualTo(user.getGender());
			assertThat(response.role()).isEqualTo(user.getRole());
			assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber());
			assertThat(response.nationality()).isEqualTo(user.getNationality());
			assertThat(response.isAuthLinked()).isFalse();
		}

		@Test
		@DisplayName("It: 이메일에 해당하는 회원이 없으면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_user_dont_exists_when_user_does_not_exist() {
			// given
			String email = "legend@naver.com";
			given(userRepository.findByEmail(email)).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getMyInfo(email)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}

		@Test
		@DisplayName("It: 삭제된 회원이면 ALREADY_DELETED 예외가 발생한다")
		void it_throws_already_deleted_when_user_is_deleted() {
			// given
			User user = createUser();
			user.delete();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getMyInfo(user.getEmail())
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_DELETED);
		}
	}

	private SignupRequest createSignupRequest() {
		return new SignupRequest(
			"대정수",
			"legend@naver.com",
			"password123",
			LocalDate.of(2000, 1, 1),
			Gender.MALE,
			"010-1234-5678",
			"KOREA"
		);
	}

	private User createUser() {
		User user = new User(
			"대정수",
			"legend@naver.com",
			"encodedPassword",
			LocalDate.of(2000, 1, 1),
			Role.USER,
			Gender.MALE,
			null,
			"010-1234-5678",
			"KOREA",
			false,
			false
		);
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}
}

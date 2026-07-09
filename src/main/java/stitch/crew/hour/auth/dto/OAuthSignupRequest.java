package stitch.crew.hour.auth.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import stitch.crew.hour.user.constant.Gender;

public record OAuthSignupRequest(

	@NotBlank(message = "회원가입 토큰은 필수입니다.")
	String signupToken,

	@NotNull(message = "생년월일은 필수입니다.")
	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	LocalDate birthDate,

	@NotNull(message = "성별은 필수입니다.")
	Gender gender,

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(
		regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$",
		message = "전화번호 형식은 010-0000-0000로 작성해주세요."
	)
	String phoneNumber,

	@NotBlank(message = "국적은 필수입니다.")
	String nationality
) {
}

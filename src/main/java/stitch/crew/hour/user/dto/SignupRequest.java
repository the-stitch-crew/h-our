package stitch.crew.hour.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import stitch.crew.hour.user.constant.Gender;

public record SignupRequest(

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 최대 50자까지 가능합니다.")
	String userName,

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 필수입니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	String password,

	/*
	YYYY-MM-DD 형식
	 */
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

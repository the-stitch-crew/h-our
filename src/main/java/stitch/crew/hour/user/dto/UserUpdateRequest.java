package stitch.crew.hour.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import stitch.crew.hour.user.constant.Gender;

public record UserUpdateRequest(

	@Pattern(regexp = ".*\\S.*", message = "이름은 공백일 수 없습니다.")
	@Size(max = 50, message = "이름은 최대 50자까지 가능합니다.")
	String userName,

	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	LocalDate birthDate,

	Gender gender,

	@Pattern(
		regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$",
		message = "전화번호 형식은 010-0000-0000로 작성해주세요."
	)
	String phoneNumber,

	@Pattern(regexp = ".*\\S.*", message = "국적은 공백일 수 없습니다.")
	String nationality

) {
}

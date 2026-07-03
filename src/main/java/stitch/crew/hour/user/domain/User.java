package stitch.crew.hour.user.domain;

import java.time.LocalDateTime;

import org.apache.logging.log4j.util.Strings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.user.constant.Role;

@Entity
@Getter
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String userName;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private LocalDateTime birthDate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	private String gender;

	@Column()
	private String provider;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String nationality;

	@Column(nullable = false)
	private Boolean isAuthLinked;

	@Column(nullable = false)
	private Boolean idBlack;

	public User(
		String userName,
		String email,
		String password,
		LocalDateTime birthDate,
		Role role,
		String gender,
		String provider,
		String phoneNumber,
		String nationality,
		Boolean isAuthLinked,
		Boolean idBlack
	) {
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.birthDate = birthDate;
		this.role = role;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.nationality = nationality;
		this.isAuthLinked = isAuthLinked;
		this.idBlack = idBlack;
		if(Strings.isNotBlank(provider)) this.provider = provider;
	}


}

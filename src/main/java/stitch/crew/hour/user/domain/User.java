package stitch.crew.hour.user.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;

@Entity
@Getter
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String userName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private LocalDate birthDate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column()
	private String provider;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Column(nullable = false)
	private String nationality;

	@Column(nullable = false)
	private Boolean isAuthLinked;

	@Column(nullable = false)
	private Boolean idBlack;

	@OneToMany(mappedBy = "orderer")
	private List<Order> orders = new ArrayList<>();

	public User(
		String userName,
		String email,
		String password,
		LocalDate birthDate,
		Role role,
		Gender gender,
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

	public void updateProfile(
		String userName,
		LocalDate birthDate,
		Gender gender,
		String phoneNumber,
		String nationality
	) {
		if (userName != null) {
			this.userName = userName;
		}
		if (birthDate != null) {
			this.birthDate = birthDate;
		}
		if (gender != null) {
			this.gender = gender;
		}
		if (phoneNumber != null) {
			this.phoneNumber = phoneNumber;
		}
		if (nationality != null) {
			this.nationality = nationality;
		}
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

}

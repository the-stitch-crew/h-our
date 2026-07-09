package stitch.crew.hour.user.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.apache.logging.log4j.util.Strings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.category.domain.Category;
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

	@OneToOne(mappedBy = "user")
	private Cart cart;

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

	public void setOAuth(String provider) {
		if (Strings.isNotBlank(provider)) {
			this.provider = provider;
		}
		this.isAuthLinked = true;
	}

	public void unsetOAuth(String provider) {
		this.provider = null;
		this.isAuthLinked = false;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void changeRole(Role role) {
		this.role = role;
	}

	public void changeBlacklisted(Boolean blacklisted) {
		this.idBlack = blacklisted;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

	public void addCart(Cart cart){ this.cart = cart;}

	public void setNoCart(){
		this.cart = null;
	}
}

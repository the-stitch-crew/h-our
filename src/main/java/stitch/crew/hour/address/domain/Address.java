package stitch.crew.hour.address.domain;

import org.apache.logging.log4j.util.Strings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.user.domain.User;

@Entity
@Getter
@Table(name = "addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 20)
	private String zipCode;

	@Column(length = 255)
	private String oldAddress;

	@Column(nullable = false, length = 255)
	private String roadAddress;

	@Column(nullable = false, length = 255)
	private String addressDetail;

	@Column(nullable = false)
	private Boolean isMain;

	public Address(
		User user,
		String zipCode,
		String oldAddress,
		String roadAddress,
		String addressDetail,
		Boolean isMain
	) {
		this.user = user;
		this.zipCode = zipCode;
		this.roadAddress = roadAddress;
		this.addressDetail = addressDetail;
		this.isMain = isMain;
		if (Strings.isNotBlank(oldAddress)) {
			this.oldAddress = oldAddress;
		}
	}

	public void update(
		String zipCode,
		String oldAddress,
		String roadAddress,
		String addressDetail
	) {
		if (zipCode != null) {
			this.zipCode = zipCode;
		}
		if (oldAddress != null) {
			this.oldAddress = oldAddress;
		}
		if (roadAddress != null) {
			this.roadAddress = roadAddress;
		}
		if (addressDetail != null) {
			this.addressDetail = addressDetail;
		}
	}

	public void setMain() {
		this.isMain = true;
	}

	public void unsetMain() {
		this.isMain = false;
	}
}

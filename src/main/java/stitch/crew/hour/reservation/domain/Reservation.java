package stitch.crew.hour.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;

@Entity
@Getter
@Table(name="reservations")
@NoArgsConstructor
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

}

package stitch.crew.hour.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name="reservations")
@NoArgsConstructor
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer deposit;  //예약금

    @Column(nullable = false)
    private Integer price;

    private String request;   //추가 요청 내용

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ReservationState state;



}

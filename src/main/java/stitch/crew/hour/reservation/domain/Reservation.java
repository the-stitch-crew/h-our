package stitch.crew.hour.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

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

    @Column(nullable = false)
    private UUID reservationNumber;

    @Setter
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
//    @JoinColumn(name = "client_id", nullable = false)
    @JoinColumn(name = "client_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Reservation(LocalDate date, LocalTime startTime, LocalTime endTime, Integer deposit, Integer price, String request, User user, Lesson lesson) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deposit = deposit;
        this.price = price;
        this.request = request;
        this.reservationNumber = UUID.randomUUID();
        this.status = ReservationStatus.PENDING;
        this.user = user;
        this.lesson = lesson;
    }


}

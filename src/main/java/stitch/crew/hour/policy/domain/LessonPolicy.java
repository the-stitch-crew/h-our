package stitch.crew.hour.policy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer reservationAvailableDays;

    @Column(nullable = false)
    private Integer reservationDeadlineDays;

    @Column(nullable = false)
    private Integer cancelDeadlineDays;

    @Column(nullable = false)
    private Integer depositDeadlineDays;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @ElementCollection
    private Set<WeekDay> regularDays;

    public LessonPolicy(Integer reservationAvailableDays, Integer reservationDeadlineDays, Integer cancelDeadlineDays, Integer depositDeadlineDays, LocalTime startTime, LocalTime endTime, Set<WeekDay> regularDays) {
        this.reservationAvailableDays = reservationAvailableDays;
        this.reservationDeadlineDays = reservationDeadlineDays;
        this.cancelDeadlineDays = cancelDeadlineDays;
        this.depositDeadlineDays = depositDeadlineDays;
        this.startTime = startTime;
        this.endTime = endTime;
        this.regularDays = regularDays;
    }
}

package stitch.crew.hour.policy.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.policy.dto.LessonPolicyRequest;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;

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
    private Integer depositAmount;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @ElementCollection
    private Set<WeekDay> regularDays;


    public LessonPolicy(Integer reservationAvailableDays, Integer reservationDeadlineDays, Integer cancelDeadlineDays, Integer depositAmount, LocalTime startTime, LocalTime endTime, Set<WeekDay> regularDays) {
        this.reservationAvailableDays = reservationAvailableDays;
        this.reservationDeadlineDays = reservationDeadlineDays;
        this.cancelDeadlineDays = cancelDeadlineDays;
        this.depositAmount = depositAmount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.regularDays = regularDays;
    }

    public static LessonPolicyResponse from(LessonPolicy policy) {
        return new LessonPolicyResponse(
                policy.getReservationAvailableDays(),
                policy.getReservationDeadlineDays(),
                policy.getCancelDeadlineDays(),
                policy.getDepositAmount(),
                policy.getStartTime(),
                policy.getEndTime(),
                policy.getRegularDays()
        );
    }

    public void update(@Valid LessonPolicyRequest request) {
        this.reservationAvailableDays = request.reservationAvailableDays();
        this.reservationDeadlineDays = request.reservationDeadlineDays();
        this.cancelDeadlineDays = request.cancelDeadlineDays();
        this.depositAmount = request.depositAmount();
        this.startTime = request.startTime();
        this.endTime = request.endTime();
        this.regularDays = request.regularDays();
    }
}

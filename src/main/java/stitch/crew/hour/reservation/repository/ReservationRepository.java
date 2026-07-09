package stitch.crew.hour.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stitch.crew.hour.reservation.domain.Reservation;
import stitch.crew.hour.reservation.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        select count(r)>0
        from Reservation r
        where r.date = :date
        and r.status <> :status
        and (r.startTime < :endTime
        or r.endTime < :startTime)
    """)
    boolean existsByDateAndTimeOverlap(LocalDate date, LocalTime startTime,  LocalTime endTime,  ReservationStatus status);

    List<Reservation> findAllByDateBetweenAndStatusNotOrderByDateAscStartTimeAsc(LocalDate dateAfter, LocalDate dateBefore, ReservationStatus status);

    Page<Reservation> findAllByUserIdOrderByDateDescStartTimeDesc(Long userId, Pageable pageable);

    Page<Reservation> findAllByUserIdAndStatusInOrderByDateDescStartTimeDesc(Long userId, List<ReservationStatus> statuses, Pageable pageable);

    Page<Reservation> findAllByDateEquals(LocalDate date, Pageable pageable);
    Page<Reservation> findAllByDateEqualsAndStatusEquals(LocalDate date, ReservationStatus status, Pageable pageable);

    Page<Reservation> findAllByDateBetween(LocalDate dateAfter, LocalDate dateBefore,  Pageable pageable);
    Page<Reservation> findAllByDateBetweenAndStatusEquals(LocalDate dateAfter, LocalDate dateBefore,  ReservationStatus status, Pageable pageable);

    int countByUserId(Long userId);
    int countByUserIdAndStatusEquals(Long userId, ReservationStatus status);
    Reservation findLastByUserIdAndStatusEquals(Long userId, ReservationStatus status);


}

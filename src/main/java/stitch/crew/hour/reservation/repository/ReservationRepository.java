package stitch.crew.hour.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stitch.crew.hour.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        select count(r)>0
        from Reservation r
        where r.date = :date
        and (r.startTime < :endTime
        or r.endTime < :startTime)
    """)
    boolean existsByDateAndTimeOverlap(LocalDate date, LocalTime startTime,  LocalTime endTime);

    List<Reservation> findAllByDateBetween(LocalDate dateAfter, LocalDate dateBefore);
}

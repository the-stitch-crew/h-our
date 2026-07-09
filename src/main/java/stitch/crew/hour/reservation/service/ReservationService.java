package stitch.crew.hour.reservation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.lock.Lock;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.service.LessonService;
import stitch.crew.hour.policy.domain.LessonPolicy;
import stitch.crew.hour.policy.service.LessonPolicyService;
import stitch.crew.hour.reservation.domain.Reservation;
import stitch.crew.hour.reservation.domain.ReservationStatus;
import stitch.crew.hour.reservation.dto.*;
import stitch.crew.hour.reservation.repository.ReservationRepository;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.service.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final LessonPolicyService lessonPolicyService;
    private final LessonService lessonService;

    // 손님이 예약
    @Lock(key = Lock.Key.RESERVATION, waitTime = 500L, leaseTime = 700L, timeunit =  TimeUnit.MILLISECONDS)
    public void saveReservation(CurrentUser currentUser, ReservationRequest request) {
        User user = userService.getActiveUserFromCurrentUser(currentUser);
        Lesson lesson = lessonService.getLessonById(request.lessonId());
        validatePolicy(request);
        validateLesson(lesson, request);
        validateReservation(request);
        Reservation reservation = new Reservation(request.date(), request.startTime(), request.endTime(), request.deposit(), request.price(), request.request(),user, lesson);
        reservationRepository.save(reservation);
    }

    //예약 전 기존 예약 확인
    @Transactional(readOnly = true)
    public List<ExistReservationResponse> getExistReservations(LocalDate fromDate, LocalDate toDate) {
        List<Reservation> reservations = reservationRepository.findAllByDateBetweenAndStatusNotOrderByDateAscStartTimeAsc(fromDate, toDate, ReservationStatus.CANCELED);
        return reservations.stream().map(ExistReservationResponse::from).toList();
    }

    //예약자가 예약 목록 확인
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getMyReservations(CurrentUser currentUser, Boolean isOngoing, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        Page<Reservation> reservations;
        if (isOngoing) {
            reservations = reservationRepository.findAllByUserIdAndStatusInOrderByDateDescStartTimeDesc(currentUser.getId(), List.of(ReservationStatus.PENDING, ReservationStatus.APPROVED), pageable);
        }
        else {
            reservations = reservationRepository.findAllByUserIdOrderByDateDescStartTimeDesc(currentUser.getId(), pageable);
        }
        return reservations.map(ReservationResponse::from);

    }

    //예약자가 예약 상세 확인
    @Transactional(readOnly = true)
    public ReservationResponse getMyReservation(CurrentUser currentUser, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        PreConditions.check(!Objects.equals(reservation.getUser().getId(), currentUser.getId()), ErrorCode.RESERVATION_NOT_CLIENT);
        return ReservationResponse.from(reservation);
    }

    //관리자가 예약 목록 확인
    @Transactional(readOnly = true)
    public Page<ReservationAdminResponse> getAdminReservations(Boolean isDate, LocalDate date, @Valid ReservationWeekRequest request, String status, Integer page) {
        Sort sort;
        Pageable pageable;
        Page<Reservation> reservations;
        if (isDate) {
            sort = Sort.by(Sort.Direction.ASC, "startTime");
            pageable = PageRequest.of(page-1, 10, sort);
            if (status.equals("ALL")) {
                reservations = reservationRepository.findAllByDateEquals(date,  pageable);
            } else {
                ReservationStatus rStatus = ReservationStatus.of(status);
                reservations = reservationRepository.findAllByDateEqualsAndStatusEquals(date, rStatus, pageable);
            }
        }
        else {
            LocalDate[] weekRange = getWeekRange(request);
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "date"), new Sort.Order(Sort.Direction.ASC, "startTime"));
            pageable = PageRequest.of(page-1, 10, sort);
            if (status.equals("ALL")) {
                reservations = reservationRepository.findAllByDateBetween(weekRange[0], weekRange[1], pageable);
            } else {
                ReservationStatus rStatus = ReservationStatus.of(status);
                reservations = reservationRepository.findAllByDateBetweenAndStatusEquals(weekRange[0], weekRange[1], rStatus, pageable);
            }
        }
        return reservations.map(r-> {
            User user = r.getUser();
            int reservationCount = reservationRepository.countByUserId(user.getId());
            int visitCount = reservationRepository.countByUserIdAndStatusEquals(user.getId(),  ReservationStatus.COMPLETED);
            LocalDate lastVisitDate = visitCount==0? null: reservationRepository.findLastByUserIdAndStatusEquals(user.getId(), ReservationStatus.COMPLETED).getDate();
            CustomerSummaryResponse customer = CustomerSummaryResponse.from(user,  reservationCount, visitCount, lastVisitDate);
            return ReservationAdminResponse.from(r, customer, r.getLesson());
        });
    }


    //사용자가 결제를 위해 예약 번호 조회
    @Transactional(readOnly = true)
    public UUID getReservationNumber(CurrentUser currentUser, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        //예약의 당사자가 아니면 거부
        PreConditions.check(!reservation.getUser().getId().equals(currentUser.getId()), ErrorCode.RESERVATION_NOT_CLIENT);
        //결제 준비 상태가 아니라면 거부
        PreConditions.check(reservation.getStatus() != ReservationStatus.PENDING,  ErrorCode.RESERVATION_NOT_PENDING);
        return reservation.getReservationNumber();
    }

    @Transactional(readOnly = true)
    public Reservation getReservationWithNumber(UUID reservationNumber) {
        return reservationRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    //관리자가 예약을 완료 또는 노쇼로 변경할 경우
    @Transactional
    public void upateAdminStatus(Long reservationId, ReservationStatus status) {
        //id가 유효하지 않을 경우 에러
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        //변경할 상태값이 완료 또는 노쇼가 아닐 경우 에러
        PreConditions.check(!Set.of(ReservationStatus.COMPLETED, ReservationStatus.NO_SHOW).contains(status),ErrorCode.RESERVATION_NOT_DONE);
        //기존 상태가 APPROVE가 아니라면 에러
        PreConditions.check(reservation.getStatus() != ReservationStatus.APPROVED,ErrorCode.RESERVATION_NOT_APPROVE);
        reservation.setStatus(status);
    }

    //결제시 예약 상태가 APPROVED로 변경
    @Transactional
    public void confirmReservation(Reservation reservation) {
        reservation.confirm();
    }

    //환불시 예약 상태가 CANCELED로 변경
    @Transactional
    public void cancelReservation(Reservation reservation) {
        reservation.cancel();
    }

    //그 주의 일요일과 토요일 날짜 구하기
    private LocalDate[] getWeekRange(ReservationWeekRequest request) {
        //그 달의 첫번째 날 찾기
        LocalDate firstDay = LocalDate.of(request.year(), request.month(), 1);
        //해당 월의 첫 번째 일요일
        LocalDate firstSunday = firstDay.with(
                TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)
        );
        //원하는 주차 시작일
        LocalDate startDate = firstSunday.plusWeeks(request.week() - 1);
        //토요일
        LocalDate endDate = startDate.plusDays(6);
        return new LocalDate[]{startDate, endDate};
    }

    //정책에 어긋나지 않는지 체크
    private void validatePolicy(ReservationRequest request) {
        //정책 불러오기
        LessonPolicy policy = lessonPolicyService.getPolicyForReservation();

        //예약 가능한 날짜 내인지 체크
        LocalDate targetDate = request.date();
        long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        PreConditions.check(days < 0,ErrorCode.PAST_DATE_RESERVATION);
        PreConditions.check(days > policy.getReservationAvailableDays(), ErrorCode.NOT_AVAILABLE_DAYS);
        //예약 가능한 최소 날짜인지 체크
        PreConditions.check(days < policy.getReservationDeadlineDays(), ErrorCode.RESERVATION_DEADLINE_PASSED);

        //휴무요일에 해당하는지 체크
        DayOfWeek dayOfWeek = targetDate.getDayOfWeek();
        PreConditions.check(policy.getClosedDays().contains(dayOfWeek), ErrorCode.CLOSED_DAYS);

        //오픈~마감시간 내인지 체크
        LocalTime startTime = request.startTime();
        LocalTime endTime = request.endTime();
        PreConditions.check(startTime.isBefore(policy.getStartTime()), ErrorCode.BEFORE_STARTTIME);
        PreConditions.check(endTime.isAfter(policy.getEndTime()), ErrorCode.AFTER_ENDTIME);
        // 시작/종료 시간 역전 체크
        PreConditions.check(endTime.isBefore(startTime), ErrorCode.AFTER_ENDTIME);

        //예약금 맞는지 체크
        PreConditions.check(!Objects.equals(request.deposit(), policy.getDepositAmount()), ErrorCode.DEPOSIT_NOT_MATCH);
    }

    //수업 내용과 검증
    private void validateLesson(Lesson lesson, ReservationRequest request) {
        //수업시간 검증
        long hours = ChronoUnit.HOURS.between(request.startTime(), request.endTime());
        PreConditions.check(hours < 0, ErrorCode.INVALID_TIME);
        PreConditions.check(hours != lesson.getDuration(), ErrorCode.DURATION_NOT_MATCH);
        //가격 검증
        PreConditions.check(!Objects.equals(request.price(), lesson.getPrice()), ErrorCode.PRICE_NOT_MATCH);
    }

    //기존의 예약과 겹치지 않은지 체크
    private void validateReservation(ReservationRequest request) {
        boolean existReservation = reservationRepository.existsByDateAndTimeOverlap(request.date(), request.startTime(), request.endTime(), ReservationStatus.CANCELED);
        PreConditions.check(existReservation,ErrorCode.RESERVATION_OVERLAP);
    }
}

package stitch.crew.hour.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.reservation.domain.ReservationStatus;
import stitch.crew.hour.reservation.dto.ReservationAdminResponse;
import stitch.crew.hour.reservation.dto.ReservationWeekRequest;
import stitch.crew.hour.reservation.service.ReservationService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {
    private final ReservationService reservationService;

    //관리자가 예약 목록 조회(날짜를 우선으로 조회)
    @GetMapping
    public ResponseEntity<ApiResponses<Page<ReservationAdminResponse>>> getReservations(
            @RequestParam(required = false) LocalDate date,
            @ModelAttribute @Valid ReservationWeekRequest request,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "1") Integer page
    ) {
        //날짜가 있을때 그대로 true
        boolean isDate = true;
        //날짜가 없는데 년월주값도 없다면 오류 있다면 isDate = false로 값 넘김
        if (date == null) {
            if (request.year()==null || request.month() == null || request.week() == null) throw new BusinessException(ErrorCode.INVALID_DATE);
            else isDate = false;
        }
        Page<ReservationAdminResponse> response = reservationService.getAdminReservations(isDate, date, request, status, page);
        return ApiResult.ok(SuccessCode.RESERVATION_READ,  response);
    }

    @PatchMapping("/status/{reservationId}")
    public ResponseEntity<ApiResponses<Void>> updateStatus(@PathVariable Long reservationId,
                                                           @RequestParam ReservationStatus status) {
        reservationService.upateAdminStatus(reservationId, status);
        return ApiResult.ok(SuccessCode.RESERVATION_UPDATED);
    }
}

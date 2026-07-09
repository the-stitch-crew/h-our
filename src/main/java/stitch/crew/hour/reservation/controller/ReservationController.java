package stitch.crew.hour.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.reservation.dto.ExistReservationResponse;
import stitch.crew.hour.reservation.dto.ReservationRequest;
import stitch.crew.hour.reservation.dto.ReservationResponse;
import stitch.crew.hour.reservation.service.ReservationService;
import stitch.crew.hour.user.domain.CurrentUser;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    //고객이 예약
    @PostMapping
    public ResponseEntity<ApiResponses<Void>> saveReservation(@AuthenticationPrincipal CurrentUser currentUser,
                                                              @RequestBody @Valid ReservationRequest request) {
        reservationService.saveReservation(currentUser, request);
        return ApiResult.ok(SuccessCode.RESERVATION_CREATED);
    }

    //예약시 기존의 예약 확인
    @GetMapping
    public ResponseEntity<ApiResponses<List<ExistReservationResponse>>> getExistReservations(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        PreConditions.check(fromDate == null, ErrorCode.FROM_DATE_REQUIRED);
        PreConditions.check(toDate == null, ErrorCode.TO_DATE_REQUIRED);
        List<ExistReservationResponse> response = reservationService.getExistReservations(fromDate, toDate);
        return ApiResult.ok(SuccessCode.RESERVATION_READ,  response);
    }

    //예약자가 예약 목록 확인
    @GetMapping("/my/reservations")
    public ResponseEntity<ApiResponses<Page<ReservationResponse>>> getExistReservations(@AuthenticationPrincipal CurrentUser currentUser,
                                                                                        @RequestParam(required = false, defaultValue = "true") Boolean isOngoing,
                                                                                        @RequestParam(required = false, defaultValue = "1") Integer page ) {
        Page<ReservationResponse> response = reservationService.getMyReservations(currentUser, isOngoing, page);
        return ApiResult.ok(SuccessCode.RESERVATION_READ,  response);
    }
    //예약자가 예약 상세 확인
}

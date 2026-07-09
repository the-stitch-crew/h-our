package stitch.crew.hour.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.reservation.dto.ReservationRequest;
import stitch.crew.hour.reservation.service.ReservationService;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponses<Void>> saveReservation(@AuthenticationPrincipal CurrentUser currentUser,
                                                              @RequestBody @Valid ReservationRequest request) {
        reservationService.saveReservation(currentUser, request);
        return ApiResult.ok(SuccessCode.RESERVATION_CREATED);
    }
}

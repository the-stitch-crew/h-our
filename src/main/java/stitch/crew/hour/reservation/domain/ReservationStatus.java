package stitch.crew.hour.reservation.domain;

import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

public enum ReservationStatus {
    PENDING, APPROVED, COMPLETED, CANCELED, NO_SHOW;

    public static ReservationStatus of(String status) {
        for (ReservationStatus reservationStatus : ReservationStatus.values()) {
            if (reservationStatus.name().equalsIgnoreCase(status)) {
                return reservationStatus;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
    }
}

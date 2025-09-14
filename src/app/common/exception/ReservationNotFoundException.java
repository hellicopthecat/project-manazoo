package app.common.exception;

/**
 * 예약을 찾을 수 없을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class ReservationNotFoundException extends BusinessException {
    
    /**
     * 예약 ID로 예외를 생성합니다.
     * 
     * @param reservationId 찾을 수 없는 예약 ID
     */
    public ReservationNotFoundException(int reservationId) {
        super("RESERVATION_NOT_FOUND",
              "예약을 찾을 수 없습니다. ID: " + reservationId,
              "요청하신 예약 정보를 찾을 수 없습니다. 예약 번호를 다시 확인해 주세요.");
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public ReservationNotFoundException(String message) {
        super("RESERVATION_NOT_FOUND", message);
    }
}
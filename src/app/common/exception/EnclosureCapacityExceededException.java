package app.common.exception;

/**
 * 사육장 용량이 초과되었을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class EnclosureCapacityExceededException extends BusinessException {
    
    /**
     * 사육장 용량 초과 예외를 생성합니다.
     * 
     * @param enclosureId 사육장 ID
     * @param currentOccupancy 현재 수용량
     * @param maxCapacity 최대 수용량
     */
    public EnclosureCapacityExceededException(int enclosureId, int currentOccupancy, int maxCapacity) {
        super("ENCLOSURE_CAPACITY_EXCEEDED",
              String.format("사육장 용량 초과. ID: %d, 현재: %d, 최대: %d", enclosureId, currentOccupancy, maxCapacity),
              String.format("사육장 용량이 초과되었습니다. (현재 %d마리, 최대 %d마리)", currentOccupancy, maxCapacity));
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public EnclosureCapacityExceededException(String message) {
        super("ENCLOSURE_CAPACITY_EXCEEDED", message);
    }
}
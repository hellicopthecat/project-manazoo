package app.common.exception;

/**
 * 동물 데이터가 유효하지 않을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class InvalidAnimalDataException extends BusinessException {
    
    /**
     * 유효하지 않은 데이터 필드와 함께 예외를 생성합니다.
     * 
     * @param field 유효하지 않은 필드명
     * @param value 잘못된 값
     * @param reason 이유
     */
    public InvalidAnimalDataException(String field, String value, String reason) {
        super("INVALID_ANIMAL_DATA",
              String.format("동물 데이터 검증 실패. 필드: %s, 값: %s, 이유: %s", field, value, reason),
              String.format("%s 정보가 올바르지 않습니다. %s", field, reason));
    }
    
    /**
     * 일반적인 데이터 검증 오류로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public InvalidAnimalDataException(String message) {
        super("INVALID_ANIMAL_DATA", message);
    }
}
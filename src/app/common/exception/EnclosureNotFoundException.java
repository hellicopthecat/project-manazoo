package app.common.exception;

/**
 * 사육장을 찾을 수 없을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class EnclosureNotFoundException extends BusinessException {
    
    /**
     * 사육장 ID로 예외를 생성합니다.
     * 
     * @param enclosureId 찾을 수 없는 사육장 ID
     */
    public EnclosureNotFoundException(int enclosureId) {
        super("ENCLOSURE_NOT_FOUND",
              "사육장을 찾을 수 없습니다. ID: " + enclosureId,
              "요청하신 사육장을 찾을 수 없습니다. 사육장 번호를 다시 확인해 주세요.");
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public EnclosureNotFoundException(String message) {
        super("ENCLOSURE_NOT_FOUND", message);
    }
}
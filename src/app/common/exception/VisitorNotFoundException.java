package app.common.exception;

/**
 * 방문객을 찾을 수 없을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class VisitorNotFoundException extends BusinessException {
    
    /**
     * 방문객 ID로 예외를 생성합니다.
     * 
     * @param visitorId 찾을 수 없는 방문객 ID
     */
    public VisitorNotFoundException(int visitorId) {
        super("VISITOR_NOT_FOUND",
              "방문객을 찾을 수 없습니다. ID: " + visitorId,
              "요청하신 방문객 정보를 찾을 수 없습니다. 방문객 번호를 다시 확인해 주세요.");
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public VisitorNotFoundException(String message) {
        super("VISITOR_NOT_FOUND", message);
    }
}
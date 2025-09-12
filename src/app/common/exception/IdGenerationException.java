package app.common.exception;

/**
 * ID 생성 과정에서 발생하는 예외를 처리하는 도메인 특화 예외 클래스입니다.
 * 
 * <p>다음과 같은 상황에서 발생합니다:
 * <ul>
 *   <li>지원되지 않는 클래스에서 ID 생성 요청</li>
 *   <li>데이터베이스 연결 실패</li>
 *   <li>id_generator 테이블 관련 SQL 오류</li>
 *   <li>트랜잭션 처리 중 오류</li>
 * </ul>
 */
public class IdGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 메시지만으로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public IdGenerationException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외 (예: SQLException)
     */
    public IdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인 예외로만 예외를 생성합니다.
     * 
     * @param cause 원인 예외
     */
    public IdGenerationException(Throwable cause) {
        super(cause);
    }
}

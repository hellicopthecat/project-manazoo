package app.common.exception;

/**
 * 데이터베이스 연결 오류가 발생할 때 발생하는 시스템 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class DatabaseConnectionException extends BusinessException {
    
    /**
     * 데이터베이스 연결 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super("DATABASE_CONNECTION_ERROR", message, cause);
    }
    
    /**
     * 기본 데이터베이스 연결 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public DatabaseConnectionException(String message) {
        super("DATABASE_CONNECTION_ERROR", 
              message,
              "데이터베이스 연결에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
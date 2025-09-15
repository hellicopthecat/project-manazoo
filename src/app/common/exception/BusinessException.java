package app.common.exception;

import java.time.LocalDateTime;

/**
 * 비즈니스 로직에서 발생하는 모든 예외의 기반 클래스입니다.
 * 도메인별 예외 클래스들이 이 클래스를 상속받아 구현됩니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public abstract class BusinessException extends Exception {
    private final String errorCode;
    private final LocalDateTime timestamp;
    private final String userMessage;
    
    /**
     * 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드 (예: "ANIMAL_NOT_FOUND")
     * @param message 시스템 메시지 (개발자용)
     * @param userMessage 사용자 친화적 메시지
     */
    protected BusinessException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 비즈니스 예외를 생성합니다. (사용자 메시지 = 시스템 메시지)
     * 
     * @param errorCode 에러 코드
     * @param message 메시지
     */
    protected BusinessException(String errorCode, String message) {
        this(errorCode, message, message);
    }
    
    /**
     * 원인 예외와 함께 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드
     * @param message 메시지
     * @param cause 원인 예외
     */
    protected BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = message;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 에러 코드를 반환합니다.
     * 
     * @return 에러 코드
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 예외 발생 시각을 반환합니다.
     * 
     * @return 발생 시각
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * 사용자에게 표시할 친화적인 메시지를 반환합니다.
     * 
     * @return 사용자 메시지
     */
    public String getUserMessage() {
        return userMessage;
    }
    
    @Override
    public String toString() {
        return String.format("%s{errorCode='%s', timestamp=%s, message='%s'}", 
                getClass().getSimpleName(), errorCode, timestamp, getMessage());
    }
}
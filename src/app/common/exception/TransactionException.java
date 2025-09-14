package app.common.exception;

/**
 * 트랜잭션 처리 중 오류가 발생할 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class TransactionException extends BusinessException {
    
    /**
     * 트랜잭션 예외를 생성합니다.
     * 
     * @param operation 실행 중이던 작업명
     * @param cause 원인 예외
     */
    public TransactionException(String operation, Throwable cause) {
        super("TRANSACTION_ERROR",
              String.format("트랜잭션 처리 실패: %s", operation),
              cause);
    }
    
    /**
     * 일반적인 트랜잭션 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public TransactionException(String message) {
        super("TRANSACTION_ERROR", 
              message,
              "데이터 처리 중 오류가 발생했습니다. 변경사항이 취소되었습니다.");
    }
}
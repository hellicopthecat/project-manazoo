package app.common.exception;

/**
 * 결제 처리 중 오류가 발생할 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class PaymentException extends BusinessException {
    
    /**
     * 결제 오류 예외를 생성합니다.
     * 
     * @param amount 결제 시도 금액
     * @param reason 결제 실패 이유
     */
    public PaymentException(int amount, String reason) {
        super("PAYMENT_ERROR",
              String.format("결제 처리 실패. 금액: %d, 이유: %s", amount, reason),
              String.format("결제 처리 중 오류가 발생했습니다. %s", reason));
    }
    
    /**
     * 일반적인 결제 오류로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public PaymentException(String message) {
        super("PAYMENT_ERROR", message);
    }
    
    /**
     * 원인 예외와 함께 결제 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public PaymentException(String message, Throwable cause) {
        super("PAYMENT_ERROR", message, cause);
    }
}
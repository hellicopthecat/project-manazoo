package app.common.exception;

/**
 * 애플리케이션 설정 오류가 발생할 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class ConfigurationException extends BusinessException {
    
    /**
     * 설정 오류 예외를 생성합니다.
     * 
     * @param configKey 잘못된 설정 키
     * @param expectedValue 예상 값 형식
     * @param actualValue 실제 값
     */
    public ConfigurationException(String configKey, String expectedValue, String actualValue) {
        super("CONFIGURATION_ERROR",
              String.format("설정 오류. 키: %s, 예상: %s, 실제: %s", configKey, expectedValue, actualValue),
              String.format("시스템 설정에 오류가 있습니다. 관리자에게 문의하세요. (설정: %s)", configKey));
    }
    
    /**
     * 일반적인 설정 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public ConfigurationException(String message) {
        super("CONFIGURATION_ERROR", 
              message,
              "시스템 설정에 오류가 있습니다. 관리자에게 문의하세요.");
    }
    
    /**
     * 원인 예외와 함께 설정 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public ConfigurationException(String message, Throwable cause) {
        super("CONFIGURATION_ERROR", message, cause);
    }
}
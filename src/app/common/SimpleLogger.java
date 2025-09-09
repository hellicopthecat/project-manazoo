package app.common;

/**
 * 간단한 로거 클래스입니다.
 * DEBUG 모드에서만 메시지를 출력하고, 일반 모드에서는 조용히 동작합니다.
 * 디버그 모드는 시스템 프로퍼티로 제어됩니다.
 */
public class SimpleLogger {
    
    // 디버그 모드 여부 (시스템 프로퍼티에서 읽어옴)
    // properties 파일보다 시스템 프로퍼티가 우선순위를 가집니다.
    private static final boolean DEBUG_MODE = Boolean.parseBoolean(
        System.getProperty("app.debug", "false")
    );
    
    private final String className;
    
    /**
     * 로거 생성자
     * 
     * @param clazz 로거를 사용하는 클래스
     */
    public SimpleLogger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }
    
    /**
     * 팩토리 메서드 - 클래스 기반 로거 생성
     * 
     * @param clazz 로거를 사용하는 클래스
     * @return SimpleLogger 인스턴스
     */
    public static SimpleLogger getLogger(Class<?> clazz) {
        return new SimpleLogger(clazz);
    }
    
    /**
     * 디버그 메시지를 출력합니다.
     * DEBUG 모드에서만 출력되고, 일반 모드에서는 무시됩니다.
     * 
     * @param message 출력할 메시지
     */
    public void debug(String message) {
        if (DEBUG_MODE) {
            System.out.println("[DEBUG] [" + className + "] " + message);
        }
    }
    
    /**
     * 포맷팅된 디버그 메시지를 출력합니다.
     * 
     * @param format 메시지 포맷
     * @param args 포맷 인수들
     */
    public void debug(String format, Object... args) {
        if (DEBUG_MODE) {
            debug(String.format(format, args));
        }
    }
    
    /**
     * 일반 정보 메시지를 출력합니다.
     * 항상 출력됩니다.
     * 
     * @param message 출력할 메시지
     */
    public void info(String message) {
        System.out.println("[INFO] [" + className + "] " + message);
    }
    
    /**
     * 포맷팅된 정보 메시지를 출력합니다.
     * 
     * @param format 메시지 포맷
     * @param args 포맷 인수들
     */
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }
    
    /**
     * 오류 메시지를 출력합니다.
     * 항상 출력됩니다.
     * 
     * @param message 출력할 메시지
     */
    public void error(String message) {
        System.err.println("[ERROR] [" + className + "] " + message);
    }
    
    /**
     * 예외와 함께 오류 메시지를 출력합니다.
     * 
     * @param message 출력할 메시지
     * @param throwable 예외 객체
     */
    public void error(String message, Throwable throwable) {
        error(message + ": " + throwable.getMessage());
        
        // 디버그 모드에서만 스택 트레이스 출력
        if (DEBUG_MODE) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * 현재 디버그 모드 상태를 반환합니다.
     * 
     * @return 디버그 모드 여부
     */
    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }
    
    /**
     * 디버그 모드 상태를 출력합니다.
     */
    public static void printDebugStatus() {
        if (DEBUG_MODE) {
            System.out.println("[DEBUG] 디버그 모드가 활성화되어 있습니다.");
        } else {
            System.out.println("[INFO] 일반 모드로 실행 중입니다. (디버그 메시지 숨김)");
        }
        System.out.println("[INFO] 디버그 모드 설정 방법:");
        System.out.println("[INFO]   1. 시스템 프로퍼티: java -Dapp.debug=true MyApp");
        System.out.println("[INFO]   2. properties 파일: app.debug=true");
        System.out.println("[INFO] ※ 시스템 프로퍼티가 properties 파일보다 우선순위가 높습니다.");
    }
}

package app.common.ui;

/**
 * Windows CMD 환경에서 사용할 기본 UI 유틸리티 클래스입니다.
 * 
 * <p>환경 설정:
 * <ul>
 *   <li>Windows CMD 기본 폰트</li>
 *   <li>콘솔 크기: 120x50</li>
 *   <li>확장 도구 없음</li>
 *   <li>텍스트 아트 기반 UI</li>
 * </ul>
 */
public final class UIUtil {
    
    /** 콘솔 가로 너비 */
    public static final int CONSOLE_WIDTH = 120;
    
    /** 콘솔 세로 높이 */
    public static final int CONSOLE_HEIGHT = 50;
    
    /** 기본 패딩 크기 */
    public static final int DEFAULT_PADDING = 2;
    
    /**
     * private 생성자로 인스턴스 생성을 방지합니다.
     */
    private UIUtil() {
    }
    
    /**
     * 화면을 클리어합니다.
     * Windows CMD 환경에서 화면을 깨끗하게 지웁니다.
     */
    public static void clearScreen() {
        try {
            // 먼저 ANSI 이스케이프 시퀀스를 시도 (Windows 10+ 지원)
            System.out.print("\033[2J\033[H");
            System.out.flush();

            // 추가적으로 Runtime.exec()로 cls 명령 시도
            Runtime.getRuntime().exec("cls");
            Thread.sleep(100); // 명령 실행 대기

        } catch (Exception e) {
            // 모든 방법이 실패하면 충분한 빈 줄로 화면 클리어
            System.out.println("실패");
            for (int i = 0; i < 100; i++) {
                System.out.println();
            }
        }
        System.out.flush(); // 버퍼 비우기
    }
    
    /**
     * 텍스트를 중앙 정렬하여 출력합니다.
     * 
     * @param text 출력할 텍스트
     */
    public static void printCentered(String text) {
        // TODO: 구현 예정
    }
    
    /**
     * 텍스트를 중앙 정렬하여 출력합니다.
     * 
     * @param text 출력할 텍스트
     * @param width 정렬 기준 너비
     */
    public static void printCentered(String text, int width) {
        // TODO: 구현 예정
    }
    
    /**
     * 구분선을 출력합니다.
     * 
     * @param character 구분선에 사용할 문자
     */
    public static void printSeparator(char character) {
        System.out.println();
        System.out.println(String.valueOf(character).repeat(CONSOLE_WIDTH));
        System.out.println();
    }

    /**
     * 지정된 개수만큼 빈 줄을 출력합니다.
     * 
     * @param lines 출력할 빈 줄의 개수
     */
    public static void printEmptyLines(int lines) {
        // TODO: 구현 예정
    }
    
    /**
     * 텍스트 양쪽에 패딩을 추가하여 출력합니다.
     * 
     * @param text 출력할 텍스트
     * @param paddingChar 패딩에 사용할 문자
     * @param totalWidth 전체 너비
     */
    public static void printWithPadding(String text, char paddingChar, int totalWidth) {
        // TODO: 구현 예정
    }
    
    /**
     * 사용자 입력을 기다리는 프롬프트를 출력합니다.
     * 
     * @param message 표시할 메시지
     */
    public static void printPrompt(String message) {
        // TODO: 구현 예정
    }
}

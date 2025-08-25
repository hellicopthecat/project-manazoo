package app.common;

import java.util.Scanner;

/**
 * 사용자 입력을 안전하게 처리하는 유틸리티 클래스입니다.
 * 다양한 타입의 입력에 대해 검증과 예외 처리를 제공합니다.
 * 
 * <p>지원하는 입력 타입:
 * <ul>
 *   <li>정수 입력 - 숫자 형식 검증</li>
 *   <li>실수 입력 - 소수점 1자리까지 제한</li>
 *   <li>문자열 입력 - 공백 제거 처리</li>
 * </ul>
 * 
 * <p>사용법:
 * <pre>{@code
 * Scanner scanner = new Scanner(System.in);
 * InputUtil inputUtil = new InputUtil(scanner);
 * 
 * int number = inputUtil.getIntInput();
 * float decimal = inputUtil.getFloatInputOneDecimal();
 * String text = inputUtil.getStringInput();
 * }</pre>
 */
public final class InputUtil {

    private final Scanner scanner;

    /**
     * InputUtil 인스턴스를 생성합니다.
     * 
     * @param scanner 사용자 입력을 받을 Scanner 객체
     * @throws IllegalArgumentException scanner가 null인 경우
     */
    public InputUtil(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner는 null일 수 없습니다.");
        }
        this.scanner = scanner;
    }

    /**
     * 안전한 정수 입력을 처리합니다.
     * 올바른 정수가 입력될 때까지 반복적으로 요청합니다.
     * 
     * <p>입력 검증:
     * <ul>
     *   <li>숫자 형식이 아닌 경우 재입력 요청</li>
     *   <li>정수 범위를 벗어나는 경우 재입력 요청</li>
     * </ul>
     * 
     * @return 검증된 정수 값
     */
    public int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("올바른 숫자를 입력해주세요: ");
            }
        }
    }

    /**
     * 소수점 1자리까지 허용하는 안전한 실수 입력을 처리합니다.
     * 올바른 형식의 실수가 입력될 때까지 반복적으로 요청합니다.
     * 
     * <p>허용되는 형식:
     * <ul>
     *   <li>정수: "1", "-3", "+12"</li>
     *   <li>소수점 1자리: "0.5", "+12.3", "-7.8"</li>
     * </ul>
     * 
     * <p>허용되지 않는 형식:
     * <ul>
     *   <li>소수점만: "1.", ".5"</li>
     *   <li>소수점 2자리 이상: "1.23", "3.456"</li>
     *   <li>문자: "abc", "12a"</li>
     * </ul>
     * 
     * @return 검증된 실수 값 (소수점 1자리까지)
     */
    public float getFloatInputOneDecimal() {
        while (true) {
            String line = scanner.nextLine().trim();

            // 정규식 패턴 설명:
            // ^[+-]? : 선택적 부호 (+ 또는 -)
            // (?:\\d+ : 정수부 1자리 이상
            // (?:\\.\\d)? : 소수점 이하가 있다면 정확히 1자리
            // )$
            //
            // 허용 예: "1", "-3", "0.5", "+12.3"
            // 비허용 예: "1.", ".5", "1.23", "abc"
            if (line.matches("^[+-]?(?:\\d+(?:\\.\\d)?)$")) {
                try {
                    return Float.parseFloat(line);
                } catch (NumberFormatException ignored) {
                    // float 범위를 벗어나면 아래 안내 출력
                }
            }

            System.out.print("올바른 형식의 실수(소수점 1자리까지)로 입력해주세요: ");
        }
    }

    /**
     * 안전한 문자열 입력을 처리합니다.
     * 입력된 문자열의 앞뒤 공백을 제거하여 반환합니다.
     * 
     * <p>처리 과정:
     * <ul>
     *   <li>사용자 입력 받기</li>
     *   <li>앞뒤 공백 제거 (trim)</li>
     *   <li>결과 반환</li>
     * </ul>
     * 
     * @return 공백이 제거된 문자열 (빈 문자열 가능)
     */
    public String getStringInput() {
        return scanner.nextLine().trim();
    }

    /**
     * 현재 사용 중인 Scanner 객체를 반환합니다.
     * 
     * <p><strong>⚠️ 주의:</strong> 반환된 Scanner 객체를 직접 조작하면 
     * InputUtil의 동작에 영향을 줄 수 있습니다.
     * 
     * @return 현재 사용 중인 Scanner 객체
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * InputUtil의 현재 상태를 문자열로 반환합니다.
     * 디버깅 및 개발 목적으로 사용됩니다.
     * 
     * @return Scanner 정보를 포함한 상태 문자열
     */
    @Override
    public String toString() {
        return String.format("InputUtil{scanner=%s}", 
            scanner != null ? scanner.getClass().getSimpleName() : "null");
    }
}

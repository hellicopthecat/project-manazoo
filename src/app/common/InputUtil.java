package app.common;

import java.util.Scanner;

import app.common.ui.MenuUtil;

/**
 * 사용자 입력을 안전하게 처리하는 유틸리티 클래스입니다. 다양한 타입의 입력에 대해 검증과 예외 처리를 제공합니다.
 *
 * <p>
 * 지원하는 입력 타입:
 * <ul>
 * <li>정수 입력 - 숫자 형식 검증</li>
 * <li>실수 입력 - 소수점 1자리까지 제한</li>
 * <li>문자열 입력 - 공백 제거 처리</li>
 * </ul>
 *
 * <p>
 * 사용법:
 * 
 * <pre>{@code
 * int number = InputUtil.getIntInput();
 * float decimal = InputUtil.getFloatInputOneDecimal();
 * String text = InputUtil.getStringInput();
 * }</pre>
 */
public final class InputUtil {

	private static final Scanner scanner = new Scanner(System.in);

	/**
	 * private 생성자로 인스턴스 생성을 방지합니다.
	 */
	private InputUtil() {
	}

	/**
	 * 안전한 정수 입력을 처리합니다. 올바른 정수가 입력될 때까지 반복적으로 요청합니다.
	 *
	 * <p>
	 * 입력 검증:
	 * <ul>
	 * <li>숫자 형식이 아닌 경우 재입력 요청</li>
	 * <li>정수 범위를 벗어나는 경우 재입력 요청</li>
	 * </ul>
	 *
	 * @return 검증된 정수 값
	 */
	public static int getIntInput() {
		while (true) {
			try {
				return Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.print(MenuUtil.DEFAULT_PREFIX + "정수를 입력하세요 ▶ ");
			}
		}
	}

	/**
	 * 안전한 양수 입력을 처리합니다. 올바른 양수가 입력될 때까지 반복적으로 요청합니다.
	 *
	 * <p>
	 * 입력 검증:
	 * <ul>
	 * <li>숫자 형식이 아닌 경우 재입력 요청</li>
	 * <li>정수 범위를 벗어나는 경우 재입력 요청</li>
	 * <li>양수 범위를 벗어나는 경우 재입력 요청</li>
	 * </ul>
	 *
	 * @return 검증된 양수 값
	 */
	public static int getPositiveNumberInput() {
		while (true) {
			try {
				int a = Integer.parseInt(scanner.nextLine());
				if (a >= 0) {
					return a;
				} else {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "올바른 숫자를 입력해주세요: ");
				}
			} catch (NumberFormatException e) {
				System.out.print(MenuUtil.DEFAULT_PREFIX + "올바른 숫자를 입력해주세요: ");
			}
		}
	}

	/**
	 * 안전한 Long 타입 정수 입력을 처리합니다. 올바른 정수가 입력될 때까지 반복적으로 요청합니다.
	 *
	 * <p>
	 * 입력 검증:
	 * <ul>
	 * <li>숫자 형식이 아닌 경우 재입력 요청</li>
	 * <li>정수 범위를 벗어나는 경우 재입력 요청</li>
	 * </ul>
	 *
	 * @return 검증된 정수 값
	 */
	public static Long getLongInput() {
		while (true) {
			try {
				return Long.parseLong(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.print("올바른 숫자를 입력해주세요: ");
			}
		}
	}

	/**
	 * 소수점 1자리까지 허용하는 안전한 실수 입력을 처리합니다. 올바른 형식의 실수가 입력될 때까지 반복적으로 요청합니다.
	 *
	 * <p>
	 * 허용되는 형식:
	 * <ul>
	 * <li>정수: "1", "-3", "+12"</li>
	 * <li>소수점 1자리: "0.5", "+12.3", "-7.8"</li>
	 * </ul>
	 *
	 * <p>
	 * 허용되지 않는 형식:
	 * <ul>
	 * <li>소수점만: "1.", ".5"</li>
	 * <li>소수점 2자리 이상: "1.23", "3.456"</li>
	 * <li>문자: "abc", "12a"</li>
	 * </ul>
	 *
	 * @return 검증된 실수 값 (소수점 1자리까지)
	 */
	public static float getFloatInputOneDecimal() {
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
	 * 안전한 문자열 입력을 처리합니다. 입력된 문자열의 앞뒤 공백을 제거하여 반환합니다.
	 *
	 * <p>
	 * 처리 과정:
	 * <ul>
	 * <li>사용자 입력 받기</li>
	 * <li>앞뒤 공백 제거 (trim)</li>
	 * <li>결과 반환</li>
	 * </ul>
	 *
	 * @return 공백이 제거된 문자열 (빈 문자열 가능)
	 */
	public static String getStringInput() {
		return scanner.nextLine().trim();
	}
}

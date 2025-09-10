package app.common.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import app.common.InputUtil;

/**
 * 메뉴 UI 생성 및 출력을 담당하는 유틸리티 클래스입니다. 일관된 스타일의 메뉴를 쉽게 생성할 수 있도록 도와줍니다.
 */
public final class MenuUtil {

	/** 기본 메뉴 항목 접두사 */
	public static final String DEFAULT_PREFIX = "  ";

	/** 기본 메뉴 항목 번호 형식 */
	public static final String DEFAULT_NUMBER_FORMAT = "%d. ";

	/**
	 * private 생성자로 인스턴스 생성을 방지합니다.
	 */
	private MenuUtil() {
	}

	/**
	 * ManageZoo 시스템의 접속 방식 선택 메뉴를 출력합니다. 관리자 모드와 관람객 모드 중 선택할 수 있는 첫 번째 메뉴입니다. 120자
	 * 콘솔 너비에 맞춰 좌측 정렬됩니다.
	 */
	public static void printAccessMenu() {
		UIUtil.printSeparator('━');
		System.out.println("  시스템 접속을 선택하세요");
		System.out.println();
		System.out.println("  ◆ 1. 관리자 모드  │  Administrator");
		System.out.println();
		System.out.println("  ◆ 2. 관람객 모드  │  Visitor");
		System.out.println();
		System.out.println("  ◆ 0. 시스템 종료  │  Exit");
		UIUtil.printSeparator('━');
		System.out.print("  선택 번호를 입력하세요 ▶ ");
	}

	/**
	 * 기본 스타일의 메뉴를 출력합니다. 좌측 정렬에 세로로 번호가 있는 메뉴를 생성합니다.
	 * 
	 * @param title   메뉴 제목
	 * @param options 메뉴 옵션들
	 */
	public static void printMenu(String title, String[] options) {
		// 상단 구분선
		UIUtil.printSeparator('━');

		// 메뉴 제목 출력
		System.out.println(DEFAULT_PREFIX + title);

		// 메뉴 옵션들 출력 (번호와 함께)
		for (int i = 0; i < options.length; i++) {
			System.out.printf(DEFAULT_PREFIX + DEFAULT_NUMBER_FORMAT + "%s%n", (i + 1), options[i]);
		}

		// 하단 구분선
		UIUtil.printSeparator('━');

		// 입력 프롬프트
		System.out.print(DEFAULT_PREFIX + "선택 번호를 입력하세요 ▶ ");
	}

	/**
	 * 특별 옵션이 포함된 메뉴를 생성합니다. 일반 메뉴는 1번부터, 특별 메뉴는 0, 9, 8번부터 역순으로 번호가 매겨집니다.
	 * 
	 * @param titlePrinter   제목 출력을 담당하는 Runnable (null이면 제목 없음) 예:
	 *                       TextArtUtil::printAdminMenuTitle
	 * @param options        일반 메뉴 옵션들 (1번부터 시작)
	 * @param specialOptions 특별 메뉴 옵션들 (0, 9, 8번으로 지정됨. null 가능) 예: ["뒤로가기", "화면
	 *                       지우기"] -> 0번, 9번으로 지정
	 */
	public static void generateMenuWithSpecialOptions(Runnable titlePrinter, String[] options,
			String[] specialOptions) {
		// 제목 출력 (Runnable이 제공된 경우)
		if (titlePrinter != null) {
			titlePrinter.run();
		}

		// 상단 구분선
		UIUtil.printSeparator('━');

		// 일반 메뉴 옵션들 출력 (1번부터 시작)
		for (int i = 0; i < options.length; i++) {
			System.out.printf(DEFAULT_PREFIX + DEFAULT_NUMBER_FORMAT + "%s%n", (i + 1), options[i]);
		}

		// 특별 메뉴 옵션들이 있다면 출력
		if (specialOptions != null && specialOptions.length > 0) {
			System.out.println(); // 구분을 위한 빈 줄

			// 특별 메뉴 번호 배열: [0, 9, 8, 7, ...] 순서로 지정
			int[] specialNumbers = { 0, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

			for (int i = 0; i < specialOptions.length && i < specialNumbers.length; i++) {
				System.out.printf(DEFAULT_PREFIX + DEFAULT_NUMBER_FORMAT + "%s%n", specialNumbers[i],
						specialOptions[i]);
			}
		}

		// 하단 구분선
		UIUtil.printSeparator('━');

		// 입력 프롬프트
		System.out.print(DEFAULT_PREFIX + "선택 번호를 입력하세요 ▶ ");
	}

	/**
	 * 일반 메뉴를 생성합니다. (특별 옵션 없음)
	 * 
	 * @param titlePrinter 제목 출력을 담당하는 Runnable
	 * @param options      메뉴 옵션들
	 */
	public static void generateMenu(Runnable titlePrinter, String[] options) {
		generateMenuWithSpecialOptions(titlePrinter, options, null);
	}

	/**
	 * 단순 텍스트 제목을 가진 메뉴를 생성합니다.
	 * 
	 * @param title          텍스트 제목
	 * @param options        메뉴 옵션들
	 * @param specialOptions 특별 메뉴 옵션들
	 */
	public static void generateMenuWithTextTitle(String title, String[] options, String[] specialOptions) {
		Runnable titlePrinter = () -> {
			System.out.println();
			System.out.println(DEFAULT_PREFIX + title);
			System.out.println();
		};
		generateMenuWithSpecialOptions(titlePrinter, options, specialOptions);
	}

	/**
	 * 단순 텍스트 제목을 가진 메뉴를 생성합니다. (특별 옵션 없음)
	 * 
	 * @param title   텍스트 제목
	 * @param options 메뉴 옵션들
	 */
	public static void generateMenuWithTextTitle(String title, String[] options) {
		generateMenuWithTextTitle(title, options, null);
	}

	/**
	 * 관리자 메뉴를 텍스트 아트 타이틀과 함께 출력합니다. Admin Menu 텍스트 아트가 포함된 특별한 관리자 전용 메뉴입니다.
	 * 
	 * @param options        관리자 메뉴 옵션들
	 * @param specialOptions 특별 메뉴 옵션들 (0, 9, 8번으로 지정됨. null 가능)
	 */
	public static void printAdminMenu(String[] options, String[] specialOptions) {
		generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, options, specialOptions);
	}

	/**
	 * 관리자 메뉴를 텍스트 아트 타이틀과 함께 출력합니다. (특별 메뉴 없음)
	 * 
	 * @param options 관리자 메뉴 옵션들
	 */
	public static void printAdminMenu(String[] options) {
		generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, options, null);
	}

	/**
	 * 질문형 UI를 담당하는 내부 클래스입니다. 사용자에게 선택지를 제시하고 입력을 받는 기능을 제공합니다. MenuUtil과 일관된 스타일을
	 * 유지하면서 질문 형태의 인터페이스를 구현합니다.
	 */
	public static class Question {

		/**
		 * private 생성자로 인스턴스 생성을 방지합니다.
		 */
		private Question() {
		}

		/**
		 * 단일 선택 질문을 출력하고 사용자 입력을 받습니다.
		 * 
		 * @param question 질문 내용
		 * @param choices  선택지 배열
		 * @return 사용자가 선택한 번호 (1부터 시작)
		 */
		public static int askSingleChoice(String question, String[] choices) {
			System.out.println(DEFAULT_PREFIX + question);
			System.out.println();

			for (int i = 0; i < choices.length; i++) {
				System.out.printf(DEFAULT_PREFIX + DEFAULT_NUMBER_FORMAT + "%s%n", (i + 1), choices[i]);
			}

			System.out.println();
			System.out.print(DEFAULT_PREFIX + "정수를 입력하세요 ▶ ");

			while (true) {
				int input = InputUtil.getIntInput();
				if (input >= 1 && input <= choices.length) {
					UIUtil.printSeparator('━');
					return input;
				} else {
					System.out.println(DEFAULT_PREFIX + "X 잘못된 입력입니다. 1부터 " + choices.length + " 사이의 번호를 입력하세요.");
					System.out.print(DEFAULT_PREFIX + "정수를 입력하세요 ▶ ");
				}
			}
		}

		/**
		 * 텍스트 입력을 받는 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return 사용자가 입력한 문자열
		 */
		public static String askTextInput(String question) {
			System.out.println(DEFAULT_PREFIX + question);
			System.out.println();
			System.out.print(DEFAULT_PREFIX + "입력하세요 ▶ ");

			String input = InputUtil.getStringInput();
			UIUtil.printSeparator('━');
			return input;
		}

		/**
		 * 숫자 입력을 받는 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @param unit     단위 (예: "㎡", "°C", "원" 등, null 가능)
		 * @return 사용자가 입력한 실수값
		 */
		public static float askNumberInput(String question, String unit) {
			String displayQuestion = unit != null ? question + "(" + unit + ")" : question;
			System.out.println(DEFAULT_PREFIX + displayQuestion);
			System.out.println();
			System.out.print(DEFAULT_PREFIX + "숫자를 입력하세요 ▶ ");

			float input = InputUtil.getFloatInputOneDecimal();
			UIUtil.printSeparator('━');
			return input;
		}

		/**
		 * 숫자 입력을 받는 질문을 출력합니다. (단위 없음)
		 * 
		 * @param question 질문 내용
		 * @return 사용자가 입력한 실수값
		 */
		public static float askNumberInput(String question) {
			return askNumberInput(question, null);
		}

		/**
		 * 숫자 입력을 받는 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @param unit     단위 (예: "㎡", "°C", "원" 등, null 가능)
		 * @return 사용자가 입력한 정수값
		 */
		public static int askNumberInputInt(String question) {
			String displayQuestion = question;
			System.out.println(DEFAULT_PREFIX + displayQuestion);
			System.out.println();
			System.out.print(DEFAULT_PREFIX + "숫자를 입력하세요 ▶ ");

			int input = InputUtil.getPositiveNumberInput();
			UIUtil.printSeparator('━');
			return input;
		}

		/**
		 * 폰번호 입력을 받는 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return 사용자가 입력한 문자열
		 */
		public static String askPhoneNumber(String question) {
			String phonePattern = "^\\d{3}-\\d{4}-\\d{4}$";

			while (true) {
				String input = askTextInput(question);
				if (input.matches(phonePattern)) {
					return input;
				} else {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 전화번호 형식입니다.");
					System.out.println();
				}
			}
		}

		/**
		 * 날짜(연-월-일) 입력을 받는 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return 사용자가 입력한 문자열
		 */
		public static String askDate(String question) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			LocalDate today = LocalDate.now();
			LocalDate minDate = today.plusDays(1);

			while (true) {

				String input = MenuUtil.Question.askTextInput(question);

				try {
					LocalDate inputDate = LocalDate.parse(input, formatter);

					if (inputDate.isBefore(minDate)) {
						System.out.println(MenuUtil.DEFAULT_PREFIX + "내일 날짜부터 입력 가능합니다.");
						System.out.println();
					} else {
						return inputDate.format(formatter);
					}

				} catch (DateTimeParseException e) {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 날짜 형식입니다.");
					System.out.println();
				}
			}
		}

		/**
		 * 예/아니오 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return true(예) 또는 false(아니오)
		 */
		public static boolean askYesNo(String question) {
			String[] choices = { "예", "아니오" };
			int result = askSingleChoice(question, choices);
			return result == 1;
		}

		/**
		 * 확인/취소 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return true(확인) 또는 false(취소)
		 */
		public static boolean askConfirmCancel(String question) {
			String[] choices = { "확인", "취소" };
			int result = askSingleChoice(question, choices);
			return result == 1;
		}

		/**
		 * y/n 형태의 간단한 확인 질문을 출력합니다.
		 * 
		 * @param question 질문 내용
		 * @return true(y/yes) 또는 false(n/no)
		 */
		public static boolean askSimpleConfirm(String question) {
			System.out.println();
			System.out.println(DEFAULT_PREFIX + question);
			System.out.println();
			System.out.print(DEFAULT_PREFIX + "확인하시겠습니까? (y/n) ▶ ");

			while (true) {
				String input = app.common.InputUtil.getStringInput().toLowerCase().trim();
				if (input.equals("y") || input.equals("yes")) {
					UIUtil.printSeparator('━');
					return true;
				} else if (input.equals("n") || input.equals("no")) {
					UIUtil.printSeparator('━');
					return false;
				} else {
					System.out.println(DEFAULT_PREFIX + "X 잘못된 입력입니다. 'y' 또는 'n'으로 입력해주세요.");
					System.out.print(DEFAULT_PREFIX + "확인하시겠습니까? (y/n) ▶ ");
				}
			}
		}
	}
}

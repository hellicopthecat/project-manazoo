package app.visitor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import app.animal.AnimalEnum.Species;
import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;

public class VisitorManager {

	Map<String, Reservation> reservations = new HashMap<>();

	String id; // 예약 번호
	String name;
	String phone;
	String date;
	int adultCount;
	int childCount;
	int totalPrice;
	final int ADULT_PRICE = 13000;
	final int CHILD_PRICE = 5000;

	public void run() {

		while (true) {
			displayVisitorMenu();
			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printViewMenuTitle();
				UIUtil.printSeparator('━');
				viewInformation();
			}
			case 2 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printReservationMenuTitle();
				UIUtil.printSeparator('━');
				reservation();
			}
			case 3 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printViewMenuTitle();
				UIUtil.printSeparator('━');
				viewReservation();
			}
			case 4 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printViewMenuTitle();
				UIUtil.printSeparator('━');
				editReservationDate();
			}
			case 5 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printRemoveMenuTitle();
				UIUtil.printSeparator('━');
				removeReservation();
			}
			case 0 -> {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
			}
		}
	}

	private static void displayVisitorMenu() {
		String[] option = { "동물원 정보", "관람 예약", "예약 확인", "예약 변경(방문일정)", "예약 취소" };
		String[] specialOptions = { "뒤로가기" };
		UIUtil.printSeparator('━');
		MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printVisitorMenuTitle, option, specialOptions);
	}

	public void viewInformation() {

		// 동물원 정보
		System.out.println("이름 : Manazoo 동물원");
		System.out.println("주소 : 서울시 종각");
		System.out.println("문의전화 : 02)123-1234 \n");

		// 동물 목록
		System.out.println("관람 가능 동물 : ");
		for (Species s : Species.values()) {
			System.out.print(s.name() + " ");
		}
		System.out.println(); // 줄 바꾸기
		System.out.println(); // 줄 바꾸기

		// 가격 정보
		System.out.printf("대인 티켓 : %d원 \n", ADULT_PRICE);
		System.out.printf("소인 티켓 : %d원 \n", CHILD_PRICE);
		System.out.println();
	}

	public void reservation() {
		id = IdGeneratorUtil.generateId();
		name = MenuUtil.Question.askTextInput("성함을 입력하세요.");
		phone = inputPhone("연락처를 입력하세요. (000-0000-0000) : ");
		date = inputDate("방문일시를 입력하세요. (YYYY-MM-DD)");
		adultCount = MenuUtil.Question.askNumberInputInt("대인 인원수를 입력하세요.");
		childCount = MenuUtil.Question.askNumberInputInt("대인 인원수를 입력하세요.");
		totalPrice = adultCount * ADULT_PRICE + childCount * CHILD_PRICE;

		System.out.println("입력하신 정보는 아래와 같습니다.");
		System.out.println("성함 : " + name);
		System.out.println("연락처 : " + phone);
		System.out.println("방문일시 : " + date);
		System.out.println("대인 인원수 : " + adultCount);
		System.out.println("소인 인원수 : " + childCount);
		System.out.printf("결제 총액 : %d 원 \n", totalPrice);

		boolean choice = MenuUtil.Question.askYesNo("결제하시겠습니까?");
		if (choice) {
			Reservation reservation = new Reservation(id, name, phone, date, adultCount, childCount, totalPrice);
			reservations.put(id, reservation);

			System.out.println("예약 및 결제 성공!");
			System.out.println(reservation);
			System.out.println();
		}

//		payment();
	}

	public String inputPhone(String question) {
		String phonePattern = "^\\d{3}-\\d{4}-\\d{4}$";

		while (true) {
			String inPhone = MenuUtil.Question.askTextInput(question);
			if (inPhone.matches(phonePattern)) {
				return inPhone;
			} else {
				System.out.println("  잘못된 날짜 형식입니다.");
			}
		}
	}

	public String inputDate(String question) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		while (true) {
			String inDate = MenuUtil.Question.askTextInput(question);
			try {
				LocalDate localDate = LocalDate.parse(inDate, formatter);
				return localDate.format(formatter);
			} catch (DateTimeParseException e) {
				System.out.println("잘못된 날짜 형식입니다.");
			}
		}
	}

	public void payment() {
		while (true) {

			System.out.println("결제하시겠습니까?");
			System.out.println("1.예 2.아니오(나가기)");
			String answer = InputUtil.getStringInput();
			if (answer.equals("1")) {

				// 예약(Reservation) 객체 생성하여 Map에 저장
				Reservation reservation = new Reservation(id, name, phone, date, adultCount, childCount, totalPrice);
				reservations.put(id, reservation);

				// 예약 및 결제 성공 메시지
				System.out.println("예약 및 결제 성공!");
				System.out.println(reservation);
				System.out.println();
				return;
			} else if (answer.equals("2")) {
				// 2.아니오(나가기)
				return;
			} else {
				System.out.println("잘못된 선택입니다.");
			}
		}

	}

	public void viewReservation() {
		while (true) {

			// 예약 목록이 없을 경우
			if (reservations.isEmpty()) {
				System.out.println("등록된 예약이 없습니다.");
				return;
			}

			// 예약번호로 검색
			System.out.println("예약번호 : ");
			String findId = InputUtil.getStringInput();
			if (reservations.containsKey(findId)) {

				// 검색된 예약 내용 보여주기
				Reservation reservation = reservations.get(findId);
				System.out.println(reservation);
				return;
			} else {
				System.out.println("예약번호를 다시 입력해 주세요. ");
			}
		}
	}

	public void editReservationDate() {
		while (true) {

			// 예약 목록이 없을 경우
			if (reservations.isEmpty()) {
				System.out.println("등록된 예약이 없습니다.");
				return;
			}

			// 예약번호로 검색
			System.out.println("예약번호 : ");
			String findId = InputUtil.getStringInput();
			if (reservations.containsKey(findId)) {

				// 검색된 예약의 방문일정 보여주기
				Reservation reservation = reservations.get(findId);
				System.out.println("현재 방문일정 : " + reservation.getDate());

				// 변경할 일정 입력 받기
				System.out.println("변경 일정 (YYYY-MM-DD) : ");
				String inDate = InputUtil.getStringInput();

				// 일정 변경하기
				reservation.setDate(inDate);
				System.out.println("방문일정 변경 성공!");
				System.out.println("방문일정 : " + reservation.getDate());
				return;
			} else {
				System.out.println("예약번호를 다시 입력해 주세요. ");
			}
		}
	}

	public void removeReservation() {
		while (true) {

			// 예약 목록이 없을 경우
			if (reservations.isEmpty()) {
				System.out.println("등록된 예약이 없습니다.");
				return;
			}

			// 예약번호로 검색
			System.out.println("예약번호 : ");
			String findId = InputUtil.getStringInput();
			if (reservations.containsKey(findId)) {

				// 검색된 예약 내용 보여주기
				Reservation reservation = reservations.get(findId);
				System.out.println(reservation);

				System.out.println("예약을 취소하시겠습니까?");
				System.out.println("1.예 2.아니오(나가기)");
				String answer = InputUtil.getStringInput();
				if (answer.equals("1")) {

					// 예약 취소, Map에서 데이터 삭제
					reservations.remove(findId);
					System.out.println("예약 취소 완료");
					return;
				} else if (answer.equals("2")) {

					// 2.아니오(나가기) 선택
					return;
				} else {
					System.out.println("잘못된 선택입니다.");
				}
			} else {
				System.out.println("예약번호를 다시 입력해 주세요. ");
			}
		}
	}
}

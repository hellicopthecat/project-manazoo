package app.visitor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import app.animal.AnimalEnum.Species;
import app.common.id.IdGeneratorUtil;

public class VisitorManager {

	Scanner in = new Scanner(System.in);
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
			showMenu();
			String menu = in.nextLine();
			switch (menu) {
			case "1" -> zooInformation();
			case "2" -> reservation();
			case "3" -> viewReservation();
			case "4" -> changeReservationDate();
			case "5" -> cancelReservation();
			case "6" -> {
				System.out.println("뒤로 가기");
				return;
			}
			default -> System.out.println("잘못된 선택입니다.");
			}
		}
	}

	public void showMenu() {
		System.out.println("=== =Manazoo 동물원 ====");
		System.out.println("1. 동물원 정보");
		System.out.println("2. 관람 예약");
		System.out.println("3. 예약 확인");
		System.out.println("4. 예약일정 변경");
		System.out.println("5. 예약 취소");
		System.out.println("6. 뒤로 가기");
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		System.out.println("선택>> ");
	}

	public void zooInformation() {

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
		while (true) {
			// 예약번호 생성 
			id = IdGeneratorUtil.generateId();

			// 예약 정보 입력 받기 
			inputInformation();

			// 예약 정보 확인 받고, 결제 여부 결정
			if (confirmInformation()) {
				break; // while문 깨고 payment() 진행 
			}
		}
		// 결제 진행 
		payment();
	}

	public void inputInformation() {
		System.out.println("예약 정보를 입력해 주세요.");

		// 방문날짜 입력 받기
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd");
		while (true) {
			System.out.println("방문 일시 (YYYY-MM-DD) : ");
			String inDate = in.nextLine();
			try {
				LocalDate localDate = LocalDate.parse(inDate,
						formatter);
				date = localDate.format(formatter);
				break; // 성공적으로 파싱되면 루프 종료
			} catch (DateTimeParseException e) {
				System.out.println("잘못된 날짜 형식입니다. 다시 입력해 주세요.");
			}
		}

		// 대인 인원수 입력 받기
		while (true) {
			System.out.println("대인 인원수 : ");
			String inputAdultCount = in.nextLine();
			if (!StringIsInt(inputAdultCount)) {
				System.out.println("숫자로 정확히 입력해 주세요.");
			} else {
				int intAdultCount = Integer.parseInt(inputAdultCount);
				if (0 <= intAdultCount) {
					adultCount = intAdultCount;
					break;
				} else {
					System.out.println("다시 입력해 주세요.");
				}
			}
		}

		// 소인 인원수 입력 받기
		while (true) {
			System.out.println("소인 인원수 : ");
			String inputChildCount = in.nextLine();
			if (!StringIsInt(inputChildCount)) {
				System.out.println("숫자로 정확히 입력해 주세요.");
			} else {
				int intChildCount = Integer.parseInt(inputChildCount);
				if (0 <= intChildCount) {
					childCount = intChildCount;
					break;
				} else {
					System.out.println("다시 입력해 주세요.");
				}
			}
		}
	}

	public static boolean StringIsInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean confirmInformation() {

		// 입력 받은 예약 정보 보여주기
		System.out.println("방문 일시 : " + date);
		System.out.println("대인 인원수 : " + adultCount);
		System.out.println("소인 인원수 : " + childCount);
		totalPrice = adultCount * ADULT_PRICE
				+ childCount * CHILD_PRICE;
		System.out.printf("금액 : %d 원 \n", totalPrice);
		while (true) {

			// 예약 정보 확정 받고, 결제 여부 결정 
			System.out.println("1.다시 입력 2.결제");
			String answer = in.nextLine();
			if (answer.equals("1")) {
				// < 다시입력 > (내부 while문 깨고, 외부 while로 반복)
				break;
			} else if (answer.equals("2")) {
				return true; // 결제로 이동 
			} else {
				System.out.println("잘못된 선택입니다.");
			}
		}
		return false;
	}

	public void payment() {
		while (true) {

			// 결제 정보 입력 받기
			System.out.println("결제 정보를 입력해 주세요.");
			System.out.println("이름: ");
			name = in.nextLine();

			// 전화번호 : 지정된 형식으로 받기 
			String phonePattern = "^\\d{3}-\\d{4}-\\d{4}$";
			while (true) {
				System.out.println("폰 번호 : ");
				String inPhone = in.nextLine();
				if (inPhone.matches(phonePattern)) {
					phone = inPhone;
					break;
				} else {
					System.out.println("잘못된 날짜 형식입니다. 다시 입력해 주세요.");
				}
			}

			// 결제 정보 확인 받고, 결제 진행 여부 결정 
			System.out.printf("결제 금액 : %d 원 \n", totalPrice);
			System.out.println("결제하시겠습니까?");
			System.out.println("1.예 2.아니오(나가기)");
			String answer = in.nextLine();
			if (answer.equals("1")) {

				// 예약(Reservation) 객체 생성하여 Map에 저장 
				Reservation reservation = new Reservation(id, name,
						phone, date, adultCount, childCount,
						totalPrice);
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
				System.out.println("(예약 목록 없음)");
				return;
			} else {

				// 예약번호로 검색 
				System.out.println("예약번호 : ");
				String findId = in.nextLine();
				if (reservations.containsKey(findId)) {

					// 검색된 예약 내용 보여주기 
					Reservation reservation = reservations
							.get(findId);
					System.out.println(reservation);
					return;
				} else {
					System.out.println("예약번호를 다시 입력해 주세요. ");
				}
			}
		}
	}

	public void changeReservationDate() {
		while (true) {

			// 예약 목록이 없을 경우 
			if (reservations.isEmpty()) {
				System.out.println("(예약 목록 없음)");
				return;
			} else {

				// 예약번호로 검색 
				System.out.println("예약번호 : ");
				String findId = in.nextLine();
				if (reservations.containsKey(findId)) {

					// 검색된 예약의 방문일정 보여주기 
					Reservation reservation = reservations
							.get(findId);
					System.out.println(
							"현재 방문일정 : " + reservation.getDate());

					// 변경할 일정 입력 받기
					System.out.println("변경 일정 (YYYY-MM-DD) : ");
					String inDate = in.nextLine();

					// 일정 변경하기 
					reservation.setDate(inDate);
					System.out.println("방문일정 변경 성공!");
					System.out.println(
							"방문일정 : " + reservation.getDate());
					return;
				} else {
					System.out.println("예약번호를 다시 입력해 주세요. ");
				}
			}
		}
	}

	public void cancelReservation() {
		while (true) {

			// 예약 목록이 없을 경우 
			if (reservations.isEmpty()) {
				System.out.println("(예약 목록 없음)");
				return;
			} else {

				// 예약번호로 검색 
				System.out.println("예약번호 : ");
				String findId = in.nextLine();
				if (reservations.containsKey(findId)) {

					// 검색된 예약 내용 보여주기 
					Reservation reservation = reservations
							.get(findId);
					System.out.println(reservation);

					System.out.println("예약을 취소하시겠습니까?");
					System.out.println("1.예 2.아니오(나가기)");
					String answer = in.nextLine();
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
}

package app.visitor;

import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TableUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.repository.MemoryVisitorRepository;
import app.repository.interfaces.VisitorRepository;

public class VisitorManager {

	private final VisitorRepository repository = new MemoryVisitorRepository();

	String id;
	String name;
	String phone;
	String date;
	int adultCount;
	int childCount;
	int totalPrice;
	final int ADULT_PRICE = 13000;
	final int CHILD_PRICE = 5000;

	public void handleVisitorManagement() {

		while (true) {
			displayVisitorMenu();
			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printViewMenuTitle();
				UIUtil.printSeparator('━');
				viewInformation();
				UIUtil.printSeparator('━');
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
			default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 다시 선택하세요.");
			}
		}
	}

	private static void displayVisitorMenu() {
		String[] option = { "동물원 정보", "관람 예약", "예약 확인", "예약 변경(방문일정)", "예약 취소" };
		String[] specialOptions = { "뒤로가기" };
		MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printVisitorMenuTitle, option, specialOptions);
	}

	public void viewInformation() {
		String[] headers = { "Name", "Address", "Call", "Animal", "Adult Ticket", "Child ticket" };
		String[][] data = {
				{ "Manazoo Zoo", "Jonggak, Seoul", "02)123-1234", "Lion, Tiger", Integer.toString(ADULT_PRICE),
						Integer.toString(CHILD_PRICE) },
				{ " ", " ", " ", "Bear, elephant", " ", " " }, { " ", " ", " ", "Wolf, Eagle", " ", " " },
				{ " ", " ", " ", "Owl, Snake", " ", " " } };
		TableUtil.printTable("동물원 정보", headers, data);
	}

	public void reservation() {
		id = IdGeneratorUtil.generateId();
		name = MenuUtil.Question.askTextInput("성함을 입력하세요.");
		phone = MenuUtil.Question.askPhoneNumber("연락처를 입력하세요. (000-0000-0000) : ");
		date = MenuUtil.Question.askDate("방문일시를 입력하세요. (YYYY-MM-DD)");
		adultCount = MenuUtil.Question.askNumberInputInt("대인 인원수를 입력하세요.");
		childCount = MenuUtil.Question.askNumberInputInt("소인 인원수를 입력하세요.");
		totalPrice = adultCount * ADULT_PRICE + childCount * CHILD_PRICE;

		String[] headers = { "Name", "Phone", "Visit Date", "Adult", "Child", "Total amount" };
		String[][] data = { { name, phone, date, Integer.toString(adultCount), Integer.toString(childCount),
				Integer.toString(totalPrice) } };
		TableUtil.printTable("입력하신 정보는 아래와 같습니다.", headers, data);

		boolean choice = MenuUtil.Question.askYesNo("결제하시겠습니까?");
		if (choice) {
			Reservation reservation = repository.createReservation(id, name, phone, date, adultCount, childCount,
					totalPrice);
			System.out.print(MenuUtil.DEFAULT_PREFIX + "예약 및 결제 성공!");
			UIUtil.printSeparator('━');
			reservation.showReservation();
		}

	}

	public void viewReservation() {
		while (true) {
			if (repository.count() == 0) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 예약이 없습니다.");
				return;
			}
			String findId = MenuUtil.Question.askTextInput("예약번호를 입력하세요.");
			if (repository.hasReservation(findId)) {
				Reservation reservation = repository.getReservationById(findId);
				System.out.print(MenuUtil.DEFAULT_PREFIX + "예약 정보");
				reservation.showReservation();
				UIUtil.printSeparator('━');
				return;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "예약번호를 다시 입력하세요. ");
			}
		}
	}

	public void editReservationDate() {
		while (true) {
			if (repository.count() == 0) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 예약이 없습니다.");
				return;
			}
			String findId = MenuUtil.Question.askTextInput("예약번호를 입력하세요.");
			if (repository.hasReservation(findId)) {
				Reservation reservation = repository.getReservationById(findId);
				System.out.println(MenuUtil.DEFAULT_PREFIX + "현재 방문일정 : " + reservation.getDate());
				System.out.println();
				String newDate = MenuUtil.Question.askDate("변경할 일정을 입력하세요. (YYYY-MM-DD) : ");
				repository.updateReservationDate(findId, newDate);
				System.out.println(MenuUtil.DEFAULT_PREFIX + "방문일정 변경 성공!");
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + "방문일정 : " + newDate);
				UIUtil.printSeparator('━');
				return;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "예약번호를 다시 입력하세요. ");
			}
		}
	}

	public void removeReservation() {
		while (true) {
			if (repository.count() == 0) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 예약이 없습니다.");
				return;
			}
			String findId = MenuUtil.Question.askTextInput("예약번호를 입력하세요.");
			if (repository.hasReservation(findId)) {
				Reservation reservation = repository.getReservationById(findId);
				reservation.showReservation();
				boolean choice = MenuUtil.Question.askYesNo("예약을 취소하시겠습니까?");
				if (choice) {
					repository.cancelReservation(findId);
					System.out.println(MenuUtil.DEFAULT_PREFIX + "예약이 취소되었습니다.");
					UIUtil.printSeparator('━');
					return;
				}
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "예약번호를 다시 입력하세요. ");
			}
		}
	}
}

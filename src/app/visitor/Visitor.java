package app.visitor;

import java.util.Scanner;

import app.animal.AnimalEnum.Species;

public class Visitor {

	Scanner in = new Scanner(System.in);

	public void run() {
		while (true) {
			showMenu();
			String menu = in.nextLine();
			switch (menu) {
			case "1" -> zooInformation();
			case "2" -> viewInformation();
			case "3" -> Reservation();
			case "4" -> viewReservation();
			case "5" -> {
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
		System.out.println("2. 관람 정보");
		System.out.println("3. 예약하기");
		System.out.println("4. 예약 조회");
		System.out.println("5. 뒤로 가기");
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		System.out.println("선택>> ");
	}

	public void zooInformation() {
		System.out.println("이름 : Manazoo 동물원");
		System.out.println("주소 : 서울시 종각");
		System.out.println("문의전화 : 02)123-1234 \n");
	}

	public void viewInformation() {
		System.out.println("관람 가능 동물 : ");
		for (Species s : Species.values()) {
			System.out.print(s.name() + " ");
		}
		System.out.println();
		System.out.println();
	}

	public void Reservation() {

	}

	public void viewReservation() {

	}

}

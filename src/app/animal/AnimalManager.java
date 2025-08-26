package app.animal;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AnimalManager {

	Scanner in = new Scanner(System.in);
	Map<String, Animal> animals = new HashMap<>();

	String id;
	String name;
	String species;
	int age;
	String gender;
	String healthStatus;
	String enclosureId;
	String zkId;

	public void run() {
		while (true) {
			showMenu();
			String menu = in.nextLine();
			switch (menu) {
			case "1" -> registerAnimal();
			case "2" -> viewAnimals();
			case "3" -> updateAnimal();
			case "4" -> deleteAnimal();
			case "5" -> {
				System.out.println("뒤로 가기");
				return;
			}
			default -> System.out.println("잘못된 선택입니다.");

			}
		}

	}

	public void showMenu() {
		System.out.println("=== < 동물 관리 프로그램> ===");
		System.out.println("1. 동물 등록");
		System.out.println("2. 동물 조회");
		System.out.println("3. 동물 수정");
		System.out.println("4. 동물 삭제");
		System.out.println("5. 뒤로가기");
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		System.out.println("선택>> ");
	}

	public void registerAnimal() {
		System.out.println("새 동물을 등록합니다.");
		while (true) {
//			<<  정보 입력 받기  >>
			inputInformation();

//			<<  정보 확인하고 확답 받기 & 동물 등록 후 완료 메시지  >> 
			System.out.println("\n입력하신 정보를 확인하세요.");
			System.out.printf("%s / %s / %s / %d / %s / %s / %s / %s \n", id, name, species, age, gender, healthStatus,
					enclosureId, zkId);

			while (true) {
				System.out.println("1.등록 2.다시입력");
				String answer = in.nextLine();

				if (answer.equals("1")) {
					// < 동물 등록 >
					Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId, zkId);
					animals.put(id, animal);
					System.out.println("동물 등록 완료");
					return;
				} else if (answer.equals("2")) {
					// < 다시입력 > (내부 while만 깨고 외부 while은 계속 진행)
					break;
				} else {
					System.out.println("잘못된 선택입니다.");
				}
			}
		}
	}

	public void inputInformation() {
		System.out.println("동물 ID : ");
		id = in.nextLine(); // (id 자동 생성)

		System.out.println("동물 이름 : ");
		name = in.nextLine();

		System.out.println("동물 종 : ");
		species = in.nextLine();

		System.out.println("동물 나이 : ");
		age = Integer.parseInt(in.nextLine());

		System.out.println("동물 성별 : ");
		gender = in.nextLine();

		System.out.println("동물 건강상태 : ");
		healthStatus = in.nextLine();

		System.out.println("케이지 ID : ");
		enclosureId = in.nextLine();

		System.out.println("사육사 ID : ");
		zkId = in.nextLine();
	}

	public void viewAnimals() {

//		<<  2-1. 전체 동물 목록 조회  >>
		System.out.println("동물 목록");
		for (Map.Entry<String, Animal> ent : animals.entrySet()) {
			System.out.println(ent.getValue());
		}

		// 2-2. Id로 검색
		searchId();

		// 2-3. 동물 이름으로 검색
		searchName();

		// 2-4. 동물 종으로 검색
		searchSpecies();

		// 2-5. 메뉴로 돌아가기
		// 메인 while문으로 돌아가기. break; ??

	}

	public void updateAnimal() {

		// << 수정할 동물 검색 >>
		System.out.println("수정할 동물 검색");

		// 3-1. Id로 검색
		searchId();

		// 3-2. 동물 이름으로 검색
		searchName();

		// << 동물 정보 수정 >>

		// << 수정 완료 정보와 메시지 >>
		System.out.println("동물 수정 완료");

		// 3-3. 메뉴로 돌아가기

	}

	public void deleteAnimal() {

		// << 삭제할 동물 검색 >>
		System.out.println("삭제할 동물 검색");

		// 4-1. Id로 검색
		searchId();

		// 4-2. 동물 이름으로 검색
		searchName();

		// << 동물 정보 삭제 >>

		// << 삭제 완료 메시지 >>
		System.out.println("동물 삭제 완료");

		// 4-3. 메뉴로 돌아가기

	}

	public Animal searchId() {

		return null;
	}

	public Animal searchName() {

		// (gpt)
//		Scanner scanner = new Scanner(System.in);
//		System.out.print("검색할 동물 이름을 입력하세요: ");
//		String searchName = scanner.nextLine();
//
//		boolean found = false;
//
//		for (Animal animal : animalMap.values()) {
//		    if (animal.getName().equalsIgnoreCase(searchName)) {
//		        System.out.println(animal);
//		        found = true;
//		    }
//		}
//
//		if (!found) {
//		    System.out.println("해당 이름의 동물을 찾을 수 없습니다.");
//		}

		return null;
	}

	public Animal searchSpecies() {

		// (gpt)
//		System.out.print("검색할 키워드를 입력하세요 (이름 또는 종): ");
//		String keyword = scanner.nextLine().toLowerCase();
//
//		boolean found = false;
//
//		for (Animal animal : animalMap.values()) {
//		    if (animal.getName().toLowerCase().contains(keyword) || animal.getSpecies().toLowerCase().contains(keyword)) {
//		        System.out.println(animal);
//		        found = true;
//		    }
//		}
//
//		if (!found) {
//		    System.out.println("검색 결과가 없습니다.");
//		}

		return null;
	}

	// 입력에 대한 예외를 처리하는 메소드들

}

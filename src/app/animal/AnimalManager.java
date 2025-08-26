package app.animal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

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
		System.out.println("5. 뒤로 가기");
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
			System.out.printf("%s / %s / %s / %d / %s / %s / %s / %s \n", id,
					name, species, age, gender, healthStatus, enclosureId,
					zkId);

			while (true) {
				System.out.println("1.등록 2.다시입력");
				String answer = in.nextLine();

				if (answer.equals("1")) {
					// < 동물 등록 >
					Animal animal = new Animal(id, name, species, age, gender,
							healthStatus, enclosureId, zkId);
					animals.put(id, animal);
					System.out.println("동물 등록 완료 \n");
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
		id = in.nextLine(); // (id 자동 생성 적용)

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
		while (true) {
			System.out.println("\n1.동물 목록 조회");
			System.out.println("2.동물 ID로 검색");
			System.out.println("3.동물 이름으로 검색");
			System.out.println("4.동물 종으로 검색");
			System.out.println("5.뒤로 가기");
			System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
			System.out.println("선택>> ");

			String menu = in.nextLine();
			switch (menu) {
			case "1" -> {
				System.out.println("동물 목록");
				for (Map.Entry<String, Animal> ent : animals.entrySet()) {
					System.out.println(ent.getValue());
				}
				if (animals.isEmpty()) {
					System.out.println("(동물 목록 없음)");
				}
			}
			case "2" -> searchId();
			case "3" -> searchName();
			case "4" -> searchSpecies();
			case "5" -> {
				System.out.println("뒤로 가기");
				return;
			}
			default -> System.out.println("잘못된 선택입니다.");
			}
		}
	}

	public Animal searchId() {

		return null;
	}

	public void searchName() {
		while (true) {
			if (animals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 이름 : ");
				String findName = in.nextLine();

				List<Animal> findAnimal = animals.values().stream()
						.filter(k -> findName.equals(k.getName()))
						.collect(Collectors.toList());

				if (findAnimal.isEmpty()) {
					System.out.println("해당 이름의 동물을 찾을 수 없습니다.");
				} else {
					findAnimal.forEach(System.out::println);
					return;
				}
			}
		}
	}

	public void searchSpecies() {
		while (true) {
			if (animals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 종 : ");
				String findSpecies = in.nextLine();

				List<Animal> findAnimals = animals.values().stream()
						.filter(k -> findSpecies.equals(k.getSpecies()))
						.collect(Collectors.toList());

				if (findAnimals.isEmpty()) {
					System.out.println("해당 종의 동물을 찾을 수 없습니다.");
				} else {
					findAnimals.forEach(System.out::println);
					return;
				}
			}
		}
	}

	public void updateAnimal() {
		if (animals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			//	<<  수정할 ID로 검색  >>
			System.out.println("수정할 동물 ID 입력 : ");
			String findId = in.nextLine();
			Animal animal = animals.get(findId);
			System.out.println(animal);

			//	<< 원하는 정보 선택 >>
			while (true) {
				System.out.println("수정할 정보 선택 : ");
				System.out.println("1.종");
				System.out.println("2.나이");
				System.out.println("3.성별");
				System.out.println("4.건강상태");
				System.out.println("5.나가기");

				String select = in.nextLine();

				//	<<  정보 수정  >>
				switch (select) {
				case "1" -> {
					System.out.println("수정할 종 : ");
					String sp = in.nextLine();
					animal.setSpecies(sp);
					System.out.println("동물 수정 완료");
					System.out.println(animal);
				}
				case "2" -> {
					System.out.println("수정할 나이 : ");
					String age = in.nextLine();
					int ag = Integer.parseInt(age);
					animal.setAge(ag);
					System.out.println("동물 수정 완료");
					System.out.println(animal);
				}
				case "3" -> {
					System.out.println("수정할 성별 : ");
					String gen = in.nextLine();
					animal.setGender(gen);
					System.out.println("동물 수정 완료");
					System.out.println(animal);
				}
				case "4" -> {
					System.out.println("수정할 건강상태 : ");
					String heal = in.nextLine();
					animal.setHealthStatus(heal);
					System.out.println("동물 수정 완료");
					System.out.println(animal);
				}
				case "5" -> {
					System.out.println("나가기");
					return;
				}
				default -> System.out.println("잘못된 선택입니다.");
				}
			}
		}
	}

	public void deleteAnimal() {
		if (animals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			//	<<  수정할 ID로 검색  >>
			System.out.println("삭제할 동물 ID 입력 : ");
			String findId = in.nextLine();

			//	<<  동물 정보 삭제  >>
			animals.remove(findId);
			System.out.println("동물 삭제 완료");
		}
	}

}

package app.animal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import app.animal.AnimalEnum.Species;
import app.common.id.IdGeneratorUtil;

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

	//	<< 1. 동물 등록 >>
	public void registerAnimal() {
		System.out.println("새 동물을 등록합니다.");
		while (true) {
			//	<<  정보 입력 받기  >>
			inputInformation();

			//	<<  정보 확인하고 확답 받기 & 동물 등록 후 완료 메시지  >> 
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

	//	< 동물 신규 등록시, 정보 입력 받는 메소드 >
	public void inputInformation() {
		//  < ID 자동 생성 >
		id = IdGeneratorUtil.generateId();

		//  < 동물 이름 입력 >
		System.out.println("동물 이름 : ");
		name = in.nextLine();

		//  < 동물 종 입력 >
		while (true) {
			System.out.println("동물 종 : ");
			String inSpe = in.nextLine().trim();
			if (Species.isValid(inSpe)) {
				species = inSpe;
				break;
			} else {
				System.out.println("동물 종을 정확히 입력하세요.");
				System.out.println("등록 가능한 종 목록:");
				for (Species s : Species.values()) {
					System.out.print(s.name() + " ");
				}
				System.out.println();
			}
		}

		//  < 나이 입력 >
		while (true) {
			System.out.println("동물 나이 : ");
			String inAge = in.nextLine();
			if (!StringIsLong(inAge)) {
				System.out.println("숫자로 정확히 입력해 주세요.");
			} else {
				int longAge = Integer.parseInt(inAge);
				if (0 <= longAge && longAge < 200) {
					age = longAge;
					break;
				} else {
					System.out.println("다시 입력해 주세요.");
				}
			}
		}

		//	< 성별 입력 >
		while (true) {
			System.out.println("동물 성별(수컷/암컷) : ");
			String inGen = in.nextLine();
			if (inGen.equals("수컷") || inGen.equals("암컷")) {
				gender = inGen;
				break;
			} else {
				System.out.println("다시 입력해 주세요.");
			}
		}

		//	< 건강상태 입력 >
		while (true) {
			System.out.println("동물 건강상태(양호/보통/나쁨) : ");
			String inHeal = in.nextLine();
			if (inHeal.equals("양호") || inHeal.equals("보통")
					|| inHeal.equals("나쁨")) {
				healthStatus = inHeal;
				break;
			} else {
				System.out.println("다시 입력해 주세요.");
			}
		}

		//	< 케이지 ID 입력 >
		System.out.println("케이지 ID : ");
		enclosureId = in.nextLine();

		//	< 사육사 ID 입력 >
		System.out.println("사육사 ID : ");
		zkId = in.nextLine();
	}

	//	< 입력된 String 값이 Long값으로 변환 가능한지 체크하는 메소드 >
	public static boolean StringIsLong(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	//	<< 2. 동물 조회 >>
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

	//	<< 2-3. 동물 ID로 검색 >>
	public void searchId() {
		while (true) {
			if (animals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 ID : ");
				String findId = in.nextLine();
				if (animals.containsKey(findId)) {
					Animal animal = animals.get(findId);
					System.out.println(animal);
					return;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}
			}
		}
	}

	//	<< 2-4. 동물 이름으로 검색 >>
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

	//	<< 2-5. 동물 종으로 검색 >>
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

	//	<< 3. 동물 수정 >>
	public void updateAnimal() {
		if (animals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			//	< 수정할 ID로 검색 >
			Animal animal = null;
			String findId = null;
			while (true) {
				System.out.println("수정할 동물 ID 입력 : ");
				findId = in.nextLine();
				if (animals.containsKey(findId)) {
					animal = animals.get(findId);
					System.out.println(animal);
					break;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}

			}

			//	< 원하는 정보 선택 >
			while (true) {
				System.out.println("수정할 정보 선택 : ");
				System.out.println("1.종");
				System.out.println("2.나이");
				System.out.println("3.성별");
				System.out.println("4.건강상태");
				System.out.println("5.나가기");

				String select = in.nextLine();

				//	< 정보 수정 >
				switch (select) {
				case "1" -> {
					while (true) {
						System.out.println("수정할 종 : ");
						String sp = in.nextLine().trim();
						if (Species.isValid(sp)) {
							animal.setSpecies(sp);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("동물 종을 정확히 입력하세요.");
							System.out.println("등록 가능한 종 목록:");
							for (Species s : Species.values()) {
								System.out.print(s.name() + " ");
							}
							System.out.println();
						}
					}
				}
				case "2" -> {
					while (true) {
						System.out.println("수정할 나이 : ");
						String age = in.nextLine();
						if (!StringIsLong(age)) {
							System.out.println("숫자로 정확히 입력해 주세요.");
						} else {
							int longAge = Integer.parseInt(age);
							if (0 <= longAge && longAge < 200) {
								animal.setAge(longAge);
								System.out.println("동물 수정 완료");
								System.out.println(animal);
								break;
							} else {
								System.out.println("다시 입력해 주세요.");
							}
						}
					}
				}
				case "3" -> {
					while (true) {
						System.out.println("수정할 성별(수컷/암컷) : ");
						String gen = in.nextLine();
						if (gen.equals("수컷") || gen.equals("암컷")) {
							animal.setGender(gen);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("다시 입력해 주세요.");
						}
					}
				}
				case "4" -> {
					while (true) {
						System.out.println("수정할 건강상태(양호/보통/나쁨) : ");
						String heal = in.nextLine();
						if (heal.equals("양호") || heal.equals("보통")
								|| heal.equals("나쁨")) {
							animal.setHealthStatus(heal);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("다시 입력해 주세요.");
						}
					}
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

	//	<< 4. 동물 삭제 >>
	public void deleteAnimal() {
		if (animals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			//	< 수정할 ID로 검색 >
			Animal animal = null;
			String findId = null;
			while (true) {
				System.out.println("수정할 동물 ID 입력 : ");
				findId = in.nextLine();
				if (animals.containsKey(findId)) {
					animal = animals.get(findId);
					System.out.println(animal);
					break;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}

			}

			//	< 동물 정보 삭제 >
			animals.remove(findId);
			System.out.println("동물 삭제 완료");
		}
	}

}

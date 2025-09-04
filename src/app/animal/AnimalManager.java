package app.animal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TableUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.repository.MemoryAnimalRepository;
import app.repository.interfaces.AnimalRepository;

public class AnimalManager {
	private final AnimalRepository repository = new MemoryAnimalRepository();

	String id;
	String name;
	String species;
	int age;
	String gender;
	String healthStatus;
	String enclosureId;
	String zkId;

	/**
	 * 기본 생성자 Repository 패턴으로 변경되어 더 이상 Map을 직접 관리하지 않습니다.
	 */
	public AnimalManager() {
		// Repository는 필드에서 초기화됨
	}

	/**
	 * 기존 인스턴스를 싱글톤과 동기화합니다. Repository 패턴에서는 더 이상 필요하지 않지만 호환성을 위해 유지합니다.
	 */
	public void syncWithSingleton() {
		// Repository 패턴에서는 자동으로 동기화됨
	}

	public void handleAnimalManagement() {
		while (true) {
			displayAnimalMenu();
			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printRegisterMenuTitle();
				UIUtil.printSeparator('━');
				registerAnimal();
			}
			case 2 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printViewMenuTitle();
				UIUtil.printSeparator('━');
				viewAnimals();
			}
			case 3 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printEditMenuTitle();
				UIUtil.printSeparator('━');
				editAnimal();
			}
			case 4 -> {
				UIUtil.printSeparator('━');
				TextArtUtil.printRemoveMenuTitle();
				UIUtil.printSeparator('━');
				removeAnimal();
			}
			case 0 -> {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 선택입니다.");
			}
		}
	}

	private static void displayAnimalMenu() {
		String[] option = { "동물 등록", "동물 조회", "동물 수정", "동물 삭제" };
		String[] specialOptions = { "뒤로가기" };
		UIUtil.printSeparator('━');
		MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printAnimalMenuTitle, option, specialOptions);
	}

	public void registerAnimal() {

		while (true) {
			// << 정보 입력 받기 >>
			inputInformation();

			// << 정보 확인하고 확답 받기 & 동물 등록 후 완료 메시지 >>
			System.out.println("\n입력하신 정보를 확인하세요.");

			System.out.printf("%s / %s / %s / %d / %s / %s / %s / %s \n", id, name, species, age, gender, healthStatus,
					enclosureId, zkId);

			while (true) {
				System.out.println("1.등록 2.다시입력");
				String answer = InputUtil.getStringInput();

				if (answer.equals("1")) {
					// < 동물 등록 >

					Animal animal = repository.createAnimal(id, name, species, age, gender, healthStatus, enclosureId,
							zkId);

					// 싱글톤과 동기화 (추가된 부분)
					syncWithSingleton();

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

	// < 동물 신규 등록시, 정보 입력 받는 메소드 >
	public void inputInformation() {
		// < ID 자동 생성 >
		id = IdGeneratorUtil.generateId();

		// < 동물 이름 입력 >
		while (true) {
			System.out.println("동물 이름 : ");
			String inName = InputUtil.getStringInput();
			List<Animal> allAnimals = repository.getAnimalList();
			if (allAnimals.isEmpty()) {
				name = inName;
				break;
			} else {
				boolean findName = allAnimals.stream().anyMatch(n -> inName.equals(n.getName()));

				if (!findName) {
					name = inName;
					break;
				} else {
					System.out.println("동일한 이름이 있습니다. 다시 입력해 주세요.");
				}
			}
		}

		// < 동물 종 입력 >
		while (true) {
			System.out.println("동물 종 : ");
			System.out.println("종 목록:");
			for (AnimalEnum s : AnimalEnum.values()) {
				System.out.print(s.name() + " ");
			}
			System.out.println();

			String inSpe = InputUtil.getStringInput().trim();
			if (AnimalEnum.isValid(inSpe)) {
				species = inSpe;
				break;
			} else {
				System.out.println("동물 종을 정확히 입력하세요.");
			}
		}

		// < 나이 입력 >
		age = MenuUtil.Question.askNumberInputInt("동물의 나이를 입력하세요.");

		// < 성별 입력 >
		String[] choices = { "수컷", "암컷" };
		int input = MenuUtil.Question.askSingleChoice("동물의 성별을 입력하세요.", choices);
		if (input == 1) {
			gender = "수컷";
		} else if (input == 2) {
			gender = "암컷";

		}

//		while (true) {
//			System.out.println("동물 성별(수컷/암컷) : ");
//			String inGen = InputUtil.getStringInput();
//			if (inGen.equals("수컷") || inGen.equals("암컷")) {
//				gender = inGen;
//				break;
//			} else {
//				System.out.println("다시 입력해 주세요.");
//			}
//		}

		// < 건강상태 입력 >
		while (true) {
			System.out.println("동물 건강상태(양호/보통/나쁨) : ");
			String inHeal = InputUtil.getStringInput();
			if (inHeal.equals("양호") || inHeal.equals("보통") || inHeal.equals("나쁨")) {
				healthStatus = inHeal;
				break;
			} else {
				System.out.println("다시 입력해 주세요.");
			}
		}

		// < 케이지 ID 입력 >
		System.out.println("케이지 ID : ");
		enclosureId = InputUtil.getStringInput();

		// < 사육사 ID 입력 >
		System.out.println("사육사 ID : ");
		zkId = InputUtil.getStringInput();
	}

	// < 입력된 String 값이 int 값으로 변환 가능한지 체크하는 메소드 >

	public static boolean StringIsInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// << 2. 동물 조회 >>
	public void viewAnimals() {
		while (true) {
			displayViewMenu();

			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> {
				System.out.println("동물 목록");

				List<Animal> allAnimals = repository.getAnimalList();
				for (Animal animal : allAnimals) {
					System.out.println(animal);
				}
				if (allAnimals.isEmpty()) {
					System.out.println("(동물 목록 없음)");
				}
			}
			case 2 -> searchId();
			case 3 -> searchName();
			case 4 -> searchSpecies();
			case 0 -> {
				System.out.println("뒤로 가기");
				return;
			}
			default -> System.out.println("잘못된 선택입니다.");
			}
		}
	}

	private static void displayViewMenu() {
		String[] option = { "동물 목록 조회", "동물 ID로 검색", "동물 이름으로 검색", "동물 종으로 검색" };
		String[] specialOptions = { "뒤로가기" };
		UIUtil.printSeparator('━');
		MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printVisitorMenuTitle, option, specialOptions);
	}

	// << 2-3. 동물 ID로 검색 >>
	public void searchId() {
		while (true) {
			List<Animal> allAnimals = repository.getAnimalList();
			if (allAnimals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 ID : ");
				String findId = InputUtil.getStringInput();
				Animal animal = repository.getAnimalById(findId);
				if (animal != null) {
					System.out.println(animal);
					return;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}
			}
		}
	}

	// << 2-4. 동물 이름으로 검색 >>
	public void searchName() {
		while (true) {
			List<Animal> allAnimals = repository.getAnimalList();
			if (allAnimals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 이름 : ");
				String findName = InputUtil.getStringInput();

				List<Animal> findAnimal = repository.getAnimalsByName(findName);

				if (findAnimal.isEmpty()) {
					System.out.println("해당 이름의 동물을 찾을 수 없습니다.");
				} else {
					findAnimal.forEach(System.out::println);
					return;
				}
			}
		}
	}

	// << 2-5. 동물 종으로 검색 >>
	public void searchSpecies() {
		while (true) {
			List<Animal> allAnimals = repository.getAnimalList();
			if (allAnimals.isEmpty()) {
				System.out.println("(동물 목록 없음)");
				return;
			} else {
				System.out.println("검색할 동물 종 : ");
				String findSpecies = InputUtil.getStringInput();

				List<Animal> findAnimals = repository.getAnimalsBySpecies(findSpecies);

				if (findAnimals.isEmpty()) {
					System.out.println("해당 종의 동물을 찾을 수 없습니다.");
				} else {
					findAnimals.forEach(System.out::println);
					return;
				}
			}
		}
	}

	// << 3. 동물 수정 >>
	public void editAnimal() {
		List<Animal> allAnimals = repository.getAnimalList();
		if (allAnimals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			// < 수정할 ID로 검색 >
			Animal animal = null;
			String findId = null;
			while (true) {
				System.out.println("수정할 동물 ID 입력 : ");
				findId = InputUtil.getStringInput();
				animal = repository.getAnimalById(findId);
				if (animal != null) {
					System.out.println(animal);
					break;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}

			}

			// < 원하는 정보 선택 >
			while (true) {
				System.out.println("수정할 정보 선택 : ");
				System.out.println("1.종");
				System.out.println("2.나이");
				System.out.println("3.성별");
				System.out.println("4.건강상태");
				System.out.println("0.나가기");

				int choice = InputUtil.getIntInput();

				// < 정보 수정 >
				switch (choice) {
				case 1 -> {
					while (true) {
						System.out.println("수정할 종 : ");

						System.out.println("종 목록:");
						for (AnimalEnum s : AnimalEnum.values()) {
							System.out.print(s.name() + " ");
						}
						System.out.println();

						String sp = InputUtil.getStringInput().trim();
						if (AnimalEnum.isValid(sp)) {
							animal.setSpecies(sp);
							repository.updateAnimal(animal.getId(), animal);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("동물 종을 정확히 입력하세요.");
							System.out.println("등록 가능한 종 목록:");
							for (AnimalEnum s : AnimalEnum.values()) {
								System.out.print(s.name() + " ");
							}
							System.out.println();
						}
					}
				}
				case 2 -> {
					while (true) {
						System.out.println("수정할 나이 : ");
						String age = InputUtil.getStringInput();
						if (!StringIsInt(age)) {
							System.out.println("숫자로 정확히 입력해 주세요.");
						} else {
							int longAge = Integer.parseInt(age);
							if (0 <= longAge && longAge < 200) {
								animal.setAge(longAge);
								repository.updateAnimal(animal.getId(), animal);
								System.out.println("동물 수정 완료");
								System.out.println(animal);
								break;
							} else {
								System.out.println("다시 입력해 주세요.");
							}
						}
					}
				}
				case 3 -> {
					while (true) {
						System.out.println("수정할 성별(수컷/암컷) : ");
						String gen = InputUtil.getStringInput();
						if (gen.equals("수컷") || gen.equals("암컷")) {
							animal.setGender(gen);
							repository.updateAnimal(animal.getId(), animal);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("다시 입력해 주세요.");
						}
					}
				}
				case 4 -> {
					while (true) {
						System.out.println("수정할 건강상태(양호/보통/나쁨) : ");
						String heal = InputUtil.getStringInput();
						if (heal.equals("양호") || heal.equals("보통") || heal.equals("나쁨")) {
							animal.setHealthStatus(heal);
							repository.updateAnimal(animal.getId(), animal);
							System.out.println("동물 수정 완료");
							System.out.println(animal);
							break;
						} else {
							System.out.println("다시 입력해 주세요.");
						}
					}
				}
				case 0 -> {
					System.out.println("나가기");
					return;
				}
				default -> System.out.println("잘못된 선택입니다.");
				}
			}
		}
	}

	// << 4. 동물 삭제 >>
	public void removeAnimal() {
		List<Animal> allAnimals = repository.getAnimalList();
		if (allAnimals.isEmpty()) {
			System.out.println("(동물 목록 없음)");
			return;
		} else {
			// < 삭제할 ID로 검색 >
			Animal animal = null;
			String findId = null;
			while (true) {
				System.out.println("삭제할 동물 ID 입력 : ");
				findId = InputUtil.getStringInput();
				animal = repository.getAnimalById(findId);
				if (animal != null) {
					System.out.println(animal);
					break;
				} else {
					System.out.println("ID를 다시 입력해 주세요. ");
				}

			}

			// < 동물 정보 삭제 >
			repository.removeAnimal(findId);
			System.out.println("동물 삭제 완료");
		}
	}

	// =================================================================
	// EnclosureManager 연동을 위한 배치 관리 메서드들
	// =================================================================

	/**
	 * 배치 가능한 동물이 있는지 확인합니다. enclosureId가 null이거나 빈 문자열인 동물이 배치 가능한 동물로 간주됩니다.
	 * 
	 * @return 배치 가능한 동물 존재 여부
	 */
	public boolean hasAvailableAnimals() {
		List<Animal> allAnimals = repository.getAnimalList();
		return allAnimals.stream()
				.anyMatch(animal -> animal.getEnclosureId() == null || animal.getEnclosureId().trim().isEmpty());
	}

	/**
	 * Working Data Pattern: 배치 가능한 동물들의 작업용 복사본을 반환합니다. 원본 데이터를 수정하지 않고 작업할 수 있도록
	 * 새로운 HashMap으로 복사합니다.
	 * 
	 * @return 배치 가능한 동물들의 복사본 Map
	 */
	public Map<String, Animal> getWorkingCopyOfAvailableAnimals() {
		Map<String, Animal> availableAnimals = getAvailableAnimals();
		// 새로운 HashMap을 생성하여 복사본 반환
		return new HashMap<>(availableAnimals);
	}

	/**
	 * 배치 가능한 동물들의 목록을 반환합니다. enclosureId가 null이거나 빈 문자열인 동물들을 필터링하여 반환합니다.
	 * 
	 * @return 배치 가능한 동물들의 Map
	 */
	public Map<String, Animal> getAvailableAnimals() {
		List<Animal> allAnimals = repository.getAnimalList();
		return allAnimals.stream().filter(animal -> {
			String enclosureId = animal.getEnclosureId();
			return enclosureId == null || enclosureId.trim().isEmpty();
		}).collect(Collectors.toMap(Animal::getId, animal -> animal));
	}

	/**
	 * 배치 가능한 동물들을 테이블 형태로 출력합니다. EnclosureManager의 동물 입사 관리에서 사용됩니다. TableUtil을
	 * 사용하여 일관된 형태의 표를 출력합니다.
	 */
	public void displayAvailableAnimalsTable() {
		Map<String, Animal> availableAnimals = getAvailableAnimals();

		if (availableAnimals.isEmpty()) {
			System.out.println("  배치 가능한 동물이 없습니다.");
			return;
		}

		// TableUtil에 맞는 헤더와 데이터 준비
		String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "Health" };

		// 데이터 배열 생성
		String[][] data = new String[availableAnimals.size()][];
		int index = 0;

		for (Animal animal : availableAnimals.values()) {
			data[index] = new String[] { animal.getId(), animal.getName(), animal.getSpecies(),
					String.valueOf(animal.getAge()), animal.getGender(), animal.getHealthStatus() };
			index++;
		}

		// TableUtil을 사용하여 표 출력
		String title = String.format("배치 가능한 동물 목록 (총 %d마리)", availableAnimals.size());
		TableUtil.printTable(title, headers, data);
	}

	/**
	 * 전체 동물 목록에서 특정 ID의 동물을 검색합니다.
	 * 
	 * @param animalId 검색할 동물 ID
	 * @return Optional<Animal> 검색된 동물 (없으면 empty)
	 */
	public Optional<Animal> getAnimalFromAll(String animalId) {
		Animal animal = repository.getAnimalById(animalId);
		return Optional.ofNullable(animal);
	}

	/**
	 * 특정 동물을 배치 가능한 상태에서 제거합니다. 동물의 enclosureId를 설정하여 배치된 상태로 변경합니다.
	 * 
	 * @param animalId    배치할 동물 ID
	 * @param enclosureId 배치할 사육장 ID
	 * @return 배치된 동물 객체 (없으면 null)
	 */
	public Animal removeAvailableAnimal(String animalId, String enclosureId) {
		Animal animal = repository.getAnimalById(animalId);
		if (animal != null) {
			// 배치 가능한 동물인지 확인
			String currentEnclosureId = animal.getEnclosureId();
			if (currentEnclosureId == null || currentEnclosureId.trim().isEmpty()) {
				animal.setEnclosureId(enclosureId);
				repository.updateAnimal(animalId, animal);
				return animal;
			}
		}
		return null; // 동물이 없거나 이미 배치된 경우
	}

	/**
	 * 동물을 사육장에서 해제하여 다시 배치 가능한 상태로 만듭니다. 동물의 enclosureId를 null로 설정합니다.
	 * 
	 * @param animalId 해제할 동물 ID
	 * @return 해제된 동물 객체 (없으면 null)
	 */
	public Animal releaseAnimalFromEnclosure(String animalId) {
		Animal animal = repository.getAnimalById(animalId);
		if (animal != null) {
			animal.setEnclosureId(null);
			repository.updateAnimal(animalId, animal);
			return animal;
		}
		return null;
	}

	/**
	 * 동물이 특정 사육장에 배치되어 있는지 확인합니다.
	 * 
	 * @param animalId    확인할 동물 ID
	 * @param enclosureId 확인할 사육장 ID
	 * @return 해당 사육장에 배치되어 있으면 true
	 */
	public boolean isAnimalInEnclosure(String animalId, String enclosureId) {
		Animal animal = repository.getAnimalById(animalId);
		if (animal != null) {
			String animalEnclosureId = animal.getEnclosureId();
			return enclosureId.equals(animalEnclosureId);
		}
		return false;
	}

	/**
	 * AnimalManager의 인스턴스를 반환합니다. Singleton 패턴을 위한 메서드입니다.
	 * 
	 * @return AnimalManager 인스턴스
	 */
	private static AnimalManager instance = null;

	public static AnimalManager getInstance() {
		if (instance == null) {
			instance = new AnimalManager();
		}
		return instance;
	}

}

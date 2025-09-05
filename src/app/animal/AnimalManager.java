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
			case 1 -> registerAnimal();
			case 2 -> viewAnimals();
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
		UIUtil.printSeparator('━');
		TextArtUtil.printRegisterMenuTitle();
		UIUtil.printSeparator('━');

		id = IdGeneratorUtil.generateId();
		inputName("동물의 이름을 입력하세요.");
		inputSpecies("동물의 종을 입력하세요.");
		age = MenuUtil.Question.askNumberInputInt("동물의 나이를 입력하세요.");
		inputGender("동물의 성별을 입력하세요.");
		inputHealth("동물의 건강상태를 입력하세요.");

		// < 케이지 ID 입력 >
		System.out.println("케이지 ID : ");
		enclosureId = InputUtil.getStringInput();

		String[] headers = { "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureId" };
		String[][] data = { { name, species, Integer.toString(age), gender, healthStatus, enclosureId } };
		TableUtil.printTable("입력하신 정보는 아래와 같습니다.", headers, data);

		boolean choice = MenuUtil.Question.askYesNo("등록하시겠습니까?");
		if (choice) {
			Animal animal = repository.createAnimal(id, name, species, age, gender, healthStatus, enclosureId);
			syncWithSingleton();
			System.out.printf(MenuUtil.DEFAULT_PREFIX + "동물 등록 성공!");
			System.out.println(animal);
		}
	}

	public void inputName(String question) {
		while (true) {
			String inName = MenuUtil.Question.askTextInput(question);
			List<Animal> findAnimal = repository.getAnimalsByName(inName);
			if (findAnimal.isEmpty()) {
				name = inName;
				break;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "동일한 이름이 있습니다.");
				System.out.println();
			}
		}
	}

	public void inputSpecies(String question) {
		String[] speciesChoices = { "Lion", "Tiger", "Bear", "Elephant", "Wolf", "Eagle", "Owl", "Snake" };
		int inSpecies = MenuUtil.Question.askSingleChoice(question, speciesChoices);
		switch (inSpecies) {
		case 1 -> species = "Lion";
		case 2 -> species = "Tiger";
		case 3 -> species = "Bear";
		case 4 -> species = "Elephant";
		case 5 -> species = "Wolf";
		case 6 -> species = "Eagle";
		case 7 -> species = "Owl";
		case 8 -> species = "Snake";
		default -> System.out.println();
		}
	}

	public void inputGender(String question) {
		String[] genderChoices = { "Male", "Female" };
		int inGender = MenuUtil.Question.askSingleChoice(question, genderChoices);
		if (inGender == 1) {
			gender = "Male";
		} else if (inGender == 2) {
			gender = "Female";
		}
	}

	public void inputHealth(String question) {
		String[] healthChoices = { "Good", "Fair", "Poor" };
		int inHealth = MenuUtil.Question.askSingleChoice(question, healthChoices);
		if (inHealth == 1) {
			healthStatus = "Good";
		} else if (inHealth == 2) {
			healthStatus = "Fair";
		} else if (inHealth == 3) {
			healthStatus = "Poor";
		}
	}

	// << 2. 동물 조회 >>
	public void viewAnimals() {
		if (repository.count() == 0) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
			return;
		}
		while (true) {
			displayViewMenu();
			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> viewAllAnimals();
			case 2 -> searchAnimalId();
			case 3 -> searchAnimalName();
			case 4 -> searchAnimalSpecies();
			case 0 -> {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
				return;
			}
			default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 선택입니다.");
			}
		}
	}

	private static void displayViewMenu() {
		String[] option = { "전체 동물 목록", "동물 ID로 검색", "동물 이름으로 검색", "동물 종별로 검색" };
		String[] specialOptions = { "뒤로가기" };
		UIUtil.printSeparator('━');
		MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printViewMenuTitle, option, specialOptions);
	}

	public void viewAllAnimals() {
		String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureId" };
		List<Animal> allAnimals = repository.findAll();
		String[][] data = new String[allAnimals.size()][];

		for (int i = 0; i < allAnimals.size(); i++) {
			Animal animal = allAnimals.get(i);

			data[i] = new String[] { animal.getId(), animal.getName(), animal.getSpecies(),
					String.valueOf(animal.getAge()), animal.getGender(), animal.getHealthStatus(),
					animal.getEnclosureId() };
		}
		String title = String.format("동물 목록 (총 %d개)", repository.count());
		TableUtil.printTable(title, headers, data);
	}

	// << 2-3. 동물 ID로 검색 >>
	public void searchAnimalId() {
		UIUtil.printSeparator('━');
		while (true) {
			String findId = MenuUtil.Question.askTextInput("검색할 동물 ID를 입력하세요.");
			Animal animal = repository.getAnimalById(findId);
			if (animal != null) {
				System.out.println(animal);
				return;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "ID를 다시 입력해 주세요. ");
				System.out.println();
			}
		}
	}

	// << 2-4. 동물 이름으로 검색 >>
	public void searchAnimalName() {
		UIUtil.printSeparator('━');
		while (true) {
			String findName = MenuUtil.Question.askTextInput("검색할 동물 이름을 입력하세요.");

			List<Animal> findAnimal = repository.getAnimalsByName(findName);

			if (findAnimal.isEmpty()) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "해당 이름의 동물이 없습니다.");
				System.out.println();
			} else {
				findAnimal.forEach(System.out::println);
				return;
			}
		}
	}

	// << 2-5. 동물 종으로 검색 >>
	public void searchAnimalSpecies() {
		UIUtil.printSeparator('━');
		String[] speciesChoices = { "Lion", "Tiger", "Bear", "Elephant", "Wolf", "Eagle", "Owl", "Snake" };
		int inSpeciesNum = MenuUtil.Question.askSingleChoice("검색할 동물의 종을 입력하세요.", speciesChoices);
		String inSpecies = "";
		switch (inSpeciesNum) {
		case 1 -> inSpecies = "Lion";
		case 2 -> inSpecies = "Tiger";
		case 3 -> inSpecies = "Bear";
		case 4 -> inSpecies = "Elephant";
		case 5 -> inSpecies = "Wolf";
		case 6 -> inSpecies = "Eagle";
		case 7 -> inSpecies = "Owl";
		case 8 -> inSpecies = "Snake";
		default -> System.out.println();
		}

		String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureId" };
		List<Animal> findAnimals = repository.getAnimalsBySpecies(inSpecies);
		String[][] data = new String[findAnimals.size()][];

		for (int i = 0; i < findAnimals.size(); i++) {
			Animal animal = findAnimals.get(i);

			data[i] = new String[] { animal.getId(), animal.getName(), animal.getSpecies(),
					String.valueOf(animal.getAge()), animal.getGender(), animal.getHealthStatus(),
					animal.getEnclosureId() };
		}
		String title = String.format("동물 목록 (총 %d개)", repository.count());
		TableUtil.printTable(title, headers, data);
	}

	// << 3. 동물 수정 >>
	public void editAnimal() {
		if (repository.count() == 0) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
			return;
		}

		// < 수정할 ID로 검색 >
		Animal animal = null;
		String findId = null;
		while (true) {
			findId = MenuUtil.Question.askTextInput("수정할 동물 ID를 입력하세요.");
			animal = repository.getAnimalById(findId);
			if (animal != null) {
				System.out.println(animal);
				break;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "ID를 다시 입력해 주세요. ");
				System.out.println();
			}
		}

		// < 원하는 정보 선택 >
		String[] choices = { "나이", "건강상태", "EnclosureId" };
		int choice = MenuUtil.Question.askSingleChoice("수정할 정보를 입력하세요.", choices);

		// < 정보 수정 >
		switch (choice) {
		case 1 -> editAnimalAge(animal);
		case 2 -> editAnimalHealth(animal);
		case 3 -> editAnimalEnclosureID(animal);
		default -> System.out.println();
		}

	}

	public void editAnimalAge(Animal animal) {
		int changeAge = MenuUtil.Question.askNumberInputInt("수정할 나이를 입력하세요.");
		animal.setAge(changeAge);
		System.out.println(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다.");
		System.out.println(animal);
	}

	public void editAnimalHealth(Animal animal) {
		String changeHealth = "";
		String[] healthChoices = { "Good", "Fair", "Poor" };
		int inHealth = MenuUtil.Question.askSingleChoice("수정할 건강상태를 입력하세요.", healthChoices);
		if (inHealth == 1) {
			changeHealth = "Good";
		} else if (inHealth == 2) {
			changeHealth = "Fair";
		} else if (inHealth == 3) {
			changeHealth = "Poor";
		}
		animal.setHealthStatus(changeHealth);
		System.out.println(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다.");
		System.out.println(animal);
	}

	public void editAnimalEnclosureID(Animal animal) {

	}

	// << 4. 동물 삭제 >>
	public void removeAnimal() {
		if (repository.count() == 0) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
			return;
		}
		List<Animal> allAnimals = repository.getAnimalList();
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
		repository.removeAnimal(findId);
		System.out.println("동물 삭제 완료");
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

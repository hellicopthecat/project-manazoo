package app.animal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import app.common.InputUtil;
import app.common.exception.AnimalNotFoundException;
import app.common.exception.InvalidAnimalDataException;
import app.common.exception.TransactionException;
import app.common.ui.MenuUtil;
import app.common.ui.TableUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.controller.AnimalController;

/**
 * 동물 관리 UI 담당 클래스입니다.
 * <p>
 * 리팩토링된 구조에서는 UI 로직만을 담당하며, 모든 비즈니스 로직은 AnimalController를 통해 처리합니다.
 * Manager-Controller 분리 아키텍처를 통해 책임이 명확히 분리되었습니다.
 * </p>
 * 
 * @author MANAZOO Team  
 * @version 2.0 (Controller 분리 버전)
 * @since 2.0
 */
public class AnimalManager {
    
    /**
     * 비즈니스 로직을 처리하는 Controller입니다.
     * 모든 동물 관련 비즈니스 로직은 이 Controller를 통해 처리됩니다.
     */
    private final AnimalController animalController = AnimalController.getInstance();
    
    // UI에서 임시로 사용하는 변수들
    private String tempName;
    private String tempSpecies;
    private int tempAge;
    private String tempGender;
    private String tempHealthStatus;

    /**
     * 기본 생성자입니다.
     * Controller 인스턴스는 필드에서 초기화됩니다.
     */
    public AnimalManager() {
        // Controller는 필드에서 초기화됨
    }

    /**
     * 호환성을 위해 유지하는 메서드입니다.
     * Controller 패턴에서는 자동으로 동기화됩니다.
     */
    public void syncWithSingleton() {
        // Controller 패턴에서는 자동으로 동기화됨
    }

	public void handleAnimalManagement() {
		while (true) {
			displayAnimalMenu();
			int choice = InputUtil.getIntInput();
			switch (choice) {
			case 1 -> registerAnimal();
			case 2 -> viewAnimals();
			case 3 -> editAnimal();
			case 4 -> removeAnimal();
			case 0 -> {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
				UIUtil.printSeparator('━');
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

    /**
     * 동물 등록 메뉴를 처리합니다.
     * UI 입력을 받아 AnimalController를 통해 비즈니스 로직을 처리합니다.
     */
    public void registerAnimal() {
        UIUtil.printSeparator('━');
        TextArtUtil.printRegisterMenuTitle();
        UIUtil.printSeparator('━');

        try {
            // UI 입력 받기
            tempName = inputAnimalName("동물의 이름을 입력하세요.");
            tempSpecies = inputAnimalSpecies("동물의 종을 입력하세요.");
            tempAge = MenuUtil.Question.askNumberInputInt("동물의 나이를 입력하세요.");
            tempGender = inputAnimalGender("동물의 성별을 입력하세요.");
            tempHealthStatus = inputAnimalHealth("동물의 건강상태를 입력하세요.");

            // 입력 정보 확인 테이블 출력
            String[] headers = { "Name", "Species", "Age", "Gender", "HealthStatus" };
            String[][] data = { { tempName, tempSpecies, Integer.toString(tempAge), tempGender, tempHealthStatus } };
            TableUtil.printTable("입력하신 정보는 아래와 같습니다.", headers, data);

            // 등록 확인
            boolean choice = MenuUtil.Question.askYesNo("등록하시겠습니까?");
            if (choice) {
                // Controller를 통해 동물 생성
                Animal animal = animalController.createAnimal(
                    tempName, tempSpecies, tempAge, tempGender, tempHealthStatus);
                
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "동물 등록 성공!");
                animal.showAnimal();
            }
            
        } catch (InvalidAnimalDataException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "입력 오류: " + e.getMessage());
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록 실패: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 동물 이름 입력을 처리합니다.
     * Controller를 통해 중복 이름 체크를 수행합니다.
     * 
     * @param question 사용자에게 표시할 질문
     * @return 입력된 동물 이름
     */
    public String inputAnimalName(String question) {
        while (true) {
            String inName = MenuUtil.Question.askTextInput(question);
            try {
                List<Animal> findAnimal = animalController.findAnimalsByName(inName);
                if (findAnimal.isEmpty()) {
                    return inName;
                } else {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "동일한 이름이 있습니다.");
                    System.out.println();
                }
            } catch (TransactionException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "이름 확인 중 오류 발생: " + e.getMessage());
                System.out.println();
            }
        }
    }

	public String inputAnimalSpecies(String question) {
		String[] speciesChoices = { "Lion", "Tiger", "Bear", "Elephant", "Wolf", "Eagle", "Owl", "Snake" };
		int inSpecies = MenuUtil.Question.askSingleChoice(question, speciesChoices);
		switch (inSpecies) {
		case 1 -> {
			return "Lion";
		}
		case 2 -> {
			return "Tiger";
		}
		case 3 -> {
			return "Bear";
		}
		case 4 -> {
			return "Elephant";
		}
		case 5 -> {
			return "Wolf";
		}
		case 6 -> {
			return "Eagle";
		}
		case 7 -> {
			return "Owl";
		}
		case 8 -> {
			return "Snake";
		}
		default -> System.out.println();
		}
		return null;
	}

	public String inputAnimalGender(String question) {
		String[] genderChoices = { "Male", "Female" };
		int inGender = MenuUtil.Question.askSingleChoice(question, genderChoices);
		if (inGender == 1) {
			return "Male";
		} else if (inGender == 2) {
			return "Female";
		}
		return null;
	}

	public String inputAnimalHealth(String question) {
		String[] healthChoices = { "Good", "Fair", "Poor" };
		int inHealth = MenuUtil.Question.askSingleChoice(question, healthChoices);
		if (inHealth == 1) {
			return "Good";
		} else if (inHealth == 2) {
			return "Fair";
		} else if (inHealth == 3) {
			return "Poor";
		}
		return null;
	}

    /**
     * 동물 조회 메뉴를 처리합니다.
     */
    public void viewAnimals() {
        try {
            List<Animal> allAnimals = animalController.findAllAnimals();
            if (allAnimals.isEmpty()) {
                UIUtil.printSeparator('━');
                System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
                return;
            }
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 목록 조회 중 오류 발생: " + e.getMessage());
            return;
        }
        
        while (true) {
            displayViewMenu();
            int choice = InputUtil.getIntInput();
            switch (choice) {
            case 1 -> {
                UIUtil.printSeparator('━');
                viewAllAnimals();
            }
            case 2 -> {
                UIUtil.printSeparator('━');
                searchAnimalId("검색할 동물 ID를 입력하세요.");
            }
            case 3 -> {
                UIUtil.printSeparator('━');
                searchAnimalName("검색할 동물 이름을 입력하세요.");
            }
            case 4 -> {
                UIUtil.printSeparator('━');
                searchAnimalSpecies("검색할 동물의 종을 입력하세요.");
            }
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

    /**
     * 전체 동물 목록을 조회하고 테이블로 출력합니다.
     */
    public void viewAllAnimals() {
        try {
            String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureID" };
            List<Animal> allAnimals = animalController.findAllAnimals();
            String[][] data = new String[allAnimals.size()][];

            for (int i = 0; i < allAnimals.size(); i++) {
                Animal animal = allAnimals.get(i);
                data[i] = new String[] { 
                    animal.getId(), 
                    animal.getName(), 
                    animal.getSpecies(),
                    String.valueOf(animal.getAge()), 
                    animal.getGender(), 
                    animal.getHealthStatus(),
                    animal.getEnclosureId() 
                };
            }
            String title = String.format("동물 목록 (총 %d마리)", data.length);
            TableUtil.printTable(title, headers, data);
            
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 목록 조회 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 동물 ID로 검색합니다.
     */
    public void searchAnimalId(String question) {
        while (true) {
            String findId = MenuUtil.Question.askTextInput(question);
            try {
                Animal animal = animalController.findAnimalById(findId);
                System.out.print(MenuUtil.DEFAULT_PREFIX + "동물 정보");
                animal.showAnimal();
                return;
            } catch (AnimalNotFoundException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "해당 ID의 동물이 없습니다.");
                System.out.println();
            } catch (TransactionException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "검색 중 오류 발생: " + e.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * 동물 이름으로 검색합니다.
     */
    public void searchAnimalName(String question) {
        while (true) {
            String findName = MenuUtil.Question.askTextInput(question);
            try {
                List<Animal> findAnimal = animalController.findAnimalsByName(findName);

                if (findAnimal.isEmpty()) {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "해당 이름의 동물이 없습니다.");
                    System.out.println();
                } else {
                    System.out.print(MenuUtil.DEFAULT_PREFIX + "동물 정보");
                    findAnimal.forEach(a -> a.showAnimal());
                    return;
                }
            } catch (TransactionException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "검색 중 오류 발생: " + e.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * 동물 종으로 검색합니다.
     */
    public void searchAnimalSpecies(String question) {
        String[] speciesChoices = { "Lion", "Tiger", "Bear", "Elephant", "Wolf", "Eagle", "Owl", "Snake" };
        int inSpeciesNum = MenuUtil.Question.askSingleChoice(question, speciesChoices);
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

        try {
            String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureID" };
            List<Animal> findAnimals = animalController.findAnimalsBySpecies(inSpecies);
            if (findAnimals.isEmpty()) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "해당 종의 동물이 없습니다.");
                return;
            }
            String[][] data = new String[findAnimals.size()][];

            for (int i = 0; i < findAnimals.size(); i++) {
                Animal animal = findAnimals.get(i);
                data[i] = new String[] { 
                    animal.getId(), 
                    animal.getName(), 
                    animal.getSpecies(),
                    String.valueOf(animal.getAge()), 
                    animal.getGender(), 
                    animal.getHealthStatus(),
                    animal.getEnclosureId() 
                };
            }
            String title = String.format("동물 목록 (총 %d마리)", data.length);
            TableUtil.printTable(title, headers, data);
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "검색 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 동물 수정 메뉴를 처리합니다.
     */
    public void editAnimal() {
        UIUtil.printSeparator('━');
        TextArtUtil.printEditMenuTitle();
        UIUtil.printSeparator('━');

        try {
            List<Animal> allAnimals = animalController.findAllAnimals();
            if (allAnimals.isEmpty()) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
                return;
            }
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 목록 확인 중 오류 발생: " + e.getMessage());
            return;
        }

        // 수정할 ID로 검색
        String findId = "";
        Animal animal = null;
        while (true) {
            findId = MenuUtil.Question.askTextInput("수정할 동물 ID를 입력하세요.");
            try {
                animal = animalController.findAnimalById(findId);
                animal.showAnimal();
                break;
            } catch (AnimalNotFoundException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "ID를 다시 입력해 주세요.");
                System.out.println();
            } catch (TransactionException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "검색 중 오류 발생: " + e.getMessage());
                System.out.println();
            }
        }

        // 원하는 정보 선택
        String[] choices = { "Age", "HealthStatus" };
        int choice = MenuUtil.Question.askSingleChoice("수정할 정보를 입력하세요.", choices);

        // 정보 수정
        switch (choice) {
        case 1 -> editAnimalAge(findId);
        case 2 -> editAnimalHealth(findId);
        default -> System.out.println();
        }
    }

    /**
     * 동물의 나이를 수정합니다.
     */
    public void editAnimalAge(String animalId) {
        String changeAge = MenuUtil.Question.askTextInput("수정할 나이를 입력하세요.");
        try {
            Animal updatedAnimal = animalController.updateAnimalInfo(animalId, "age", changeAge);
            System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다.");
            updatedAnimal.showAnimal();
        } catch (AnimalNotFoundException | InvalidAnimalDataException | TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "수정 실패: " + e.getMessage());
        }
    }

    /**
     * 동물의 건강상태를 수정합니다.
     */
    public void editAnimalHealth(String animalId) {
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
        
        try {
            Animal updatedAnimal = animalController.updateAnimalInfo(animalId, "health", changeHealth);
            System.out.print(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다.");
            updatedAnimal.showAnimal();
        } catch (AnimalNotFoundException | InvalidAnimalDataException | TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "수정 실패: " + e.getMessage());
        }
    }

    /**
     * 동물 삭제 메뉴를 처리합니다.
     */
    public void removeAnimal() {
        UIUtil.printSeparator('━');
        TextArtUtil.printRemoveMenuTitle();
        UIUtil.printSeparator('━');

        try {
            List<Animal> allAnimals = animalController.findAllAnimals();
            if (allAnimals.isEmpty()) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 동물이 없습니다.");
                return;
            }
        } catch (TransactionException e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 목록 확인 중 오류 발생: " + e.getMessage());
            return;
        }
        
        String findId = "";
        Animal animal = null;
        while (true) {
            findId = MenuUtil.Question.askTextInput("삭제할 동물 ID를 입력하세요.");
            try {
                animal = animalController.findAnimalById(findId);
                animal.showAnimal();
                boolean choice = MenuUtil.Question.askYesNo("동물을 삭제하시겠습니까?");
                if (choice) {
                    animalController.deleteAnimal(findId);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "동물이 삭제되었습니다.");
                    return;
                }
            } catch (AnimalNotFoundException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "ID를 다시 입력하세요.");
                System.out.println();
            } catch (TransactionException e) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "작업 중 오류 발생: " + e.getMessage());
                System.out.println();
            }
        }
    }

    // =================================================================
    // EnclosureManager 연동을 위한 배치 관리 메서드들
    // =================================================================

    /**
     * 배치 가능한 동물이 있는지 확인합니다.
     * enclosureId가 null이거나 빈 문자열인 동물이 배치 가능한 동물로 간주됩니다.
     * 
     * @return 배치 가능한 동물 존재 여부
     */
    public boolean hasAvailableAnimals() {
        try {
            List<Animal> unassignedAnimals = animalController.findUnassignedAnimals();
            return !unassignedAnimals.isEmpty();
        } catch (TransactionException e) {
            return false;
        }
    }

    /**
     * Working Data Pattern: 배치 가능한 동물들의 작업용 복사본을 반환합니다.
     * 원본 데이터를 수정하지 않고 작업할 수 있도록 새로운 HashMap으로 복사합니다.
     * 
     * @return 배치 가능한 동물들의 복사본 Map
     */
    public Map<String, Animal> getWorkingCopyOfAvailableAnimals() {
        Map<String, Animal> availableAnimals = getAvailableAnimals();
        // 새로운 HashMap을 생성하여 복사본 반환
        return new HashMap<>(availableAnimals);
    }

    /**
     * 배치 가능한 동물들의 목록을 반환합니다.
     * enclosureId가 null이거나 빈 문자열인 동물들을 필터링하여 반환합니다.
     * 
     * @return 배치 가능한 동물들의 Map
     */
    public Map<String, Animal> getAvailableAnimals() {
        try {
            List<Animal> unassignedAnimals = animalController.findUnassignedAnimals();
            return unassignedAnimals.stream()
                    .collect(Collectors.toMap(Animal::getId, animal -> animal));
        } catch (TransactionException e) {
            return new HashMap<>();
        }
    }

    /**
     * 배치 가능한 동물들을 테이블 형태로 출력합니다.
     * EnclosureManager의 동물 입사 관리에서 사용됩니다.
     * TableUtil을 사용하여 일관된 형태의 표를 출력합니다.
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
            data[index] = new String[] { 
                animal.getId(), 
                animal.getName(), 
                animal.getSpecies(),
                String.valueOf(animal.getAge()), 
                animal.getGender(), 
                animal.getHealthStatus() 
            };
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
        try {
            Animal animal = animalController.findAnimalById(animalId);
            return Optional.of(animal);
        } catch (AnimalNotFoundException | TransactionException e) {
            return Optional.empty();
        }
    }

    /**
     * 특정 동물을 배치 가능한 상태에서 제거합니다.
     * 동물의 enclosureId를 설정하여 배치된 상태로 변경합니다.
     * 
     * @param animalId    배치할 동물 ID
     * @param enclosureId 배치할 사육장 ID
     * @return 배치된 동물 객체 (없으면 null)
     */
    public Animal removeAvailableAnimal(String animalId, String enclosureId) {
        try {
            // 배치 가능한 동물인지 확인하고 enclosure 설정
            List<Animal> unassignedAnimals = animalController.findUnassignedAnimals();
            boolean isAvailable = unassignedAnimals.stream()
                    .anyMatch(animal -> animal.getId().equals(animalId));
            
            if (isAvailable) {
                // 동물 조회
                Animal animal = animalController.findAnimalById(animalId);
                // enclosureId 설정 (직접 설정 후 업데이트)
                animal.setEnclosureId(enclosureId);
                // 업데이트는 별도의 repository 호출로 처리 (임시)
                // 향후 Controller에 updateEnclosureAssignment 메서드 추가 예정
                return animal;
            }
            return null;
        } catch (AnimalNotFoundException | TransactionException e) {
            return null;
        }
    }

    /**
     * 동물을 사육장에서 해제하여 다시 배치 가능한 상태로 만듭니다.
     * 동물의 enclosureId를 null로 설정합니다.
     * 
     * @param animalId 해제할 동물 ID
     * @return 해제된 동물 객체 (없으면 null)
     */
    public Animal releaseAnimalFromEnclosure(String animalId) {
        try {
            Animal animal = animalController.findAnimalById(animalId);
            animal.setEnclosureId(null);
            // 업데이트는 별도의 repository 호출로 처리 (임시)
            // 향후 Controller에 releaseFromEnclosure 메서드 추가 예정
            return animal;
        } catch (AnimalNotFoundException | TransactionException e) {
            return null;
        }
    }

    /**
     * 동물이 특정 사육장에 배치되어 있는지 확인합니다.
     * 
     * @param animalId    확인할 동물 ID
     * @param enclosureId 확인할 사육장 ID
     * @return 해당 사육장에 배치되어 있으면 true
     */
    public boolean isAnimalInEnclosure(String animalId, String enclosureId) {
        try {
            Animal animal = animalController.findAnimalById(animalId);
            String animalEnclosureId = animal.getEnclosureId();
            return enclosureId.equals(animalEnclosureId);
        } catch (AnimalNotFoundException | TransactionException e) {
            return false;
        }
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

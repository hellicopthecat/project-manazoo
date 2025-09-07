package app.enclosure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import app.animal.Animal;
import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.TableUtil;
import app.common.ui.UIUtil;
import app.animal.AnimalManager;
import app.repository.MemoryEnclosureRepository;
import app.repository.interfaces.EnclosureRepository;
import app.zooKeeper.ZooKeeper;
import app.zooKeeper.ZooKeeperManager;

/**
 * 동물원 사육장을 생성, 조회, 수정, 삭제 및 동물 배치 등을 전반적으로 관리하는 클래스입니다.
 * Repository 패턴을 적용하여 데이터 계층을 분리하고 타입 안전성을 확보했습니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사육장 등록 및 관리</li>
 *   <li>동물 입사 관리 (Working Data Pattern 적용)</li>
 *   <li>사육장 정보 조회 및 수정</li>
 *   <li>사육장별 거주 동물 현황 관리</li>
 * </ul>
 */
public class EnclosureManager {

    /**
     * 사육장 데이터를 관리하는 Repository입니다.
     * 메모리 기반 구현체를 사용하여 CRUD 연산을 수행합니다.
     */
    private final EnclosureRepository repository = new MemoryEnclosureRepository();

    /**
     * 사용자로부터 LocationType을 선택받는 헬퍼 메서드입니다.
     *
     * @return 선택된 LocationType
     */
    private LocationType selectLocationType() {
        String[] choices = {"실내", "야외"};
        int choice = MenuUtil.Question.askSingleChoice("사육장의 위치 타입을 입력하세요.", choices);

        return switch (choice) {
            case 1 -> LocationType.INDOOR;
            case 2 -> LocationType.OUTDOOR;
            default -> null; // 이 경우는 발생하지 않음 (askSingleChoice에서 검증됨)
        };
    }

    /**
     * 사용자로부터 EnvironmentType을 선택받는 헬퍼 메서드입니다.
     *
     * @return 선택된 EnvironmentType
     */
    private EnvironmentType selectEnvironmentType() {
        String[] choices = {"육상", "수상", "혼합"};
        int choice = MenuUtil.Question.askSingleChoice("사육장의 환경 타입을 입력하세요.", choices);

        return switch (choice) {
            case 1 -> EnvironmentType.LAND;
            case 2 -> EnvironmentType.AQUATIC;
            case 3 -> EnvironmentType.MIXED;
            default -> null; // 이 경우는 발생하지 않음 (askSingleChoice에서 검증됨)
        };
    }

    /**
     * 사육장 정보를 표 형식으로 출력합니다.
     * CMD 호환성을 위해 영문 헤더와 ASCII 문자로 변경했습니다.
     *
     * @param title     제목
     * @param enclosure 출력할 사육장 객체
     */
    private void printEnclosureInfo(String title, Enclosure enclosure) {
        String[] headers = {"Enclosure ID", "Name", "Size(m2)", "Temp(C)", "Location", "Environment", "Inhabitants", "Caretakers"};
        String[] values = {
                enclosure.getId(),
                enclosure.getName(),
                String.format("%.1f", enclosure.getAreaSize()),
                String.format("%.1f", enclosure.getTemperature()),
                enclosure.getLocationType().toString(),
                enclosure.getEnvironmentType().toString(),
                String.valueOf(enclosure.getInhabitantCount()),  // 거주 동물 수
                String.valueOf(enclosure.getCaretakerCount())    // 배정된 사육사 수
        };

        TableUtil.printSingleRowTable(title, headers, values);
    }

    public void handleEnclosureManagement() {

        while (true) {
            displayEnclosureMenu();
            int choice = InputUtil.getIntInput();
            switch (choice) {
                case 1 -> registerManagement();
                case 2 -> {
                    viewEnclosures();
                }
                case 3 -> {
                    editEnclosure();
                }
                case 4 -> {
                    removeEnclosure();
                }
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
                    UIUtil.printSeparator('━');
                    return;
                }
                default -> System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void displayEnclosureMenu() {
        String[] option = {"등록관리", "사육장조회", "사육장 정보 수정", "삭제"};
        String[] specialOptions = {"뒤로가기"};
        UIUtil.printSeparator('━');
        MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printEnclosureMenuTitle, option, specialOptions);
    }

    private void registerManagement() {

        while (true) {
            displayRegisterMenu();
            int choice = InputUtil.getIntInput();
            switch (choice) {
                case 1 -> registerEnclosure();
                case 2 -> manageAnimalAdmission();
                case 3 -> manageKeeperAssignment();
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void displayRegisterMenu() {
        String[] option = {"사육장등록", "동물 입사 관리", "사육사 배치 관리"};
        String[] specialOptions = {"뒤로가기"};
        UIUtil.printSeparator('━');
        MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printRegisterMenuTitle, option, specialOptions);
    }

    /**
     * 새로운 사육장을 등록합니다.
     * 사용자로부터 사육장 정보를 입력받아 시스템에 저장하고 결과를 출력합니다.
     * 
     * <p>입력받는 정보:</p>
     * <ul>
     *   <li>사육장 이름</li>
     *   <li>사육장 크기 (㎡)</li>
     *   <li>사육장 온도 (°C)</li>
     *   <li>위치 타입 (실내/야외)</li>
     *   <li>환경 타입 (육상/수상/혼합)</li>
     * </ul>
     */
    private void registerEnclosure() {
        UIUtil.printSeparator('━');
        String id = IdGeneratorUtil.generateId();
        String name = MenuUtil.Question.askTextInput("사육장의 이름을 입력하세요.");
        float areaSize = MenuUtil.Question.askNumberInput("사육장의 크기를 입력하세요", "m2");
        float temperature = MenuUtil.Question.askNumberInput("사육장의 온도를 입력하세요", "C");

        LocationType locationType = selectLocationType();
        EnvironmentType environmentType = selectEnvironmentType();

        Enclosure newEnclosure = new Enclosure(id, name, areaSize, temperature, locationType, environmentType);
        
        repository.save(newEnclosure);
        
        printEnclosureInfo("사육장이 등록되었습니다. 등록된 사육장의 정보는 아래와 같습니다.", newEnclosure);
    }

    /**
     * 사육장 조회 메뉴를 표시하고 처리합니다.
     * 모든 사육장 조회와 특정 사육장 지정 조회 옵션을 제공합니다.
     */
    private void viewEnclosures() {
        while (true) {
            displayViewEnclosuresMenu();
            int choice = InputUtil.getIntInput();
            
            switch (choice) {
                case 1 -> viewAllEnclosures();
                case 2 -> viewSpecificEnclosure();
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    /**
     * 사육장 조회 메뉴를 표시합니다.
     */
    private static void displayViewEnclosuresMenu() {
        String[] options = {"모든 사육장 조회", "사육장 지정 조회"};
        String[] specialOptions = {"뒤로가기"};
        UIUtil.printSeparator('━');
        MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printViewMenuTitle, options, specialOptions);
    }

    /**
     * 모든 사육장을 테이블 형태로 조회합니다.
     * 사육장의 기본 정보와 함께 거주 동물 수, 배정된 사육사 수를 표시합니다.
     * 
     * @see TableUtil#printTable(String, String[], String[][]) 테이블 출력 유틸리티
     */
    private void viewAllEnclosures() {
        if (repository.count() == 0) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 사육장이 없습니다.");
            return;
        }

        String[] headers = {"Enclosure ID", "Name", "Size(m2)", "Temp(C)", "Location", "Environment", "Inhabitants", "Caretakers"};

        List<Enclosure> allEnclosures = repository.findAll();
        String[][] data = new String[allEnclosures.size()][];

        for (int i = 0; i < allEnclosures.size(); i++) {
            Enclosure enclosure = allEnclosures.get(i);
            
            int inhabitantCount = enclosure.getInhabitantCount();
            int caretakerCount = enclosure.getCaretakerCount();
            
            data[i] = new String[]{
                    enclosure.getId(),
                    enclosure.getName(),
                    String.format("%.1f", enclosure.getAreaSize()),
                    String.format("%.1f", enclosure.getTemperature()),
                    enclosure.getLocationType().toString(),
                    enclosure.getEnvironmentType().toString(),
                    String.valueOf(inhabitantCount),
                    String.valueOf(caretakerCount)
            };
        }

        String title = String.format("사육장 목록 (총 %d개)", repository.count());
        TableUtil.printTable(title, headers, data);
    }

    /**
     * 지정된 사육장의 상세 정보를 조회합니다.
     * 사육장 기본 정보, 거주 동물 목록, 배정된 사육사 목록을 모두 표시합니다.
     */
    private void viewSpecificEnclosure() {
        if (repository.count() == 0) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 사육장이 없습니다.");
            return;
        }
        
        // 먼저 전체 사육장 목록을 간단히 표시
        System.out.println("등록된 사육장 목록:");
        viewAllEnclosures();
        
        System.out.println();
        String enclosureId = MenuUtil.Question.askTextInput("조회할 사육장 ID를 입력하세요");
        
        Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
        
        if (foundEnclosure.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "입력하신 ID '" + enclosureId + "'의 사육장이 존재하지 않습니다.");
            System.out.println(MenuUtil.DEFAULT_PREFIX + "위의 목록에서 올바른 사육장 ID를 확인해주세요.");
            return;
        }
        
        Enclosure enclosure = foundEnclosure.get();
        
        // 1. 사육장 기본 정보 표시
        printEnclosureInfo("사육장 기본 정보", enclosure);
        
        System.out.println();
        
        // 2. 거주 동물 상세 목록 표시
        displayEnclosureInhabitants(enclosure);
        
        System.out.println();
        
        // 3. 배정된 사육사 상세 목록 표시
        displayEnclosureCaretakers(enclosure);
    }

    /**
     * 사육장에 거주하는 동물들의 상세 목록을 테이블 형태로 표시합니다.
     * 
     * <p>표시 정보:</p>
     * <ul>
     *   <li>동물 ID</li>
     *   <li>동물 이름</li>
     *   <li>종류</li>
     *   <li>나이</li>
     *   <li>입사일</li>
     * </ul>
     * 
     * @param enclosure 조회할 사육장 객체
     */
    private void displayEnclosureInhabitants(Enclosure enclosure) {
        Map<String, Object> inhabitants = enclosure.getAllInhabitants();
        
        if (inhabitants.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "이 사육장에는 현재 거주하는 동물이 없습니다.");
            return;
        }
        
        String[] headers = {"Animal ID", "Name", "Species", "Age", "Admission Date"};
        String[][] data = new String[inhabitants.size()][];
        int index = 0;
        
        String currentDate = java.time.LocalDate.now().toString();
        
        for (Map.Entry<String, Object> entry : inhabitants.entrySet()) {
            String animalId = entry.getKey();
            Object animalObj = entry.getValue();
            
            if (animalObj instanceof Animal) {
                Animal animal = (Animal) animalObj;
                data[index] = new String[]{
                    truncateString(animalId, 15),
                    truncateString(animal.getName(), 15),
                    truncateString(animal.getSpecies(), 15),
                    truncateString(String.valueOf(animal.getAge()), 15),
                    truncateString(currentDate, 15)
                };
            } else {
                data[index] = new String[]{
                    truncateString(animalId, 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString("0", 15),
                    truncateString(currentDate, 15)
                };
            }
            index++;
        }
        
        String title = String.format("%s (%s) 거주 동물 목록", 
                      truncateString(enclosure.getName(), 15), enclosure.getId());
        
        TableUtil.printTable(title, headers, data);
    }

    /**
     * 사육장에 배정된 사육사들의 상세 목록을 테이블 형태로 표시합니다.
     * 
     * <p>표시 정보:</p>
     * <ul>
     *   <li>사육사 ID</li>
     *   <li>사육사 이름</li>
     *   <li>부서</li>
     *   <li>직급</li>
     *   <li>위험동물 처리 가능 여부</li>
     *   <li>배정일</li>
     * </ul>
     * 
     * @param enclosure 조회할 사육장 객체
     */
    private void displayEnclosureCaretakers(Enclosure enclosure) {
        Map<String, Object> caretakers = enclosure.getAllCaretakers();
        
        if (caretakers.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "이 사육장에는 현재 배정된 사육사가 없습니다.");
            return;
        }
        
        String[] headers = {"Keeper ID", "Name", "Department", "Rank", "Danger Animal", "Assignment Date"};
        String[][] data = new String[caretakers.size()][];
        int index = 0;
        
        String currentDate = java.time.LocalDate.now().toString();
        
        for (Map.Entry<String, Object> entry : caretakers.entrySet()) {
            String keeperId = entry.getKey();
            Object keeperObj = entry.getValue();
            
            if (keeperObj instanceof ZooKeeper) {
                ZooKeeper keeper = (ZooKeeper) keeperObj;
                data[index] = new String[]{
                    truncateString(keeperId, 15),
                    truncateString(keeper.getName(), 15),
                    truncateString(keeper.getDepartment().toString(), 15),
                    truncateString(keeper.getRank().toString(), 15),
                    keeper.isCanHandleDangerAnimal() ? "가능" : "불가능",
                    truncateString(currentDate, 15)
                };
            } else {
                data[index] = new String[]{
                    truncateString(keeperId, 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString(currentDate, 15)
                };
            }
            index++;
        }
        
        String title = String.format("%s (%s) 배정된 사육사 목록", 
                      truncateString(enclosure.getName(), 15), enclosure.getId());
        
        TableUtil.printTable(title, headers, data);
    }

    /**
     * 사육장 이름을 수정하는 헬퍼 메서드입니다.
     *
     * @param enclosure 수정할 사육장 객체
     */
    private void editName(Enclosure enclosure) {
        String newName = MenuUtil.Question.askTextInput(MenuUtil.DEFAULT_PREFIX + "사육장 이름을 입력하세요.");
        enclosure.setName(newName);
        System.out.println("사육장 이름이 '" + newName + "'으로 수정되었습니다.");
    }

    /**
     * 사육장 크기를 수정하는 헬퍼 메서드입니다.
     *
     * @param enclosure 수정할 사육장 객체
     */
    private void editAreaSize(Enclosure enclosure) {
        float newAreaSize = MenuUtil.Question.askNumberInput("사육장 크기를 입력하세요", "m2");
        enclosure.setAreaSize(newAreaSize);
        System.out.println("사육장 크기가 " + newAreaSize + "m2로 수정되었습니다.");
    }

    /**
     * 사육장 온도를 수정하는 헬퍼 메서드입니다.
     *
     * @param enclosure 수정할 사육장 객체
     */
    private void editTemperature(Enclosure enclosure) {
        float newTemperature = MenuUtil.Question.askNumberInput("사육장 온도를 입력하세요", "C");
        enclosure.setTemperature(newTemperature);
        System.out.println("사육장 온도가 " + newTemperature + "C로 수정되었습니다.");
    }

    /**
     * 사육장 위치 타입을 수정하는 헬퍼 메서드입니다.
     *
     * @param enclosure 수정할 사육장 객체
     */
    private void editLocationType(Enclosure enclosure) {
        LocationType newLocationType = selectLocationType();
        enclosure.setLocationType(newLocationType);
        System.out.println("위치 타입이 " + newLocationType + "으로 수정되었습니다.");
    }

    /**
     * 사육장 환경 타입을 수정하는 헬퍼 메서드입니다.
     *
     * @param enclosure 수정할 사육장 객체
     */
    private void editEnvironmentType(Enclosure enclosure) {
        EnvironmentType newEnvironmentType = selectEnvironmentType();
        enclosure.setEnvironmentType(newEnvironmentType);
        System.out.println("환경 타입이 " + newEnvironmentType + "으로 수정되었습니다.");
    }

    /**
     * 사육장 정보를 수정합니다.
     * 각 편집 작업 후 즉시 Repository에 저장하여 데이터 안전성을 확보합니다.
     * 
     * <p>수정 가능한 항목:</p>
     * <ul>
     *   <li>사육장 이름</li>
     *   <li>사육장 크기</li>
     *   <li>사육장 온도</li>
     *   <li>위치 타입</li>
     *   <li>환경 타입</li>
     * </ul>
     */
    private void editEnclosure() {
        viewEnclosures();

        if (repository.count() == 0) {
            return;
        }
        
        String enclosureId = MenuUtil.Question.askTextInput("수정할 사육장 번호를 입력하세요.");

        Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
        
        if (foundEnclosure.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "입력하신 아이디의 사육장이 없습니다.");
            return;
        }
        
        Enclosure enclosure = foundEnclosure.get();
        printEnclosureInfo("현재 사육장 정보", enclosure);
        UIUtil.printSeparator('━');

        while (true) {
            String[] editOptions = {"이름 수정", "크기 수정", "온도 수정", "위치타입 수정", "환경타입 수정"};
            String[] specialOptions = {"수정완료"};
            MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printRegisterMenuTitle, editOptions, specialOptions);

            int choice = InputUtil.getIntInput();
            switch (choice) {
                case 1 -> {
                    editName(enclosure);
                    repository.update(enclosure);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "변경사항이 저장되었습니다.");
                }
                case 2 -> {
                    editAreaSize(enclosure);
                    repository.update(enclosure);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "변경사항이 저장되었습니다.");
                }
                case 3 -> {
                    editTemperature(enclosure);
                    repository.update(enclosure);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "변경사항이 저장되었습니다.");
                }
                case 4 -> {
                    editLocationType(enclosure);
                    repository.update(enclosure);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "변경사항이 저장되었습니다.");
                }
                case 5 -> {
                    editEnvironmentType(enclosure);
                    repository.update(enclosure);
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "변경사항이 저장되었습니다.");
                }
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다!");
                    printEnclosureInfo("최종 사육장 정보", enclosure);
                    return;
                }
                default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    private void removeEnclosure() {
        viewEnclosures();

        if (repository.count() == 0) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "삭제할 수 있는 사육장이 없습니다.");
        } else {
            String enclosureId = MenuUtil.Question.askTextInput("삭제할 사육장 번호를 입력하세요.");

            Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
            if (foundEnclosure.isPresent()) {
                Enclosure enclosure = foundEnclosure.get();

                // 삭제 전 확인
                printEnclosureInfo("삭제할 사육장 정보", enclosure);

                boolean confirmed = MenuUtil.Question.askSimpleConfirm("정말로 이 사육장을 삭제하시겠습니까?");

                if (confirmed) {
                    boolean deleted = repository.deleteById(enclosureId);
                    if (deleted) {
                        System.out.println("사육장 '" + enclosure.getName() + "' [" + enclosureId + "]이(가) 성공적으로 삭제되었습니다.");
                    } else {
                        System.out.println(MenuUtil.DEFAULT_PREFIX + "삭제 중 오류가 발생했습니다.");
                    }
                } else {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "삭제가 취소되었습니다.");
                }
            } else {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "입력하신 ID '" + enclosureId + "'의 사육장이 존재하지 않습니다.");
                System.out.println(MenuUtil.DEFAULT_PREFIX + "위의 목록에서 올바른 사육장 ID를 확인해주세요.");
            }
        }
    }

    /**
     * 동물 입사 관리 기능을 처리합니다.
     * Working Data Pattern을 사용하여 AnimalManager의 원본 데이터를 보호합니다.
     * 
     * 처리 과정:
     * 1. 사전 조건 확인 (사육장과 동물 존재 여부)
     * 2. Working Data 생성 (AnimalManager에서 복사본 획득)
     * 3. 사용자 인터페이스 처리 (목록 표시, 선택받기)
     * 4. 입사 처리 시뮬레이션 (working data로 먼저 검증)
     * 5. 실제 데이터 업데이트 (검증 완료 후 원본 수정)
     * 6. 결과 표시
     */
    private void manageAnimalAdmission() {
        UIUtil.printSeparator('━');
        System.out.println("동물 입사 관리");
        UIUtil.printSeparator('━');
        
        // 1. 사전 조건 확인
        if (!hasRequiredDataForAdmission()) {
            return;
        }
        
        // 2. Working Data 패턴: AnimalManager로부터 작업용 복사본 획득
        Map<String, Animal> workingAnimals = AnimalManager.getInstance().getWorkingCopyOfAvailableAnimals();
        
        if (workingAnimals.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "현재 배치 가능한 동물이 없습니다.");
            return;
        }
        
        // 3. 현재 상황 표시
        displayDataForAdmissionWithWorkingData(workingAnimals);
        
        // 4. 사육장 선택
        String enclosureId = selectEnclosureWithRetry();
        if (enclosureId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        // 5. 동물 선택 (working data 기준)
        String animalId = selectAnimalWithRetryFromWorkingData(workingAnimals);
        if (animalId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        // 6. 입사 처리 시뮬레이션 (working data로 사전 검증)
        if (!simulateAnimalAdmission(enclosureId, animalId, workingAnimals)) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사 처리 중 오류가 발생했습니다.");
            return;
        }
        
        // 7. 실제 데이터 업데이트 (시뮬레이션 성공 후 실행)
        boolean success = executeAnimalAdmission(enclosureId, animalId);
        
        if (success) {
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사가 성공적으로 처리되었습니다!");
            displayAdmissionResult(enclosureId);
        } else {
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 동물 입사를 위한 필수 데이터가 있는지 확인합니다.
     * 사육장과 배치 가능한 동물이 모두 있어야 동물 입사가 가능합니다.
     * 
     * @return 필수 데이터 존재 여부 (true: 실행 가능, false: 실행 불가능)
     */
    private boolean hasRequiredDataForAdmission() {
        // 사육장 존재 여부 확인
        if (repository.count() == 0) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 사육장이 없습니다.");
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사를 위해서는 먼저 사육장을 등록해주세요.");
            return false;
        }
        
        // AnimalManager의 배치 가능한 동물 존재 여부 확인
        boolean hasAnimals = AnimalManager.getInstance().hasAvailableAnimals();
        
        if (!hasAnimals) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "배치 가능한 동물이 없습니다.");
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사를 위해서는 먼저 동물을 등록해주세요.");
            return false;
        }
        
        return true; // 둘 다 있으면 실행 가능
    }

    /**
     * Working Data Pattern: 작업용 동물 데이터와 함께 현재 상황을 표시합니다.
     * 
     * @param workingAnimals 작업용 동물 데이터 (AnimalManager로부터 복사된 데이터)
     */
    private void displayDataForAdmissionWithWorkingData(Map<String, Animal> workingAnimals) {
        System.out.println();
        System.out.println("사용 가능한 사육장 목록:");
        viewAllEnclosures(); // 메뉴 없이 직접 전체 사육장 목록 표시
        
        System.out.println();
        System.out.println("배치 가능한 동물 목록:");
        displayWorkingAnimalsTable(workingAnimals);
    }
    
    /**
     * Working Data Pattern에서 사용되는 작업용 동물 데이터를 테이블 형태로 출력합니다.
     * 
     * <p>표시 정보:</p>
     * <ul>
     *   <li>동물 ID</li>
     *   <li>동물 이름</li>
     *   <li>종류</li>
     *   <li>나이</li>
     *   <li>상태 (대기중)</li>
     * </ul>
     * 
     * @param workingAnimals 작업용 동물 데이터 맵
     */
    private void displayWorkingAnimalsTable(Map<String, Animal> workingAnimals) {
        if (workingAnimals.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "배치 가능한 동물이 없습니다.");
            return;
        }
        
        String[] headers = {"Animal ID", "Name", "Species", "Age", "Status"};
        String[][] data = new String[workingAnimals.size()][];
        int index = 0;
        
        for (Map.Entry<String, Animal> entry : workingAnimals.entrySet()) {
            String animalId = entry.getKey();
            Animal animal = entry.getValue();
            
            data[index] = new String[]{
                animalId,
                animal.getName(),
                animal.getSpecies(),
                String.valueOf(animal.getAge()),
                "대기중"
            };
            index++;
        }
        
        String title = String.format("배치 가능한 동물 목록 (총 %d마리)", workingAnimals.size());
        TableUtil.printTable(title, headers, data);
    }
    
    /**
     * Working Data Pattern에서 사용자로부터 동물을 선택받습니다.
     * 최대 3회까지 재시도 가능하며, 각 시도마다 사용자 확인을 받습니다.
     * 
     * @param workingAnimals 작업용 동물 데이터 맵
     * @return 선택된 동물 ID, 3회 모두 실패 시 null 반환
     */
    private String selectAnimalWithRetryFromWorkingData(Map<String, Animal> workingAnimals) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.printf("\n[%d/3] 입사시킬 동물 ID를 입력하세요: ", attempt);
            String animalId = MenuUtil.Question.askTextInput("");
            
            // Working data에서 동물 검색
            if (workingAnimals.containsKey(animalId)) {
                Animal animal = workingAnimals.get(animalId);
                System.out.printf("선택된 동물: %s (%s - %s)\n", 
                                animal.getName(), animalId, animal.getSpecies());
                
                // 사용자 확인 받기
                boolean confirmed = MenuUtil.Question.askSimpleConfirm("이 동물이 맞습니까?");
                if (confirmed) {
                    return animalId; // 정상 선택 완료
                } else {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 선택을 다시 진행합니다.");
                    continue; // 다음 시도로
                }
            } else {
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "배치 가능한 동물 목록에 없는 ID입니다: %s\n", animalId);
            }
            
            // 아직 시도 기회가 남았다면 안내 메시지
            if (attempt < 3) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "다시 시도해주세요.");
            }
        }
        return null; // 3회 모두 실패
    }
    
    /**
     * 동물 입사 처리를 시뮬레이션합니다.
     * Working Data Pattern을 사용하여 실제 데이터를 수정하기 전에 작업의 유효성을 검증합니다.
     * 
     * @param enclosureId    대상 사육장 ID
     * @param animalId       대상 동물 ID
     * @param workingAnimals 작업용 동물 데이터 맵
     * @return 시뮬레이션 성공 여부
     */
    private boolean simulateAnimalAdmission(String enclosureId, String animalId, Map<String, Animal> workingAnimals) {
        // 1. 사육장 존재 확인
        Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
        if (enclosureOpt.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육장을 찾을 수 없습니다: " + enclosureId);
            return false;
        }
        
        // 2. Working data에서 동물 존재 확인
        if (!workingAnimals.containsKey(animalId)) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "배치 가능한 동물 목록에서 찾을 수 없습니다: " + animalId);
            return false;
        }
        
        // 3. 시뮬레이션: working data에서 동물을 제거해보기
        Animal animal = workingAnimals.remove(animalId);
        if (animal == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "시뮬레이션 중 오류가 발생했습니다.");
            return false;
        }
        
        // 4. 시뮬레이션 성공 - 로그 출력
        System.out.println(String.format("시뮬레이션 성공: %s를 %s 사육장에 배치할 수 있습니다.", 
                                        animal.getName(), enclosureOpt.get().getName()));
        
        return true;
    }
    
    /**
     * 실제 동물 입사 데이터를 업데이트합니다.
     * Working Data Pattern에서 시뮬레이션이 성공한 후에만 호출되어야 합니다.
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>사육장 객체 조회</li>
     *   <li>AnimalManager에서 동물 이동 처리</li>
     *   <li>사육장에 동물 추가</li>
     * </ol>
     * 
     * @param enclosureId 대상 사육장 ID
     * @param animalId    대상 동물 ID
     * @return 실제 처리 성공 여부
     */
    private boolean executeAnimalAdmission(String enclosureId, String animalId) {
        try {
            Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
            if (enclosureOpt.isEmpty()) {
                return false;
            }
            Enclosure enclosure = enclosureOpt.get();
            
            Animal animal = AnimalManager.getInstance().removeAvailableAnimal(animalId, enclosureId);
            if (animal == null) {
                return false;
            }
            
            enclosure.addInhabitant(animalId, animal);
            
            return true;
        } catch (Exception e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "처리 중 예외가 발생했습니다: " + e.getMessage());
            return false;
        }
    }

    /**
     * 사육장을 선택받습니다. 잘못된 입력 시 최대 3회까지 재시도할 수 있습니다.
     * 각 시도마다 사용자에게 확인을 받아 정확한 선택을 보장합니다.
     * 
     * @return 선택된 사육장 ID (3회 실패 시 null 반환)
     */
    private String selectEnclosureWithRetry() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.printf("\n[%d/3] 입사시킬 사육장 ID를 입력하세요: ", attempt);
            String enclosureId = MenuUtil.Question.askTextInput("");
            
            // 입력된 ID로 사육장 검색
            Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
            if (foundEnclosure.isPresent()) {
                Enclosure enclosure = foundEnclosure.get();
                System.out.printf("선택된 사육장: %s (%s)\n", enclosure.getName(), enclosureId);
                
                // 사용자 확인 받기
                boolean confirmed = MenuUtil.Question.askSimpleConfirm("이 사육장이 맞습니까?");
                if (confirmed) {
                    return enclosureId; // 정상 선택 완료
                } else {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "사육장 선택을 다시 진행합니다.");
                    continue; // 다음 시도로
                }
            } else {
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "존재하지 않는 사육장 ID입니다: %s\n", enclosureId);
            }
            
            // 아직 시도 기회가 남았다면 안내 메시지
            if (attempt < 3) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "다시 시도해주세요.");
            }
        }
        return null; // 3회 모두 실패
    }

    /**
     * 동물 입사 처리 결과를 테이블 형태로 출력합니다.
     * 처리된 사육장의 현재 동물 현황을 보여줍니다.
     * 
     * <p>표시 항목:</p>
     * <ul>
     *   <li>Animal ID (최대 15글자)</li>
     *   <li>Name (최대 15글자)</li>
     *   <li>Species (최대 15글자)</li>
     *   <li>Age (최대 15글자)</li>
     *   <li>Admission Date (최대 15글자)</li>
     * </ul>
     * 
     * @param enclosureId 입사가 처리된 사육장 ID
     */
    private void displayAdmissionResult(String enclosureId) {
        Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
        Enclosure enclosure = enclosureOpt.get();
        
        Map<String, Object> inhabitants = enclosure.getAllInhabitants();
        
        if (inhabitants.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육장에 동물이 없습니다.");
            return;
        }
        
        String[] headers = {"Animal ID", "Name", "Species", "Age", "Admission Date"};
        String[][] data = new String[inhabitants.size()][];
        int index = 0;
        
        String currentDate = java.time.LocalDate.now().toString();
        
        for (Map.Entry<String, Object> entry : inhabitants.entrySet()) {
            String animalId = entry.getKey();
            Object animalObj = entry.getValue();
            
            if (animalObj instanceof Animal) {
                Animal animal = (Animal) animalObj;
                data[index] = new String[]{
                    truncateString(animalId, 15),
                    truncateString(animal.getName(), 15),
                    truncateString(animal.getSpecies(), 15),
                    truncateString(String.valueOf(animal.getAge()), 15),
                    truncateString(currentDate, 15)
                };
            } else {
                data[index] = new String[]{
                    truncateString(animalId, 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString("0", 15),
                    truncateString(currentDate, 15)
                };
            }
            index++;
        }
        
        String title = String.format("%s (%s) 현재 거주 동물 현황", 
                      truncateString(enclosure.getName(), 15), enclosureId);
        
        TableUtil.printTable(title, headers, data);
    }

    /**
     * 문자열을 지정된 최대 길이로 자르는 유틸리티 메서드입니다.
     * CMD 환경에서의 테이블 표시 호환성을 위해 문자열 길이를 제한합니다.
     * 
     * @param str       원본 문자열
     * @param maxLength 최대 허용 길이
     * @return 잘린 문자열, null인 경우 빈 문자열 반환
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    // =================================================================
    // 사육사 배치 관리 기능
    // =================================================================

    /**
     * 사육사 배치 관리 기능을 처리합니다.
     * 사육사는 여러 사육장을 담당할 수 있으므로 직접적인 참조 추가 방식을 사용합니다.
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>사전 조건 확인 (사육장과 사육사 존재 여부)</li>
     *   <li>재직 중인 사육사 목록 조회</li>
     *   <li>사용자 인터페이스 처리 (목록 표시, 선택받기)</li>
     *   <li>배치 처리 (사육장에 사육사 참조 추가)</li>
     *   <li>결과 표시</li>
     * </ol>
     */
    private void manageKeeperAssignment() {
        UIUtil.printSeparator('━');
        System.out.println("사육사 배치 관리");
        UIUtil.printSeparator('━');
        
        if (!hasRequiredDataForKeeperAssignment()) {
            return;
        }
        
        List<ZooKeeper> availableKeepers = ZooKeeperManager.getInstance()
                                                          .getWorkingKeepers();
        
        if (availableKeepers.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "현재 배치 가능한 사육사가 없습니다.");
            return;
        }
        
        displayDataForKeeperAssignment(availableKeepers);
        
        String enclosureId = selectEnclosureWithRetry();
        if (enclosureId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        String keeperId = selectKeeperFromList(availableKeepers);
        if (keeperId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        boolean success = executeKeeperAssignment(enclosureId, keeperId);
        
        if (success) {
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사 배치가 성공적으로 처리되었습니다!");
            displayKeeperAssignmentResult(enclosureId);
        } else {
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사 배치 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사육사 배치를 위한 필수 데이터가 있는지 확인합니다.
     * 사육장과 재직 중인 사육사가 모두 있어야 배치가 가능합니다.
     * 
     * @return 필수 데이터 존재 여부 (true: 실행 가능, false: 실행 불가능)
     */
    private boolean hasRequiredDataForKeeperAssignment() {
        if (repository.count() == 0) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 사육장이 없습니다.");
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사 배치를 위해서는 먼저 사육장을 등록해주세요.");
            return false;
        }
        
        boolean hasWorkingKeepers = ZooKeeperManager.getInstance()
                                                   .hasWorkingKeepers();
        
        if (!hasWorkingKeepers) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "배치 가능한 사육사가 없습니다.");
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사 배치를 위해서는 먼저 재직 중인 사육사가 필요합니다.");
            return false;
        }
        
        return true;
    }

    /**
     * 사육사 배치를 위한 현재 상황을 표시합니다.
     * 
     * @param availableKeepers 재직 중인 사육사 목록
     */
    private void displayDataForKeeperAssignment(List<ZooKeeper> availableKeepers) {
        System.out.println();
        System.out.println("사용 가능한 사육장 목록:");
        viewAllEnclosures();
        
        System.out.println();
        System.out.println("배치 가능한 사육사 목록:");
        displayWorkingKeepersTable(availableKeepers);
    }

    /**
     * 재직 중인 사육사 목록을 테이블 형태로 출력합니다.
     * 
     * <p>표시 정보:</p>
     * <ul>
     *   <li>사육사 ID</li>
     *   <li>사육사 이름</li>
     *   <li>부서</li>
     *   <li>직급</li>
     *   <li>위험동물 처리 가능 여부</li>
     * </ul>
     * 
     * @param availableKeepers 재직 중인 사육사 목록
     */
    private void displayWorkingKeepersTable(List<ZooKeeper> availableKeepers) {
        if (availableKeepers.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "배치 가능한 사육사가 없습니다.");
            return;
        }
        
        String[] headers = {"Keeper ID", "Name", "Department", "Rank", "Danger Animal"};
        String[][] data = new String[availableKeepers.size()][];
        
        for (int i = 0; i < availableKeepers.size(); i++) {
            ZooKeeper keeper = availableKeepers.get(i);
            String keeperId = findKeeperIdFromManager(keeper);
            
            data[i] = new String[]{
                truncateString(keeperId, 15),
                truncateString(keeper.getName(), 15),
                truncateString(keeper.getDepartment().toString(), 15),
                truncateString(keeper.getRank().toString(), 15),
                keeper.isCanHandleDangerAnimal() ? "가능" : "불가능"
            };
        }
        
        String title = String.format("배치 가능한 사육사 목록 (총 %d명)", availableKeepers.size());
        TableUtil.printTable(title, headers, data);
    }

    /**
     * ZooKeeper 객체로부터 해당하는 ID를 찾습니다.
     * 
     * @param keeper ID를 찾을 ZooKeeper 객체
     * @return 찾은 사육사 ID, 없으면 "Unknown"
     */
    private String findKeeperIdFromManager(ZooKeeper keeper) {
        try {
            // ZooKeeperManager의 전체 목록에서 해당 객체와 일치하는 ID 찾기
            List<ZooKeeper> allKeepers = ZooKeeperManager.getInstance()
                                                        .getRepository()
                                                        .getZooKeeperList();
            
            for (ZooKeeper k : allKeepers) {
                if (k == keeper || (k.getName().equals(keeper.getName()) && 
                                   k.getDepartment() == keeper.getDepartment())) {
                    // toString()에서 ID 추출
                    String toString = k.toString();
                    if (toString.contains("id : ")) {
                        int start = toString.indexOf("id : ") + 5;
                        int end = toString.indexOf(" |", start);
                        if (end > start) {
                            return toString.substring(start, end);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 오류 발생 시 기본값 반환
        }
        return "Unknown";
    }

    /**
     * 사육사 목록에서 사용자로부터 사육사를 선택받습니다.
     * 최대 3회까지 재시도 가능하며, 각 시도마다 사용자 확인을 받습니다.
     * 
     * @param availableKeepers 재직 중인 사육사 목록
     * @return 선택된 사육사 ID, 3회 모두 실패 시 null 반환
     */
    private String selectKeeperFromList(List<ZooKeeper> availableKeepers) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.printf("\n[%d/3] 배치할 사육사 ID를 입력하세요: ", attempt);
            String keeperId = MenuUtil.Question.askTextInput("");
            
            // 입력받은 ID로 사육사 검색
            ZooKeeper foundKeeper = null;
            for (ZooKeeper keeper : availableKeepers) {
                String currentId = findKeeperIdFromManager(keeper);
                if (currentId.equals(keeperId)) {
                    foundKeeper = keeper;
                    break;
                }
            }
            
            if (foundKeeper != null) {
                System.out.printf("선택된 사육사: %s (%s - %s %s)\n", 
                                foundKeeper.getName(), keeperId, 
                                foundKeeper.getDepartment(), foundKeeper.getRank());
                
                boolean confirmed = MenuUtil.Question.askSimpleConfirm("이 사육사가 맞습니까?");
                if (confirmed) {
                    return keeperId;
                } else {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사 선택을 다시 진행합니다.");
                    continue;
                }
            } else {
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "재직 중인 사육사 목록에 없는 ID입니다: %s\n", keeperId);
            }
            
            if (attempt < 3) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "다시 시도해주세요.");
            }
        }
        return null;
    }

    /**
     * 실제 사육사 배정 데이터를 업데이트합니다.
     * 
     * <p>처리 과정:</p>
     * <ol>
     *   <li>사육장 객체 조회</li>
     *   <li>ZooKeeperManager에서 사육사 정보 조회</li>
     *   <li>중복 배정 확인 및 사용자 의사 확인</li>
     *   <li>사육장에 사육사 배정 (이동 없이 참조 추가만)</li>
     * </ol>
     * 
     * @param enclosureId 대상 사육장 ID
     * @param keeperId    대상 사육사 ID
     * @return 실제 처리 성공 여부
     */
    private boolean executeKeeperAssignment(String enclosureId, String keeperId) {
        try {
            Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
            if (enclosureOpt.isEmpty()) {
                return false;
            }
            Enclosure enclosure = enclosureOpt.get();
            
            ZooKeeper keeper = ZooKeeperManager.getInstance()
                                              .getRepository()
                                              .getZooKeeperById(keeperId);
            if (keeper == null) {
                return false;
            }
            
            // 중복 배정 확인
            if (enclosure.hasCaretaker(keeperId)) {
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "이미 %s 사육장에 배정된 사육사입니다: %s\n", 
                                 enclosure.getName(), keeper.getName());
                
                boolean forceAssign = MenuUtil.Question.askSimpleConfirm("그래도 배정하시겠습니까?");
                if (!forceAssign) {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "배정이 취소되었습니다.");
                    return false;
                }
            }
            
            enclosure.assignCaretaker(keeperId, keeper);
            
            return true;
        } catch (Exception e) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "처리 중 예외가 발생했습니다: " + e.getMessage());
            return false;
        }
    }

    /**
     * 사육사 배정 처리 결과를 테이블 형태로 출력합니다.
     * 처리된 사육장의 현재 사육사 배정 현황을 보여줍니다.
     * 
     * @param enclosureId 배정이 처리된 사육장 ID
     */
    private void displayKeeperAssignmentResult(String enclosureId) {
        Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
        Enclosure enclosure = enclosureOpt.get();
        
        Map<String, Object> caretakers = enclosure.getAllCaretakers();
        
        if (caretakers.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육장에 배정된 사육사가 없습니다.");
            return;
        }
        
        String[] headers = {"Keeper ID", "Name", "Department", "Rank", "Assignment Date"};
        String[][] data = new String[caretakers.size()][];
        int index = 0;
        
        String currentDate = java.time.LocalDate.now().toString();
        
        for (Map.Entry<String, Object> entry : caretakers.entrySet()) {
            String keeperId = entry.getKey();
            Object keeperObj = entry.getValue();
            
            if (keeperObj instanceof ZooKeeper) {
                ZooKeeper keeper = (ZooKeeper) keeperObj;
                data[index] = new String[]{
                    truncateString(keeperId, 15),
                    truncateString(keeper.getName(), 15),
                    truncateString(keeper.getDepartment().toString(), 15),
                    truncateString(keeper.getRank().toString(), 15),
                    truncateString(currentDate, 15)
                };
            } else {
                data[index] = new String[]{
                    truncateString(keeperId, 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString("Unknown", 15),
                    truncateString(currentDate, 15)
                };
            }
            index++;
        }
        
        String title = String.format("%s (%s) 배정된 사육사 현황", 
                      truncateString(enclosure.getName(), 15), enclosureId);
        
        TableUtil.printTable(title, headers, data);
    }
}

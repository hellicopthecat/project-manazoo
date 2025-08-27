package app.enclosure;

import java.util.Collection;
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

/**
 * 동물원 우리를 생성, 조회 및 동물 관리 등 전반적으로 관리하는 클래스입니다.
 */
public class EnclosureManager {

    private final EnclosureRepository repository = EnclosureRepository.getInstance();

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
        String[] headers = {"Enclosure ID", "Name", "Size(m2)", "Temp(C)", "Location", "Environment"};
        String[] values = {
                enclosure.getId(),
                enclosure.getName(),
                String.format("%.1f", enclosure.getAreaSize()),
                String.format("%.1f", enclosure.getTemperature()),
                enclosure.getLocationType().toString(),
                enclosure.getEnvironmentType().toString()
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
                    UIUtil.printSeparator('━');
                    TextArtUtil.printViewMenuTitle();
                    UIUtil.printSeparator('━');
                    viewEnclosures();
                }
                case 3 -> {
                    UIUtil.printSeparator('━');
                    TextArtUtil.printViewMenuTitle();
                    UIUtil.printSeparator('━');
                    editEnclosure();
                }
                case 4 -> {
                    UIUtil.printSeparator('━');
                    TextArtUtil.printRemoveMenuTitle();
                    UIUtil.printSeparator('━');
                    removeEnclosure();
                }
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
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
                case 3 -> System.out.println("사육사배치관리");
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

    // 사육장 관련 메서드들
    private void registerEnclosure() {
        UIUtil.printSeparator('━');
        String id = IdGeneratorUtil.generateId();
        String name = MenuUtil.Question.askTextInput("사육장의 이름을 입력하세요.");
        float areaSize = MenuUtil.Question.askNumberInput("사육장의 크기를 입력하세요", "m2");
        float temperature = MenuUtil.Question.askNumberInput("사육장의 온도를 입력하세요", "C");

        LocationType locationType = selectLocationType();
        EnvironmentType environmentType = selectEnvironmentType();

        Enclosure newEnclosure = new Enclosure(id, name, areaSize, temperature, locationType, environmentType);
        repository.save(newEnclosure.getId(), newEnclosure);
        printEnclosureInfo("사육장이 등록되었습니다. 등록된 사육장의 정보는 아래와 같습니다.", newEnclosure);
    }

    private void viewEnclosures() {
        if (repository.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "등록된 사육장이 없습니다.");
            return;
        }

        // 헤더는 printEnclosureInfo와 동일
        String[] headers = {"Enclosure ID", "Name", "Size(m2)", "Temp(C)", "Location", "Environment"};

        // repository의 모든 사육장 데이터를 2차원 배열로 변환
        Collection<Object> allEnclosures = repository.findAll();
        String[][] data = new String[allEnclosures.size()][];
        int index = 0;

        for (Object obj : allEnclosures) {
            if (obj instanceof Enclosure enclosure) {
                data[index] = new String[]{
                        enclosure.getId(),
                        enclosure.getName(),
                        String.format("%.1f", enclosure.getAreaSize()),
                        String.format("%.1f", enclosure.getTemperature()),
                        enclosure.getLocationType().toString(),
                        enclosure.getEnvironmentType().toString()
                };
                index++;
            }
        }

        // TableUtil.printTable 사용하여 다중 행 표 출력
        String title = String.format("사육장 목록 (총 %d개)", repository.size());
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

    private void editEnclosure() {
        viewEnclosures();

        // Early return 패턴 적용
        if (repository.isEmpty()) {
            return;
        }
        
        String enclosureId = MenuUtil.Question.askTextInput("수정할 사육장 번호를 입력하세요.");

        Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
        
        // Early return 패턴 적용
        if (foundEnclosure.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "입력하신 아이디의 사육장이 없습니다.");
            return;
        }
        
        // 메인 로직 - 중첩 레벨 감소
        Enclosure enclosure = foundEnclosure.get();
        printEnclosureInfo("현재 사육장 정보", enclosure);
        UIUtil.printSeparator('━');

        while (true) {
            String[] editOptions = {"이름 수정", "크기 수정", "온도 수정", "위치타입 수정", "환경타입 수정"};
            String[] specialOptions = {"수정완료"};
            MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printRegisterMenuTitle, editOptions, specialOptions);

            int choice = InputUtil.getIntInput();
            switch (choice) {
                case 1 -> editName(enclosure);
                case 2 -> editAreaSize(enclosure);
                case 3 -> editTemperature(enclosure);
                case 4 -> editLocationType(enclosure);
                case 5 -> editEnvironmentType(enclosure);
                case 0 -> {
                    System.out.println(MenuUtil.DEFAULT_PREFIX + "수정이 완료되었습니다!");
                    printEnclosureInfo("수정된 사육장 정보", enclosure);
                    return;
                }
                default -> System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    private void removeEnclosure() {
        viewEnclosures();

        if (repository.isEmpty()) {
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
                    Object deletedEnclosure = repository.deleteById(enclosureId);
                    if (deletedEnclosure != null) {
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
     * 기존 동물을 특정 사육장에 입주시키는 기능으로, 다음과 같은 과정을 거칩니다:
     * 1. 사전 조건 확인 (사육장과 동물 존재 여부)
     * 2. 사용자에게 사육장과 동물 목록 표시
     * 3. 사육장 선택 (3회 시도 제한)
     * 4. 동물 선택 (3회 시도 제한)
     * 5. 입사 처리 (사육장에 추가, 대기 목록에서 제거)
     * 6. 결과 표시 (입사한 사육장의 동물 현황)
     */
    private void manageAnimalAdmission() {
        UIUtil.printSeparator('━');
        System.out.println("동물 입사 관리");
        UIUtil.printSeparator('━');
        
        // 1. 사전 조건 확인 - 사육장과 동물이 모두 있어야 실행 가능
        if (!hasRequiredDataForAdmission()) {
            return; // Early return으로 중첩 방지
        }
        
        // 2. 현재 상황을 사용자에게 표시
        displayDataForAdmission();
        
        // 3. 사육장 선택 (3회 시도 제한)
        String enclosureId = selectEnclosureWithRetry();
        if (enclosureId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        // 4. 동물 선택 (3회 시도 제한)
        String animalId = selectAnimalWithRetry();
        if (animalId == null) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력이 3회 반복되어 작업을 취소합니다.");
            return;
        }
        
        // 5. 입사 처리 - 실제 데이터 이동
        processAnimalAdmission(enclosureId, animalId);
        
        // 6. 결과 출력 - 입사한 사육장의 현재 상황 표시
        displayAdmissionResult(enclosureId);
    }

    /**
     * 동물 입사를 위한 필수 데이터가 있는지 확인합니다.
     * 사육장과 배치 가능한 동물이 모두 있어야 동물 입사가 가능합니다.
     * 
     * @return 필수 데이터 존재 여부 (true: 실행 가능, false: 실행 불가능)
     */
    private boolean hasRequiredDataForAdmission() {
        // 사육장 존재 여부 확인
        if (repository.isEmpty()) {
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
     * 동물 입사를 위한 데이터를 사용자에게 표시합니다.
     * 현재 등록된 사육장 목록과 배치 가능한 동물 목록을 보여줍니다.
     */
    private void displayDataForAdmission() {
        System.out.println();
        System.out.println("사용 가능한 사육장 목록:");
        viewEnclosures(); // 기존 메서드 재사용
        
        System.out.println();
        System.out.println("배치 가능한 동물 목록:");
        AnimalManager.getInstance().displayAvailableAnimalsTable();
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
     * 동물을 선택받습니다. 잘못된 입력 시 최대 3회까지 재시도할 수 있습니다.
     * 각 시도마다 사용자에게 확인을 받아 정확한 선택을 보장합니다.
     * 
     * @return 선택된 동물 ID (3회 실패 시 null 반환)
     */
    private String selectAnimalWithRetry() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.printf("\n[%d/3] 입사시킬 동물 ID를 입력하세요: ", attempt);
            String animalId = MenuUtil.Question.askTextInput("");
            
            // AnimalManager에서 동물 검색
            Optional<app.animal.Animal> foundAnimal = AnimalManager.getInstance().getAnimalFromAll(animalId);
            if (foundAnimal.isPresent()) {
                app.animal.Animal animal = foundAnimal.get();
                // 배치 가능한 동물인지 확인 (enclosureId가 null이거나 빈 문자열)
                String currentEnclosureId = animal.getEnclosureId();
                if (currentEnclosureId == null || currentEnclosureId.trim().isEmpty()) {
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
                    System.out.printf(MenuUtil.DEFAULT_PREFIX + "이미 배치된 동물입니다: %s (사육장: %s)\n", 
                                    animalId, currentEnclosureId);
                }
            } else {
                System.out.printf(MenuUtil.DEFAULT_PREFIX + "존재하지 않는 동물 ID입니다: %s\n", animalId);
            }
            
            // 아직 시도 기회가 남았다면 안내 메시지
            if (attempt < 3) {
                System.out.println(MenuUtil.DEFAULT_PREFIX + "다시 시도해주세요.");
            }
        }
        return null; // 3회 모두 실패
    }

    /**
     * 동물 입사를 실제로 처리합니다.
     * 선택된 동물을 사육장의 inhabitants에 추가하고, 
     * AnimalManager의 배치 가능한 동물 목록에서 제거합니다.
     * 
     * @param enclosureId 입사할 사육장 ID
     * @param animalId    입사할 동물 ID
     */
    private void processAnimalAdmission(String enclosureId, String animalId) {
        // 사육장 객체 가져오기 (이미 검증됨)
        Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
        Enclosure enclosure = enclosureOpt.get();
        
        // AnimalManager에서 동물 객체 가져오기 및 사육장 배치 처리
        app.animal.Animal animal = AnimalManager.getInstance().removeAvailableAnimal(animalId, enclosureId);
        
        if (animal != null) {
            // 사육장에 동물 추가 (Enclosure.addInhabitant 사용)
            enclosure.addInhabitant(animalId, animal);
            
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 입사가 성공적으로 처리되었습니다!");
        } else {
            System.out.println();
            System.out.println(MenuUtil.DEFAULT_PREFIX + "동물 배치 중 오류가 발생했습니다.");
        }
    }

    /**
     * 동물 입사 결과를 표로 출력합니다.
     * 입사가 진행된 사육장의 현재 동물 현황을 테이블 형태로 보여줍니다.
     * 표시 항목: Animal ID, Name, Species, Age, Admission Date
     * 각 항목은 최대 15글자로 제한됩니다.
     * 
     * @param enclosureId 입사가 처리된 사육장 ID
     */
    private void displayAdmissionResult(String enclosureId) {
        Optional<Enclosure> enclosureOpt = repository.findById(enclosureId);
        Enclosure enclosure = enclosureOpt.get();
        
        // 사육장에 입주한 동물들 가져오기
        Map<String, Object> inhabitants = enclosure.getAllInhabitants();
        
        if (inhabitants.isEmpty()) {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "사육장에 동물이 없습니다.");
            return;
        }
        
        // 테이블 헤더 정의 (15글자 제한)
        String[] headers = {"Animal ID", "Name", "Species", "Age", "Admission Date"};
        String[][] data = new String[inhabitants.size()][];
        int index = 0;
        
        // 현재 날짜를 입사일로 사용
        String currentDate = java.time.LocalDate.now().toString();
        
        // 동물 데이터를 테이블 형태로 변환
        for (Map.Entry<String, Object> entry : inhabitants.entrySet()) {
            String animalId = entry.getKey();
            Object animalObj = entry.getValue();
            
            // Animal 객체에서 실제 정보 추출
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
                // 임시 데이터 (호환성 보장)
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
        
        // 테이블 제목 생성 (사육장 이름도 15글자 제한)
        String title = String.format("%s (%s) 현재 거주 동물 현황", 
                      truncateString(enclosure.getName(), 15), enclosureId);
        
        // 테이블 출력
        TableUtil.printTable(title, headers, data);
    }

    /**
     * 문자열을 지정된 최대 길이로 자르는 유틸리티 메서드입니다.
     * CMD 환경에서의 표시 호환성을 위해 문자열 길이를 제한합니다.
     * 
     * @param str       원본 문자열
     * @param maxLength 최대 허용 길이
     * @return 잘린 문자열 (null인 경우 빈 문자열 반환)
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}

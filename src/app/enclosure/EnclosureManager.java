package app.enclosure;

import java.util.Collection;
import java.util.Optional;

import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.TableUtil;
import app.common.ui.UIUtil;

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
     * @param title 제목
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
                case 2 -> System.out.println("동물입사관리");
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

        if(repository.isEmpty()){
            return;
        }
        String enclosureId = MenuUtil.Question.askTextInput("수정할 사육장 번호를 입력하세요.");

        Optional<Enclosure> foundEnclosure = repository.findById(enclosureId);
        if (foundEnclosure.isPresent()) {
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
        } else {
            System.out.println(MenuUtil.DEFAULT_PREFIX + "입력하신 아이디의 사육장이 없습니다.");
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
}

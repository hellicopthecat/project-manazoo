
/**
 * 동물원 우리를 생성, 조회 및 동물 관리 등 전반적으로 관리하는 클래스입니다.
 */
package app.enclosure;

import app.common.id.IdGeneratorUtil;

import java.util.Scanner;

public class EnclosureManager {

    private final static Scanner scanner = new Scanner(System.in);

    // 안전한 정수 입력 메서드
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("올바른 숫자를 입력해주세요: ");
            }
        }
    }

    // 안전한 정수 입력 메서드
    private static float getFloatInputOneDecimal() {
        while (true) {
            String line = scanner.nextLine().trim();

            // 패턴 설명:
            // ^[+-]?         : 선택적 부호
            // (?:\\d+        : 정수부 1자리 이상
            // (?:\\.\\d)?    : 소수점 이하가 있다면 정확히 1자리
            // )$
            //
            // 허용 예:  "1", "-3", "0.5", "+12.3"
            // 비허용 예: "1.", ".5", "1.23", "abc"
            if (line.matches("^[+-]?(?:\\d+(?:\\.\\d)?)$")) {
                try {
                    return Float.parseFloat(line);
                } catch (NumberFormatException ignored) {
                    // float 범위를 벗어나면 아래 안내 출력
                }
            }

            System.out.print("올바른 형식의 실수(소수점 1자리까지)로 입력해주세요: ");
        }
    }

    // 안전한 문자열 입력 메서드
    private static String getStringInput() {
        return scanner.nextLine().trim();
    }

    /**
     * EnclosureManager를 테스트하거나 실행하는 메인 메서드입니다.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== 사육장 관리 ===");
            System.out.println("1. 사육장등록  2. 사육장조회  3. 사육장수정  4. 사육장삭제  0. 뒤로가기");
            System.out.print("선택해주세요: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> registerEnclosure();
                case 2 -> viewEnclosures();
                case 3 -> editEnclosure();
                case 4 -> removeEnclosure();
                case 0 -> {
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void handleEnclosureManagement() {
        while (true) {
            System.out.println("\n=== 사육장 관리 ===");
            System.out.println("1. 사육장등록  2. 사육장조회  3. 사육장수정  4. 사육장삭제  0. 뒤로가기");
            System.out.print("선택해주세요: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> registerEnclosure();
                case 2 -> viewEnclosures();
                case 3 -> editEnclosure();
                case 4 -> removeEnclosure();
                case 0 -> {
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
    }

    // 사육장 관련 메서드들
    private static void registerEnclosure() {
        String id = IdGeneratorUtil.generateId();
        System.out.println(IdGeneratorUtil.getEnclosureIdCount());
        System.out.println("\n사육장 등록 기능입니다.");
        System.out.print("사육장 이름을 입력하세요: ");
        String name = getStringInput();
        System.out.print("사육장 크기(㎡)를 입력하세요: ");
        float areaSize = getFloatInputOneDecimal();
        System.out.print("사육장 온도(°C)를 입력하세요: ");
        float temperature = getFloatInputOneDecimal();
        LocationType locationType;

        while (true) {
            System.out.print("""
            사육장의 위치 타입을 입력하세요.
            1. 실내  2. 야외
            정수를 입력하세요:
            """);
            int choice = getIntInput();

            locationType = switch (choice) {
                case 1 -> LocationType.INDOOR;
                case 2 -> LocationType.OUTDOOR;
                default -> null; // 유효하지 않으면 null
            };

            if (locationType != null) break;
            System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
        }

        EnvironmentType environmentType;
        while (true) {
            System.out.print("""
            사육장의 환경 타입을 입력하세요.
            1. LAND  2. AQUATIC  3. MIXED
            정수를 입력하세요:
            """);
            int choice = getIntInput();

            environmentType = switch (choice) {
                case 1 -> EnvironmentType.LAND;
                case 2 -> EnvironmentType.AQUATIC;
                case 3 -> EnvironmentType.MIXED;
                default -> null;
            };

            if (environmentType != null) break;
            System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
        }

        Enclosure newEnclosure = new Enclosure(id, name, areaSize, temperature, locationType, environmentType);
        EnclosureRepository repository = EnclosureRepository.getInstance();
        repository.save(newEnclosure.getId(), newEnclosure);
        System.out.println(IdGeneratorUtil.getStatus());
    }

    private static void viewEnclosures() {
        System.out.println("\n=== 사육장 목록 ===");
        System.out.println("A-1: 맹수관 (100㎡, 수용량: 2마리) - 호랑이 1마리");
        System.out.println("B-2: 초식동물관 (200㎡, 수용량: 3마리) - 코끼리 1마리");
        System.out.println("C-3: 사바나관 (300㎡, 수용량: 5마리) - 기린 1마리");
        System.out.println("D-4: 극지관 (150㎡, 수용량: 10마리) - 펭귄 1마리");
        System.out.println("E-5: 원숭이관 (80㎡, 수용량: 8마리) - 원숭이 1마리");
    }

    private static void editEnclosure() {
        System.out.println("\n사육장 수정 기능입니다.");
        viewEnclosures();
        System.out.print("수정할 사육장 번호를 입력하세요: ");
        String EnclosureId = getStringInput();
        System.out.println("사육장 " + EnclosureId + " 정보가 수정되었습니다.");
    }

    private static void removeEnclosure() {
        System.out.println("\n사육장 삭제 기능입니다.");
        viewEnclosures();
        System.out.print("삭제할 사육장 번호를 입력하세요: ");
        String EnclosureId = getStringInput();
        System.out.println("사육장 " + EnclosureId + "이(가) 삭제되었습니다.");
    }

}

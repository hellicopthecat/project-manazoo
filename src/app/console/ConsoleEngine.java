package app.console;

import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.enclosure.EnclosureManager;

public class ConsoleEngine {

    public static void main(String[] args) {
        TextArtUtil.printLoadingAnimation();
        MenuUtil.printAccessMenu();
        
        // 접속 방식 선택 루프
        while (true) {
            int userChoice = InputUtil.getIntInput();

            switch (userChoice) {
                case 1 -> {
                    System.out.println("관리자 모드로 접속합니다...");
                    UIUtil.printSeparator('━');
                    TextArtUtil.printWelcomeMessage();
                    String[] option = {"동물 관리", "사육장 관리", "직원 관리"};
                    String[] specialOptions = {"뒤로가기"};
                    MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, option, specialOptions);

                    while (true) {
                        int choice = InputUtil.getIntInput();
                        switch (choice) {
                            case 1 -> {

                            }
                            case 2 -> {
                                EnclosureManager m = new EnclosureManager();
                                m.handleEnclosureManagement();
                            }
                            case 3 -> {}
                            case 0 -> {
                                System.out.println("이전 메뉴로 돌아갑니다.");
                                return;
                            }
                            default -> {
                                System.out.println("잘못된 입력입니다. 1 또는 2를 입력하세요.");
                                System.out.println("  선택 번호를 입력하세요 ▶");
                            }
                        }
                    }
                }
                case 2 -> {
                    System.out.println("관람객 모드로 접속합니다...");
                    TextArtUtil.printWelcomeMessage();
                }
                default -> {
                    System.out.println("잘못된 입력입니다. 1 또는 2를 입력하세요.");
                    System.out.println("  선택 번호를 입력하세요 ▶");
                }
            }
        }

//        EnclosureManager i = new EnclosureManager();
//        i.handleEnclosureManagement();
    }
}

package app.console;

import app.animal.AnimalManager;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.enclosure.EnclosureManager;
import app.finance.FinanceManager;
import app.visitor.VisitorManager;
import app.zooKeeper.ZooKeeperManager;

/**
 * 콘솔 기반 동물원 관리 시스템의 메인 엔진 클래스입니다.
 * 사용자 인터페이스와 메뉴 네비게이션을 담당합니다.
 */
public class ConsoleEngine {
    
    /**
     * 애플리케이션을 시작합니다.
     * 로딩 애니메이션 후 메인 메뉴로 진입합니다.
     */
    public static void start() {
        TextArtUtil.printLoadingAnimation();
        showAccessMenu();
    }
    
    /**
     * 접속 방식 선택 메뉴를 표시하고 처리합니다.
     * 관리자 모드와 관람객 모드 중 선택할 수 있습니다.
     */
    private static void showAccessMenu() {
        while (true) {
            MenuUtil.printAccessMenu();
            int userChoice = InputUtil.getIntInput();

            switch (userChoice) {
                case 1 -> {
                    handleAdminMode();
                }
                case 2 -> {
                    handleVisitorMode();
                }
                default -> {
                    showInvalidAccessChoice();
                }
            }
        }
    }
    
    /**
     * 관리자 모드를 처리합니다.
     * 동물 관리, 사육장 관리, 직원 관리 메뉴를 제공합니다.
     */
    private static void handleAdminMode() {
        System.out.println(MenuUtil.DEFAULT_PREFIX + "관리자 모드로 접속합니다...");
        UIUtil.printSeparator('━');
        TextArtUtil.printWelcomeMessage();
        
        showAdminMenu();
    }
    
    /**
     * 관리자 메뉴를 표시하고 처리합니다.
     */
    private static void showAdminMenu() {
        String[] option = {"동물 관리", "사육장 관리", "직원 관리", "제정 관리"};
        String[] specialOptions = {"뒤로가기"};
        MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, option, specialOptions);

        while (true) {
            int choice = InputUtil.getIntInput();
            
            switch (choice) {
                case 1 -> {
                    handleAnimalManagement();
                }
                case 2 -> {
                    handleEnclosureManagement();
                }
                case 3 -> {
                    handleStaffManagement();
                }
                case 4 -> {
                    handleFinancialManagement();
                }
                case 0 -> {
                    handleBackToAccessMenu();
                }
                default -> {
                    showInvalidAdminChoice();
                }
            }
        }
    }
    
    /**
     * 동물 관리 기능을 처리합니다.
     */
    private static void handleAnimalManagement() {
        AnimalManager manager = new AnimalManager();
        manager.run();
    }
    
    /**
     * 사육장 관리 기능을 처리합니다.
     */
    private static void handleEnclosureManagement() {
        EnclosureManager manager = new EnclosureManager();
        manager.handleEnclosureManagement();
    }
    
    /**
     * 직원 관리 기능을 처리합니다.
     */
    private static void handleStaffManagement() {
        ZooKeeperManager manager = new ZooKeeperManager();
        manager.handleZookeeperManagement();
    }

    /**
     * 재정 관리 기능을 처리합니다.
     */
    private static void handleFinancialManagement() {
        FinanceManager.getInstance().handleFinanceManagement();
    }

    /**
     * 관람객 모드를 처리합니다.
     * TODO: 관람객용 기능 구현
     */
    private static void handleVisitorMode() {
        System.out.println(MenuUtil.DEFAULT_PREFIX + "관람객 모드로 접속합니다...");
        TextArtUtil.printWelcomeMessage();
        TextArtUtil.printVisitorMenuTitle();
        UIUtil.printSeparator('━');
        VisitorManager manager = new VisitorManager();
        manager.run();
    }
    
    /**
     * 이전 메뉴로 돌아가는 처리를 합니다.
     */
    private static void handleBackToAccessMenu() {
        System.out.println(MenuUtil.DEFAULT_PREFIX + "이전 메뉴로 돌아갑니다.");
    }
    
    /**
     * 잘못된 접속 방식 선택에 대한 오류 메시지를 표시합니다.
     */
    private static void showInvalidAccessChoice() {
        System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 1 또는 2를 입력하세요.");
        System.out.print(MenuUtil.DEFAULT_PREFIX + "선택 번호를 입력하세요 ▶ ");
    }
    
    /**
     * 잘못된 관리자 메뉴 선택에 대한 오류 메시지를 표시합니다.
     */
    private static void showInvalidAdminChoice() {
        System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 입력입니다. 올바른 번호를 입력하세요.");
        System.out.print(MenuUtil.DEFAULT_PREFIX + "선택 번호를 입력하세요 ▶ ");
    }
}

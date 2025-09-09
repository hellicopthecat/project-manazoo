package app.console;

import java.sql.Connection;
import java.sql.SQLException;

import app.animal.AnimalManager;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.config.DatabaseConnection;
import app.enclosure.EnclosureManager;
import app.finance.FinanceManager;
import app.visitor.VisitorManager;
import app.zooKeeper.ZooKeeperManager;

/**
 * 콘솔 기반 동물원 관리 시스템의 메인 엔진 클래스입니다. 사용자 인터페이스와 메뉴 네비게이션을 담당합니다.
 */
public class ConsoleEngine {
	/**
	 * 애플리케이션을 시작합니다. 로딩 애니메이션 후 메인 메뉴로 진입합니다.
	 * 
	 * @throws SQLException
	 */
	public static void start() throws SQLException {
		TextArtUtil.printLoadingAnimation();
		Connection connection = DatabaseConnection.getConnection();
		showAccessMenu(connection);
	}

	/**
	 * 접속 방식 선택 메뉴를 표시하고 처리합니다. 관리자 모드와 관람객 모드 중 선택할 수 있습니다.
	 * 
	 * @throws SQLException
	 */
	private static void showAccessMenu(Connection connection) throws SQLException {
		while (true) {
			MenuUtil.printAccessMenu();
			int userChoice = InputUtil.getIntInput();

			switch (userChoice) {
			case 1 -> {
				handleAdminMode(connection);
			}
			case 2 -> {
				handleVisitorMode();
			}
			case 0 -> {
				UIUtil.printSeparator('━');
				DatabaseConnection.closeConnection(connection);
				System.out.println(MenuUtil.DEFAULT_PREFIX + "프로그램을 종료합니다.");
				return;
			}
			default -> {
				showInvalidAccessChoice();
			}
			}
		}
	}

	/**
	 * 관리자 모드를 처리합니다. 동물 관리, 사육장 관리, 직원 관리 메뉴를 제공합니다.
	 * 
	 * @throws SQLException
	 */
	private static void handleAdminMode(Connection connection) throws SQLException {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "관리자 모드로 접속합니다...");
		UIUtil.printSeparator('━');
		TextArtUtil.printWelcomeMessage();

		showAdminMenu(connection);
	}

	/**
	 * 관리자 메뉴를 표시하고 처리합니다.
	 * 
	 * @throws SQLException
	 */
	private static void showAdminMenu(Connection connection) throws SQLException {

		while (true) {
			String[] option = { "동물 관리", "사육장 관리", "직원 관리", "재정 관리" };
			String[] specialOptions = { "뒤로가기" };
			MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, option, specialOptions);
			int choice = InputUtil.getIntInput();

			switch (choice) {
			case 1 -> {
				handleAnimalManagement(connection);
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
				return;
			}
			default -> {
				showInvalidAdminChoice();
			}
			}
		}
	}

	/**
	 * 동물 관리 기능을 처리합니다.
	 * 
	 * @throws SQLException
	 */
	private static void handleAnimalManagement(Connection connection) throws SQLException {
		AnimalManager manager = new AnimalManager();
		manager.handleAnimalManagement(connection);
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
		ZooKeeperManager manager = ZooKeeperManager.getInstance();
		manager.handleZookeeperManagement();
	}

	/**
	 * 재정 관리 기능을 처리합니다.
	 */
	private static void handleFinancialManagement() {
		FinanceManager.getInstance().handleFinanceManagement();
	}

	/**
	 * 관람객 모드를 처리합니다. TODO: 관람객용 기능 구현
	 */
	private static void handleVisitorMode() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "관람객 모드로 접속합니다...");
		UIUtil.printSeparator('━');
		TextArtUtil.printWelcomeMessage();
		VisitorManager manager = new VisitorManager();
		manager.handleVisitorManagement();
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

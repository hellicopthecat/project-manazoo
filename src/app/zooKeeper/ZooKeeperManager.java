package app.zooKeeper;

import java.util.concurrent.atomic.AtomicBoolean;

import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.finance.FinanceManager;

public class ZooKeeperManager {

	private final ZooKeeperRepository repository = ZooKeeperRepository.getInstance();

	/**
	 * test용 main 함수입니다.
	 */
	public static void main(String[] args) {
		ZooKeeperManager z = new ZooKeeperManager();
		z.handleZookeeperManagement();
	}

	private void createDummyData() {
		// 비관리자
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "루피", 20, 1, 1, 1, 1, 1, 1, "");
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "크롱", 22, 2, 2, 2, 1, 2, 1, "자격증, 자격증2");
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "뽀로로", 32, 1, 3, 3, 1, 7, 2, "");
		// 관리자
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "스펀지밥", 40, 1, 4, 4, 1, 12, 2, "자격증, 자격증2, 자격증3");
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "뚱이", 4, 1, 5, 5, 2, 14, 1, "자격증 ");
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "집게사장", 50, 1, 6, 6, 1, 25, 1,
				"자격증, 자격증2, 자격증3, 자격증4");
		repository.createZooKeeper(IdGeneratorUtil.generateId(), "다람이", 50, 1, 3, 6, 1, 25, 1, "자격증, 자격증2, 자격증3, 자격증4");
	}

	/*
	 * ZooKeeperManagement으로 콘솔을 실행시킬 수 있습니다.
	 */
	public void handleZookeeperManagement() {
		createDummyData();
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			String[] menu = { "사육사 등록", "사육사 찾기", "사육사 수정", "사육사 삭제", "급여지출결의서작성" };
			String[] s_menu = { "뒤로가기" };
			UIUtil.printSeparator('━');
			TextArtUtil.printZookeeperMenuTitle();
			UIUtil.printSeparator('━');
			MenuUtil.generateMenuWithTextTitle("사 육 사 관 리", menu, s_menu);
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> registerZooKeeper();
			case 2 -> getZooKeeper();
			case 3 -> editZooKeeper();
			case 4 -> deleteZooKeeper();
			case 5 -> createSalaryService();
			case 0 -> {
				goBack(run);
				String[] option = { "동물 관리", "사육장 관리", "직원 관리", "재정 관리" };
				String[] specialOptions = { "뒤로가기" };
				MenuUtil.generateMenuWithSpecialOptions(TextArtUtil::printAdminMenuTitle, option, specialOptions);
			}
			default -> wrongIndex();
			}
		}
	}

	/**
	 * 사육사 등록 메서드 입니다.
	 * 
	 *  @param Scanner
	 */
	private void registerZooKeeper() {
		while (true) {
			// 이름
			String name;
			UIUtil.printSeparator('━');
			System.out.println(MenuUtil.DEFAULT_PREFIX + "사 육 사 생 성 ");
			UIUtil.printSeparator('━');
			while (true) {
				// 이름
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + "이름을 입력하세요 ▶ ︎");
				System.out.println();
				name = InputUtil.getStringInput();
				if (name == null || name.isEmpty()) {
					UIUtil.printSeparator('━');
					System.out.println(MenuUtil.DEFAULT_PREFIX + "이름은 비워둘 수 없습니다.");
					UIUtil.printSeparator('━');
				} else if (!name.matches("^[가-힣a-zA-Z]{2,20}$")) {
					UIUtil.printSeparator('━');
					System.out.println(MenuUtil.DEFAULT_PREFIX + "이름은 한글 또는 영문 2~20자 입니다.");
					UIUtil.printSeparator('━');
				} else {
					break;
				}
			}
			// 나이
			int age = getValidateInt("나이를 입력하세요 ▶ ", "20세 ~ 65세", 20, 65);
			// 성별
			int genderIndex = getValidateInt("성별을 입력하세요 ▶ ", "1 : 남성 , 2 : 여성", 1, 2);
			// 직책
			int rankIndex = getValidateInt("직책을 입력하세요 ▶ ",
					"1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장", 1, 6);
			// 부서
			int departmentIndex = getValidateInt("부서를 입력하세요 ▶ ",
					"1 : 포유류부서 , 2 : 조류부서 , 3 : 파충류부서 , 4 : 어류부서 , 5 : 양서류부서 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육", 1, 8);
			// 재직여부
			int isWorkingIndex = getValidateInt("재직여부를 입력하세요 ▶ ", "1 : 재직 , 2 : 퇴사", 1, 2);
			// 재직여부
			int experienceYear = getValidateInt("연차를 입력하세요 ▶ ", "1 ~ 40", 1, 40);
			// 맹수조련여부
			int canHandleDangerAnimalIndex = getValidateInt("맹수조련여부를 입력하세요 ▶ ", "1 : 가능 , 2 : 불가능", 1, 2);
			// 자격
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "자격증을 작성하세요. ',' 로 구분할 수 있습니다 ▶ ");
			System.out.println();
			String desc = InputUtil.getStringInput();

			// id 생성
			String id = IdGeneratorUtil.generateId();
			repository.createZooKeeper(id, name, age, genderIndex, rankIndex, departmentIndex, isWorkingIndex,
					experienceYear, canHandleDangerAnimalIndex, desc);
			UIUtil.printSeparator('━');
			System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사가 등록되었습니다.");
			UIUtil.printSeparator('━');
			break;
		}
	}

	/**
	 * 사육사 조회 메서드입니다.
	 * 
	 * @param Scanner
	 */

	// 읽기
	private void getZooKeeper() {
		AtomicBoolean run = new AtomicBoolean(true);
		UIUtil.printSeparator('━');
		System.out.println("사육사 조회");
		System.out.println();
		System.out.println(
				MenuUtil.DEFAULT_PREFIX + "1. 전체리스트 조회, 2. ID로 찾기(개인), 3. 이름으로 찾기(다수), 4. 부서로 찾기(다수), 0. 뒤로가기");
		System.out.println();
		UIUtil.printSeparator('━');
		System.out.println(MenuUtil.DEFAULT_PREFIX + "어떤 방식으로 찾으시겠습니까?  ▶ ");
		int index = InputUtil.getIntInput();
		switch (index) {
		case 1 -> getZooKeeperList();
		case 2 -> getZooKeeperById();
		case 3 -> getZooKeeperByName();
		case 4 -> getZooKeeperByDepartment();
		case 0 -> goBack(run);
		default -> wrongIndex();
		}
	}

	/**
	 * 사육사 전체 리스트를 출력합니다.
	 */
	private void getZooKeeperList() {
		UIUtil.printSeparator('━');
		repository.getZooKeeperList().stream().forEach(x -> {
			System.out.println(x);
		});
		UIUtil.printSeparator('━');
	}

	/**
	 * 특정 id의 사육사를 출력합니다.
	 */
	private void getZooKeeperById() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "아이디를 입력해주세요 ▶ ");
		String id = InputUtil.getStringInput();
		System.out.println(repository.getZooKeeperById(id));
	}

	/**
	 * 특정이름으로 사육사를 출력합니다.
	 */
	private void getZooKeeperByName() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "이름을 입력해주세요 ▶ ");
		String name = InputUtil.getStringInput();
		UIUtil.printSeparator('━');
		System.out.println();
		System.out.println(repository.getZooKeeperByName(name));
		System.out.println();
		UIUtil.printSeparator('━');
	}

	/**
	 * 부서별 사육사들을 출력합니다.
	 */
	private void getZooKeeperByDepartment() {
		System.out.println(MenuUtil.DEFAULT_PREFIX
				+ "1 : 포유류 , 2 : 조류 , 3 : 파충류 , 4 : 어류 , 5 : 양서류 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육");
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "부서를 고르세요 ▶ ");
		int departmentIndex = InputUtil.getIntInput();
		UIUtil.printSeparator('━');
		repository.getZooKeeperByDepartment(departmentIndex).forEach(System.out::println);
		UIUtil.printSeparator('━');
	}

	/**
	 * 사육사 수정 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editZooKeeper() {
		AtomicBoolean run = new AtomicBoolean(true);
		System.out.println(MenuUtil.DEFAULT_PREFIX + "무엇을 수정하시겠습니까?");
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "1. 재직여부, 2. 업무배정가능여부, 3. 위험군동물관리여부, 0. 뒤로가기");
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "메뉴를 고르세요 ▶ ");
		int index = InputUtil.getIntInput();
		switch (index) {
		case 1 -> editIsWorking();
		case 2 -> editCanAssignTask();
		case 3 -> editPermissionDangerAnimal();
		case 0 -> goBack(run);
		default -> wrongIndex();
		}
	}

	/**
	 * 사육사 수정(재직여부) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editIsWorking() {
		IdTracker ids = getIds();
		int index = getValidateInt(null, "1. 재직, 2. 퇴사", 1, 2);
		repository.setIsWorking(ids.getMyId(), ids.getTargetId(), index);
		UIUtil.printSeparator('━');
		System.out
				.println(MenuUtil.DEFAULT_PREFIX + "************************ 재직여부가 수정되었습니다. ************************");
		UIUtil.printSeparator('━');
	}

	/**
	 * 사육사 수정(업무할당) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editCanAssignTask() {
		IdTracker ids = getIds();
		int index = getValidateInt(null, "1. 가능, 2. 불가능", 1, 2);
		repository.setCanAssignTask(ids.getMyId(), ids.getTargetId(), index);
		System.out.println(
				MenuUtil.DEFAULT_PREFIX + "************************ 업무배정가능여부가 수정되었습니다. ************************");
	}

	/**
	 * 사육사 수정(고위험동물군관리여부) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editPermissionDangerAnimal() {
		IdTracker ids = getIds();
		int index = getValidateInt(null, "1. 가능, 2. 불가능", 1, 2);
		repository.setPermissionDangerAnimal(ids.getMyId(), ids.getTargetId(), index);
		System.out.println(
				MenuUtil.DEFAULT_PREFIX + "************************ 위험동물관리여부가 수정되었습니다. ************************");
	}

	/**
	 * 사육사 삭제 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void deleteZooKeeper() {
		IdTracker ids = getIds();
		repository.removeZooKeeper(ids.getMyId(), ids.getTargetId());
		System.out.println(MenuUtil.DEFAULT_PREFIX + "************************ 삭제되었습니다. ************************");
	}

	// 급여생성

	private void createSalaryService() {
		IdTracker ids = getIds();
		long money = FinanceManager.getInstance().useMoney();
		boolean ok = repository.setSalary(ids.getMyId(), ids.targetId, money);
		if (ok) {
			System.out.println("************************ 급여가 생성 되었습니다. ************************");
		}

	}

	// inner util methods
	/**
	 * 수의 최저값과 최고값을 정하고 그 사이 int 값을 반환하는 메소드입니다.
	 * 
	 *   @param in Scanner
	 *   @param message 제목 String
	 *   @param numInfo 수의 정보를 나타내주는 String
	 *   @param min 최저값
	 *   @param max 최고값
	 */
	private int getValidateInt(String message, String numInfo, int min, int max) {
		while (true) {
			if (message != null && !message.isEmpty()) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + message);
			}
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + numInfo);
			System.out.println(" ▶ ");
			System.out.println();
			String input = InputUtil.getStringInput();
			try {
				int value = Integer.parseInt(input);
				if (value >= min && value <= max) {
					return value;
				} else {
					System.out.println(MenuUtil.DEFAULT_PREFIX + numInfo);
				}
			} catch (NumberFormatException e) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "숫자를 입력해주세요  ▶ ");
			}
		}
	}

	/*
	 * 뒤로가기
	 */
	private void goBack(AtomicBoolean run) {
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "뒤로 갑니다.");
		System.out.println();
		run.set(false);
	}

	/*
	 * 잘못된 번호 문구 리턴
	 */
	private void wrongIndex() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 번호입니다.");
	}

	/*
	 * 사육사 수정 시 반복되는 id값을 받는것을 객체화 시켰습니다.
	 */
	// inner class
	private class IdTracker {
		private String myId;
		private String targetId;

		public IdTracker(String myId, String targetId) {
			this.myId = myId;
			this.targetId = targetId;
		}

		public String getMyId() {
			return myId;
		}

		public String getTargetId() {
			return targetId;
		}
	}

	/**
	 * id값을 작성하고 검증 및 객체를 생성하는 메서드입니다.
	 * 
	 * @param in Scanner
	 */
	private IdTracker getIds() {
		String myId, targetId;
		while (true) {
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "본인 아이디를 입력하세요 ▶ ");
			System.out.println();
			myId = InputUtil.getStringInput();
			if (myId.matches("^K-([0-9]{4,})$")) {
				break;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 형식입니다.");
			}
		}
		while (true) {
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "상대 아이디를 입력하세요 ▶ ");
			System.out.println();
			targetId = InputUtil.getStringInput();
			if (targetId.matches("^K-([0-9]{4,})$")) {
				break;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 형식입니다.");
			}
		}
		return new IdTracker(myId, targetId);
	}
}

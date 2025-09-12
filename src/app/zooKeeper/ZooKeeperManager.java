package app.zooKeeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import app.common.DatabaseIdGenerator;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TableUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.finance.FinanceManager;
import app.incomeExpend.IncomeExpend;
import app.repository.interfaces.ZooKeeperRepository;
import app.repository.jdbc.JdbcZooKeeperRepository;
import app.repository.memory.MemoryZooKeeperRepository;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;

/**
 * ZooKeeperManager 클래스
 * ---------------------
 * 사육사 관리를 담당하는 클래스입니다.
 * Repository 패턴을 적용하여 데이터 계층을 분리하고 타입 안전성을 확보했습니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사육사 등록 및 관리</li>
 *   <li>다양한 조회 옵션 (전체, ID별, 이름별, 부서별)</li>
 *   <li>사육사 정보 수정</li>
 *   <li>사육사 삭제 및 급여 설정</li>
 * </ul>
 */

public final class ZooKeeperManager {

	/**
	 * Singleton 인스턴스
	 */
	private static final ZooKeeperManager instance = new ZooKeeperManager();

	/**
	 * 사육사 데이터를 관리하는 Repository
	 * Singleton Repository를 사용하여 데이터 일관성을 보장합니다.
	 */
	private final ZooKeeperRepository repository = MemoryZooKeeperRepository.getInstance();

	private final JdbcZooKeeperRepository jdbcRepository = JdbcZooKeeperRepository.getInstance();

	/**
	 * private 생성자 - Singleton 패턴 적용
	 * 초기 더미 데이터를 생성합니다.
	 */
	private ZooKeeperManager() {
//		initializeTestData();
	}

	/**
	 * Singleton 인스턴스를 반환합니다.
	 * 
	 * @return ZooKeeperManager 인스턴스
	 */
	public static ZooKeeperManager getInstance() {
		return instance;
	}

	/**
	 * Repository 인스턴스를 반환합니다.
	 * 다른 Manager에서 사육사 정보가 필요할 때 사용됩니다.
	 * 
	 * @return ZooKeeperRepository 인스턴스
	 */
	public JdbcZooKeeperRepository getRepository() {
		return jdbcRepository;
	}

	// inner util methods
	/**
	 * 입력값의 범위를 검증하여 유효한 정수를 반환합니다.
	 * 사용자가 올바른 범위의 값을 입력할 때까지 반복하여 입력을 받습니다.
	 * 
	 * @param message 입력 요청 메시지 (null 가능)
	 * @param numInfo 입력 범위 안내 메시지
	 * @param min 최솟값 (포함)
	 * @param max 최댓값 (포함)
	 * @return 검증된 정수값
	 */

	// =================================================================
	// 기존 UI 메서드들 (호환성 유지)
	// =================================================================

	/**
	 * 사육사 관리 메인 메뉴를 처리합니다.
	 * 사육사 등록, 조회, 수정, 삭제, 급여 설정 기능을 제공합니다.
	 * 
	 * <p>제공하는 메뉴:</p>
	 * <ul>
	 *   <li>사육사 등록</li>
	 *   <li>사육사 찾기 (전체, ID별, 이름별, 부서별)</li>
	 *   <li>사육사 수정 (재직여부, 업무배정여부, 위험동물관리여부)</li>
	 *   <li>사육사 삭제</li>
	 *   <li>급여지출결의서 작성</li>
	 * </ul>
	 */
	/*
	 * ZooKeeperManagement으로 콘솔을 실행시킬 수 있습니다.
	 */
	public void handleZookeeperManagement() {
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
			case 0 -> goBack(run);
			default -> wrongIndex();
			}
		}
	}

	/**
	 * 새로운 사육사를 등록합니다.
	 * 사용자로부터 사육사의 상세 정보를 입력받아 시스템에 저장합니다.
	 * 
	 * <p>입력받는 정보:</p>
	 * <ul>
	 *   <li>이름 (한글/영문 2-20자)</li>
	 *   <li>나이 (20-65세)</li>
	 *   <li>성별 (남성/여성)</li>
	 *   <li>직급 (신입사육사~동물원장)</li>
	 *   <li>부서 (포유류~교육)</li>
	 *   <li>재직 여부</li>
	 *   <li>경력 연수 (1-40년)</li>
	 *   <li>위험동물 관리 가능 여부</li>
	 *   <li>자격증 정보 (쉼표로 구분)</li>
	 * </ul>
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
				} else if (!name.matches("^[a-zA-Z]{2,20}$")) {
					UIUtil.printSeparator('━');
					System.out.println(MenuUtil.DEFAULT_PREFIX + "이름은 영문 2~20자 입니다.");
					UIUtil.printSeparator('━');
				} else {
					break;
				}
			}
			// 나이
			int age = getValidateInt("나이를 입력하세요", "20세 ~ 65세", 20, 65);
			// 성별
			int genderIndex = getValidateInt("성별을 입력하세요", "1 : 남성 , 2 : 여성", 1, 2);
			// 직책
			int rankIndex = getValidateInt("직책을 입력하세요",
					"1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장", 1, 6);
			// 부서
			int departmentIndex = getValidateInt("부서를 입력하세요",
					"1 : 포유류부서 , 2 : 조류부서 , 3 : 파충류부서 , 4 : 어류부서 , 5 : 양서류부서 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육", 1, 8);
			// 재직여부
			int isWorkingIndex = getValidateInt("재직여부를 입력하세요", "1 : 재직 , 2 : 퇴사", 1, 2);
			// 재직여부
			int experienceYear = getValidateInt("연차를 입력하세요", "1 ~ 40", 1, 40);
			// 맹수조련여부
			int canHandleDangerAnimalIndex = getValidateInt("맹수조련여부를 입력하세요", "1 : 가능 , 2 : 불가능", 1, 2);
			// 자격
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "자격증을 작성하세요. ',' 로 구분할 수 있습니다. (enter로 넘기기 가능) ▶ ");
			System.out.println();
			String desc = InputUtil.getStringInput();

			// id 생성
			// InMemory
			// String id = IdGeneratorUtil.generateId();
			String id = DatabaseIdGenerator.generateId();
			ZooKeeper zk = new ZooKeeper(id, name, age, ZooKeeperConverter.genderConverter(genderIndex),
					ZooKeeperConverter.rankConverter(rankIndex),
					ZooKeeperConverter.departmentConverter(departmentIndex),
					ZooKeeperConverter.workingConverter(isWorkingIndex), experienceYear,
					ZooKeeperConverter.possibleImpossibleConverter(canHandleDangerAnimalIndex), stringListMaker(desc));
			jdbcRepository.createZooKeeper(zk);
			UIUtil.printSeparator('━');
			System.out.println(MenuUtil.DEFAULT_PREFIX + "사육사가 등록되었습니다.");
			UIUtil.printSeparator('━');
			break;
		}
	}

	private List<String> stringListMaker(String licenses) {
		List<String> list = licenses != null && !licenses.isEmpty() ? Arrays.asList(licenses.split(","))
				: new ArrayList<>();
		return list;
	}

	/**
	 * 사육사 조회 메뉴를 표시하고 처리합니다.
	 * 다양한 조회 옵션을 제공하여 사용자가 원하는 방식으로 사육사 정보를 찾을 수 있습니다.
	 * 
	 * <p>조회 옵션:</p>
	 * <ul>
	 *   <li>전체 리스트 조회</li>
	 *   <li>ID로 개별 조회</li>
	 *   <li>이름으로 다중 조회</li>
	 *   <li>부서별 다중 조회</li>
	 * </ul>
	 */
	// 읽기
	private void getZooKeeper() {
		AtomicBoolean run = new AtomicBoolean(true);
		String[] choices = { "전체리스트 조회", "ID로 찾기(개인)", "이름으로 찾기(다수)", "부서로 찾기(다수)", "뒤로가기" };
		MenuUtil.printMenu("사육사 조회", choices);
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
	 * 등록된 모든 사육사의 목록을 출력합니다.
	 * 시스템에 등록된 전체 사육사 정보를 순서대로 표시합니다.
	 */
	private void getZooKeeperList() {
//		int length = repository.getZooKeeperList().size();
		List<ZooKeeper> zooKeeperListDB = jdbcRepository.getZooKeeperListDB();
		int length = zooKeeperListDB.size();
		String title = "사육사 리스트";
		String[] headers = { "ID", "Name", "Age", "Gender", "Rank", "Department", "IsWorking",
				"Can Handle Danger Animal" };
		String[][] data = new String[length][8];
		for (int i = 0; i < length; i++) {
			ZooKeeper zooKeeper = zooKeeperListDB.get(i);
			data[i][0] = zooKeeper.getId();
			data[i][1] = zooKeeper.getName();
			data[i][2] = zooKeeper.getAge() + "";
			data[i][3] = ZooKeeperConverter.genderStringConverter(zooKeeper.getGender());
			data[i][4] = ZooKeeperConverter.rankStringConverter(zooKeeper.getRank());
			data[i][5] = ZooKeeperConverter.departmentStringConverter(zooKeeper.getDepartment());
			data[i][6] = ZooKeeperConverter.workingStringConverter(zooKeeper.isWorking());
			data[i][7] = ZooKeeperConverter.possibleImpossibleStringConverter(zooKeeper.isCanHandleDangerAnimal());
		}
		TableUtil.printTable(title, headers, data);
	}

	/**
	 * 특정 ID로 사육사를 조회하고 출력합니다.
	 * 사용자가 입력한 ID와 일치하는 사육사의 상세 정보를 표시합니다.
	 */
	private void getZooKeeperById() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "아이디를 입력해주세요 ▶ ");
		String id = InputUtil.getStringInput();
//		ZooKeeper zk = repository.getZooKeeperById(id);
		ZooKeeper zk = jdbcRepository.getZooKeeperById(id);
		if (zk == null) {
			String noDataTitle = "데이터 없음";
			String[] noDataHeaders = { "No Data" };
			String[] noDataValues = { "No Data" };
			TableUtil.printSingleRowTable(noDataTitle, noDataHeaders, noDataValues);
		} else {
			String title = zk.getName();
			String[] headers = { "id", "name", "rank", "department", "gender", "CanHandleDangerAnimal", "IsWorking" };
			String[] values = { zk.getId(), zk.getName(), ZooKeeperConverter.rankStringConverter(zk.getRank()),
					ZooKeeperConverter.departmentStringConverter(zk.getDepartment()),
					ZooKeeperConverter.genderStringConverter(zk.getGender()),
					ZooKeeperConverter.possibleImpossibleStringConverter(zk.isCanHandleDangerAnimal()),
					ZooKeeperConverter.workingStringConverter(zk.isWorking()) };
			TableUtil.printSingleRowTable(title, headers, values);
		}
	}

	/**
	 * 특정 이름으로 사육사를 조회하고 출력합니다.
	 * 동일한 이름을 가진 여러 사육사가 있을 수 있으므로 모든 일치하는 결과를 표시합니다.
	 */
	private void getZooKeeperByName() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "이름을 입력해주세요 ▶ ");
		String name = InputUtil.getStringInput();
		List<ZooKeeper> zk = jdbcRepository.getZooKeeperByNameDB(name);
		if (zk == null) {
			String noDataTitle = "데이터 없음";
			String[] noDataHeaders = { "No Data" };
			String[] noDataValues = { "No Data" };
			TableUtil.printSingleRowTable(noDataTitle, noDataHeaders, noDataValues);
		} else {
			String title = "사육사 리스트";
			String[] headers = { "ID", "Name", "Age", "Gender", "Rank", "Department", "IsWorking",
					"Can Handle Danger Animal" };
			String[][] data = new String[zk.size()][8];
			for (int i = 0; i < zk.size(); i++) {
				ZooKeeper zooKeeper = zk.get(i);
				data[i][0] = zooKeeper.getId();
				data[i][1] = zooKeeper.getName();
				data[i][2] = zooKeeper.getAge() + "";
				data[i][3] = ZooKeeperConverter.genderStringConverter(zooKeeper.getGender());
				data[i][4] = ZooKeeperConverter.rankStringConverter(zooKeeper.getRank());
				data[i][5] = ZooKeeperConverter.departmentStringConverter(zooKeeper.getDepartment());
				data[i][6] = ZooKeeperConverter.workingStringConverter(zooKeeper.isWorking());
				data[i][7] = ZooKeeperConverter.possibleImpossibleStringConverter(zooKeeper.isCanHandleDangerAnimal());
			}
			TableUtil.printTable(title, headers, data);
		}
	}

	/**
	 * 부서별로 사육사를 조회하고 출력합니다.
	 * 사용자가 선택한 부서에 소속된 모든 사육사의 정보를 표시합니다.
	 */
	private void getZooKeeperByDepartment() {
		String[] choices = { "포유류", "조류", "파충류", "어류", "양서류", "번식/연구", "수의/재활", "교육" };
		MenuUtil.printMenu("부서를 고르세요", choices);
		int departmentIndex = InputUtil.getIntInput();
//		List<ZooKeeper> zk = repository.getZooKeeperByDepartment(departmentIndex);
		List<ZooKeeper> zk = jdbcRepository.getZookeeperByDepartmentDB(departmentIndex);
		if (zk == null) {
			String noDataTitle = "데이터 없음";
			String[] noDataHeaders = { "No Data" };
			String[] noDataValues = { "No Data" };
			TableUtil.printSingleRowTable(noDataTitle, noDataHeaders, noDataValues);
		} else {
			String title = "사육사 리스트";
			String[] headers = { "ID", "Name", "Age", "Gender", "Rank", "Department", "IsWorking",
					"Can Handle Danger Animal" };
			String[][] data = new String[zk.size()][8];
			for (int i = 0; i < zk.size(); i++) {
				ZooKeeper zooKeeper = zk.get(i);
				data[i][0] = zooKeeper.getId();
				data[i][1] = zooKeeper.getName();
				data[i][2] = zooKeeper.getAge() + "";
				data[i][3] = ZooKeeperConverter.genderStringConverter(zooKeeper.getGender());
				data[i][4] = ZooKeeperConverter.rankStringConverter(zooKeeper.getRank());
				data[i][5] = ZooKeeperConverter.departmentStringConverter(zooKeeper.getDepartment());
				data[i][6] = ZooKeeperConverter.workingStringConverter(zooKeeper.isWorking());
				data[i][7] = ZooKeeperConverter.possibleImpossibleStringConverter(zooKeeper.isCanHandleDangerAnimal());
			}
			TableUtil.printTable(title, headers, data);
		}

	}

	/**
	 * 사육사 정보 수정 메뉴를 표시하고 처리합니다.
	 * 권한에 따라 제한된 정보만 수정할 수 있으며, 수정 가능한 항목을 메뉴로 제공합니다.
	 * 
	 * <p>수정 가능한 항목:</p>
	 * <ul>
	 *   <li>재직여부 (재직/퇴사)</li>
	 *   <li>업무배정가능여부 (가능/불가능)</li>
	 *   <li>위험동물관리여부 (가능/불가능)</li>
	 * </ul>
	 */
	private void editZooKeeper() {
		AtomicBoolean run = new AtomicBoolean(true);
		String[] choice = { "재직여부", "위험군동물관리여부", "뒤로가기" };
		MenuUtil.printMenu("무엇을 수정하시겠습니까?", choice);
		int index = InputUtil.getIntInput();
		switch (index) {
		case 1 -> editIsWorking();
		case 2 -> editPermissionDangerAnimal();
		case 0 -> goBack(run);
		default -> wrongIndex();
		}
	}

	/**
	 * 사육사의 재직 여부를 수정합니다.
	 * 권한 검증을 통해 적절한 권한을 가진 사용자만 수정할 수 있습니다.
	 * 자기 자신은 언제나 수정 가능하며, 관리자 이상은 타인의 정보도 수정 가능합니다.
	 */
	private void editIsWorking() {
		IdTracker ids = getIds();
		boolean isManager = jdbcRepository.checkManager(ids.getMyId());
		if (isManager) {
			int index = getValidateInt(null, "1. 재직, 2. 퇴사", 1, 2);
			boolean success = jdbcRepository.editIsWorkingDB(ids.targetId, index);
			if (success) {
				UIUtil.printSeparator('━');
				System.out.println(
						MenuUtil.DEFAULT_PREFIX + "************************ 재직여부가 수정되었습니다. ************************");
				UIUtil.printSeparator('━');
			} else {
				UIUtil.printSeparator('━');
				System.out.println(MenuUtil.DEFAULT_PREFIX
						+ "************************ 재직여부가 수정에 실패했습니다. ************************");
				UIUtil.printSeparator('━');
			}
		} else {
			System.out.println("당신은 매니저 급이 아닙니다.");
		}
	}

	/**
	 * 사육사의 업무 배정 가능 여부를 수정합니다.
	 * 권한 검증을 통해 적절한 권한을 가진 사용자만 수정할 수 있습니다.
	 * 업무 할당과 관련된 중요한 설정이므로 신중하게 처리됩니다.
	 */
	// private void editCanAssignTask() {
	// IdTracker ids = getIds();
	// int index = getValidateInt(null, "1. 가능, 2. 불가능", 1, 2);
	// repository.setCanAssignTask(ids.getMyId(), ids.getTargetId(), index);
	// System.out.println(
	// MenuUtil.DEFAULT_PREFIX + "************************ 업무배정가능여부가 수정되었습니다.
	// ************************");
	// }

	/**
	 * 사육사의 위험동물 관리 권한을 수정합니다.
	 * 권한 검증을 통해 적절한 권한을 가진 사용자만 수정할 수 있습니다.
	 * 안전과 직결된 중요한 권한이므로 엄격하게 관리됩니다.
	 */
	private void editPermissionDangerAnimal() {
		IdTracker ids = getIds();
		boolean isManager = jdbcRepository.checkManager(ids.getMyId());
		if (isManager) {
			int index = getValidateInt(null, "1. 가능, 2. 불가능", 1, 2);
			boolean success = jdbcRepository.editPermissionDangerAnimalDB(ids.targetId, index);
			if (success) {
				UIUtil.printSeparator('━');
				System.out.println(MenuUtil.DEFAULT_PREFIX
						+ "************************ 위험동물관리여부가 수정되었습니다. ************************");
				UIUtil.printSeparator('━');
			} else {
				UIUtil.printSeparator('━');
				System.out.println(MenuUtil.DEFAULT_PREFIX
						+ "************************ 위험동물관리여부가 수정이 실패했습니다. ************************");
				UIUtil.printSeparator('━');
			}
		} else {
			System.out.println("당신은 매니저 급이 아닙니다.");
		}

	}

	/**
	 * 사육사를 시스템에서 삭제합니다.
	 * 권한 검증을 통해 적절한 권한을 가진 사용자만 삭제할 수 있습니다.
	 * 삭제된 사육사 정보는 복구할 수 없으므로 신중하게 처리됩니다.
	 */
	private void deleteZooKeeper() {
		IdTracker ids = getIds();
		boolean isManager = jdbcRepository.checkManager(ids.getMyId());
		if (isManager) {
			boolean success = jdbcRepository.deleteZooKeeperDB(ids.targetId);
			if (success) {
				UIUtil.printSeparator('━');
				System.out.println(
						MenuUtil.DEFAULT_PREFIX + "************************ 삭제되었습니다. ************************");
				UIUtil.printSeparator('━');
			} else {
				UIUtil.printSeparator('━');
				System.out.println(
						MenuUtil.DEFAULT_PREFIX + "************************ 삭제에 실패했습니다. ************************");
				UIUtil.printSeparator('━');
			}
		} else {
			System.out.println("당신은 매니저 급이 아닙니다.");
		}

	}

	/**
	 * 사육사의 급여를 설정하는 급여지출결의서를 작성합니다.
	 * FinanceManager와 연동하여 급여 정보를 처리하고, 권한 검증을 통해
	 * 적절한 권한을 가진 사용자만 급여를 설정할 수 있습니다.
	 */
	// 급여생성
	private void createSalaryService() {
		IdTracker ids = getIds();
		boolean isManager = jdbcRepository.checkManager(ids.getMyId());
		if (isManager) {
			IncomeExpend ie = FinanceManager.getInstance().useMoneyForSalary(ids.targetId);
			if (ie != null) {
				UIUtil.printSeparator('━');
				System.out.println(
						MenuUtil.DEFAULT_PREFIX + "************************ 급여가 생성되었습니다. ************************");
				UIUtil.printSeparator('━');
			} else {
				UIUtil.printSeparator('━');
				System.out.println(
						MenuUtil.DEFAULT_PREFIX + "************************ 급여 생성에 실패했습니다. ************************");
				UIUtil.printSeparator('━');
			}
		} else {
			UIUtil.printSeparator('━');
			System.out.println(
					MenuUtil.DEFAULT_PREFIX + "************************ 당신은 매니저가 아닙니다. ************************");
			UIUtil.printSeparator('━');
		}
	}

	// inner util methods
	/**
	 * 입력값의 범위를 검증하여 유효한 정수를 반환합니다.
	 * 사용자가 올바른 범위의 값을 입력할 때까지 반복하여 입력을 받습니다.
	 * 
	 * @param message 입력 요청 메시지 (null 가능)
	 * @param numInfo 입력 범위 안내 메시지
	 * @param min 최솟값 (포함)
	 * @param max 최댓값 (포함)
	 * @return 검증된 정수값
	 */
	private int getValidateInt(String message, String numInfo, int min, int max) {
		while (true) {
			if (message != null && !message.isEmpty()) {
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + numInfo);
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + message + " ▶ ");
			} else {
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + numInfo);
				System.out.println();
				System.out.println(" ▶ ");
			}
			System.out.println();
			String input = InputUtil.getStringInput();
			try {
				int value = Integer.parseInt(input);
				if (value >= min && value <= max) {
					return value;
				} else {
					System.out.println(MenuUtil.DEFAULT_PREFIX + "주어진 범위를 다시보고 입력해 주세요.");
					System.out.println();
				}
			} catch (NumberFormatException e) {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "숫자를 입력해주세요  ▶ ");
			}
		}
	}

	/**
	 * 이전 메뉴로 돌아갑니다.
	 * 현재 실행 중인 루프를 종료하여 상위 메뉴로 복귀합니다.
	 * 
	 * @param run 실행 상태를 제어하는 AtomicBoolean
	 */
	/*
	 * 뒤로가기
	 */
	private void goBack(AtomicBoolean run) {
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "뒤로 갑니다.");
		System.out.println();
		run.set(false);
	}

	/**
	 * 잘못된 메뉴 번호 입력 시 안내 메시지를 출력합니다.
	 * 사용자가 유효하지 않은 메뉴 번호를 선택했을 때 호출됩니다.
	 */
	/*
	 * 잘못된 번호 문구 리턴
	 */
	private void wrongIndex() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 번호입니다.");
	}

	/**
	 * 사육사 수정 작업에서 사용되는 ID 정보를 담는 내부 클래스입니다.
	 * 작업 수행자 ID와 작업 대상자 ID를 함께 관리하여 권한 검증 및 작업 처리를 용이하게 합니다.
	 */
	/*
	 * 사육사 수정 시 반복되는 id값을 받는것을 객체화 시켰습니다.
	 */
	// inner class
	private class IdTracker {
		private String myId;
		private String targetId;

		/**
		 * IdTracker 생성자
		 * 
		 * @param myId 작업 수행자 ID
		 * @param targetId 작업 대상자 ID
		 */
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

	/**
	 * 현재 재직 중인 사육사 목록을 반환합니다.
	 * 
	 * @return 재직 중인 사육사 리스트
	 */
	public List<ZooKeeper> getWorkingKeepers() {
		return jdbcRepository.getWorkingKeepersDB();
	}

	/**
	 * 재직 중인 사육사가 존재하는지 확인합니다.
	 * 
	 * @return 재직 중인 사육사 존재 여부
	 */
	public boolean hasWorkingKeepers() {
		return jdbcRepository.hasWorkingKeepers();
	}
}

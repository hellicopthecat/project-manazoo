package app.zooKeeper;

import java.util.Scanner;

import app.common.id.IdGeneratorUtil;

public class ZooKeeperManager {

	private final ZooKeeperRepository repository = ZooKeeperRepository.getInstance();

	/**
	 * test용 main 함수입니다.
	 */
//	public static void main(String[] args) {
//		ZooKeeperManager z = new ZooKeeperManager();
//		z.handleZookeeperManagement();
//	}

	/*
	 * ZooKeeperManagement으로 콘솔을 실행시킬 수 있습니다.
	 */
	public void handleZookeeperManagement() {
		while (true) {
			System.out.println("메뉴를 고르세요 .");
			System.out.println("1. 사육사 등록 , 2. 사육사 찾기, 3. 사육사 수정, 4. 사육사 삭제, 0. 뒤로가기");
			Scanner in = new Scanner(System.in);
			switch (Integer.parseInt(in.nextLine())) {
			case 1 -> registerZooKeeper(in);
			case 2 -> getZooKeeper(in);
			case 3 -> editZooKeeper(in);
			case 4 -> deleteZooKeeper(in);
			case 0 -> goBack();
			default -> wrongIndex();
			}
		}
	}

	/**
	 * 사육사 등록 메서드 입니다.
	 * 
	 *  @param Scanner
	 */
	private void registerZooKeeper(Scanner in) {

		while (true) {
			System.out.println("=================== 사 육 사 생 성 ===================");
			String name;
			while (true) {
				// 이름
				System.out.println("이름을 입력하세요.");
				name = in.nextLine().trim();
				if (name.isEmpty()) {
					System.out.println("""
							====================

							이름은 비워둘 수 없습니다.

							이름을 입력하세요.

							====================
							""");
				} else if (!name.matches("^[가-힣a-zA-Z]{2,20}$")) {
					System.out.println("""
							=============================

							이름은 한글 또는 영문 2~20자 입니다.

							이름을 입력하세요.

							=============================
							""");
				} else {
					break;
				}
			}
			// 나이
			int age = getValidateInt(in, "나이를 입력하세요.", "20세 ~ 65세", 20, 65);
			// 성별
			int genderIndex = getValidateInt(in, "성별을 입력하세요.", "1 : 남성 , 2 : 여성", 1, 2);
			// 직책
			int rankIndex = getValidateInt(in, "직책을 입력하세요.",
					"1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장", 1, 6);
			// 부서
			int departmentIndex = getValidateInt(in, "부서를 입력하세요.",
					"1 : 포유류부서 , 2 : 조류부서 , 3 : 파충류부서 , 4 : 어류부서 , 5 : 양서류부서 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육", 1, 8);
			// 재직여부
			int isWorkingIndex = getValidateInt(in, "재직여부를 입력하세요.", "1 : 재직 , 2 : 퇴사", 1, 2);
			// 재직여부
			int experienceYear = getValidateInt(in, "연차를 입력하세요.", "1 ~ 40", 1, 40);
			// 맹수조련여부
			int canHandleDangerAnimalIndex = getValidateInt(in, "맹수조련여부를 입력하세요.", "1 : 가능 , 2 : 불가능", 1, 2);
			// 자격
			System.out.println("자격증을 작성하세요. ',' 로 구분할 수 있습니다.");
			System.out.println("---------------");
			String desc = in.nextLine();

			// id 생성
			String id = IdGeneratorUtil.generateId();
			repository.createZooKeeper(id, name, age, genderIndex, rankIndex, departmentIndex, isWorkingIndex,
					experienceYear, canHandleDangerAnimalIndex, desc);
			System.out.println("""
					==================
					사육사가 등록되었습니다.
					==================
					""");
			break;
		}
	}

	/**
	 * 사육사 조회 메서드입니다.
	 * 
	 * @param Scanner
	 */

	// 읽기
	private void getZooKeeper(Scanner in) {
		System.out.println("어떤 방식으로 찾으시겠습니까?");
		System.out.println("1. 전체리스트 조회, 2. ID로 찾기(개인), 3. 이름으로 찾기(다수), 4. 부서로 찾기(다수), 0. 뒤로가기");
		switch (Integer.parseInt(in.nextLine())) {
		case 1 -> System.out.println(repository.getZooKeeperList());
		case 2 -> {
			System.out.println("아이디를 입력해주세요.");
			String id = in.nextLine();
			System.out.println(repository.getZooKeeperById(id));
		}
		case 3 -> {
			System.out.println("이름을 입력해주세요.");
			String name = in.nextLine();
			System.out.println(repository.getZooKeeperByName(name));
		}
		case 4 -> {
			System.out.println("부서를 고르세요.");
			System.out.println("1 : 포유류 , 2 : 조류 , 3 : 파충류 , 4 : 어류 , 5 : 양서류 , 6 : 번식/연 , 7 : 수의/재활 , 8 : 교육");
			String departmentIndex = in.nextLine();
			int index = Integer.parseInt(departmentIndex);
			System.out.println(repository.getZooKeeperByDepartment(index));
		}
		case 0 -> goBack();
		default -> wrongIndex();
		}
	}

	/**
	 * 사육사 수정 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editZooKeeper(Scanner in) {
		System.out.println("무엇을 수정하시겠습니까?");
		System.out.println("1. 재직여부, 2. 업무배정가능여부, 3. 위험군동물관리여부, 0. 뒤로가기");
		switch (Integer.parseInt(in.nextLine())) {
		case 1 -> editIsWorking(in);
		case 2 -> editCanAssignTask(in);
		case 3 -> editPermissionDangerAnimal(in);
		case 0 -> goBack();
		default -> wrongIndex();
		}
	}

	/**
	 * 사육사 수정(재직여부) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editIsWorking(Scanner in) {
		IdTracker ids = getIds(in);
		int index = getValidateInt(in, null, "1. 재직, 2. 퇴사", 1, 2);
		repository.setIsWorking(ids.getMyId(), ids.getTargetId(), index);
		System.out.println("재직여부가 수정되었습니다.");
	}

	/**
	 * 사육사 수정(업무할당) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editCanAssignTask(Scanner in) {
		IdTracker ids = getIds(in);
		int index = getValidateInt(in, null, "1. 가능, 2. 불가능", 1, 2);
		repository.setCanAssignTask(ids.getMyId(), ids.getTargetId(), index);
		System.out.println("업무배정가능여부가 수정되었습니다.");
	}

	/**
	 * 사육사 수정(고위험동물군관리여부) 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void editPermissionDangerAnimal(Scanner in) {
		IdTracker ids = getIds(in);
		int index = getValidateInt(in, null, "1. 가능, 2. 불가능", 1, 2);
		repository.setPermissionDangerAnimal(ids.getMyId(), ids.getTargetId(), index);
		System.out.println("위험동물관리여부가 수정되었습니다.");
	}

	/**
	 * 사육사 삭제 메서드입니다.
	 * 
	 * @param Scanner
	 */
	private void deleteZooKeeper(Scanner in) {
		IdTracker ids = getIds(in);
		repository.removeZooKeeper(ids.getMyId(), ids.getTargetId());
		System.out.println("삭제되었습니다.");
	}

	/**
	 * 수의 최저값과 최고값을 정하고 그 사이 int 값을 반환하는 메소드입니다.
	 * 
	 *   @param in Scanner
	 *   @param message 제목 String
	 *   @param numInfo 수의 정보를 나타내주는 String
	 *   @param min 최저값
	 *   @param max 최고값
	 */
	private int getValidateInt(Scanner in, String message, String numInfo, int min, int max) {
		while (true) {
			if (message != null && !message.isEmpty()) {
				System.out.println(message);
			}
			System.out.println("---------------------------");
			System.out.println(numInfo);
			String input = in.nextLine();
			try {
				int value = Integer.parseInt(input);
				if (value >= min && value <= max) {
					return value;
				} else {
					System.out.println(numInfo);
				}
			} catch (NumberFormatException e) {
				System.out.println("숫자를 입력해주세요.");
			}
		}
	}

	/*
	 * 뒤로가기
	 */
	private void goBack() {
		System.out.println("뒤로 갑니다.");
		return;
	}

	/*
	 * 잘못된 번호 문구 리턴
	 */
	private void wrongIndex() {
		System.out.println("잘못된 번호입니다.");
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
	private IdTracker getIds(Scanner in) {
		String myId, targetId;
		while (true) {
			System.out.println("본인 아이디를 입력하세요.");
			myId = in.nextLine();
			if (myId.matches("^K-([0-9]{4,})$")) {
				break;
			} else {
				System.out.println("잘못된 형식입니다.");
			}
		}
		while (true) {
			System.out.println("상대 아이디를 입력하세요.");
			targetId = in.nextLine();
			if (targetId.matches("^K-([0-9]{4,})$")) {
				break;
			} else {
				System.out.println("잘못된 형식입니다.");
			}
		}
		return new IdTracker(myId, targetId);
	}
}

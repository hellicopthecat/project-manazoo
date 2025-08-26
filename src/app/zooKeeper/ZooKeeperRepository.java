package app.zooKeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;

enum Species {
	MAMMAL, BIRD, REPTILE, FISH
}

public class ZooKeeperRepository {
	private static final ZooKeeperRepository instance = new ZooKeeperRepository();
	private final Map<String, ZooKeeper> repository;

	private ZooKeeperRepository() {
		this.repository = new HashMap<String, ZooKeeper>();
	}

	public static ZooKeeperRepository getInstance() {
		return instance;
	}

	public static void registerZooKeeper() {
		Scanner in = new Scanner(System.in);
		String name;
		int age;
		int genderIndex;
		int rankIndex;
		int departmentIndex;
		int isWorkingIndex;
		int canHandleDangerAnimalIndex;
		int canAssignTaskIndex;
		String desc;

		while (true) {
			System.out.println("=================== 사 육 사 생 성 ===================");
			// 이름
			System.out.println("이름을 입력하세요.");
			name = in.nextLine();
			// 나이
			System.out.println("나이를 입력하세요.");
			age = Integer.parseInt(in.nextLine());
			// 성별
			System.out.println("성별을 입력하세요.");
			System.out.println("---------------");
			System.out.println("1 : 남성 , 2 : 여성");
			genderIndex = Integer.parseInt(in.nextLine());
			// 직책
			System.out.println("직책을 입력하세요.");
			System.out.println("---------------");
			System.out.println("1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장");
			rankIndex = Integer.parseInt(in.nextLine());
			// 부서
			System.out.println("부서를 입력하세요.");
			System.out.println("---------------");
			System.out.println(
					"1 : 포유류부서 , 2 : 조류부서 , 3 : 파충류부서 , 4 : 어류부서 , 5 : 양서류부서 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육");
			departmentIndex = Integer.parseInt(in.nextLine());
			// 재직여부
			System.out.println("재직여부를 입력하세요.");
			System.out.println("---------------");
			System.out.println("1 : 재직 , 2 : 퇴사");
			isWorkingIndex = Integer.parseInt(in.nextLine());
			// 맹수조련여부
			System.out.println("맹수조련여부를 입력하세요.");
			System.out.println("---------------");
			System.out.println("1 : 가능 , 2 : 불가능");
			canHandleDangerAnimalIndex = Integer.parseInt(in.nextLine());
			// 업무할당여부
			System.out.println("업무할당여부를 입력하세요.");
			System.out.println("---------------");
			System.out.println("1 : 가능 , 2 : 불가능");
			canAssignTaskIndex = Integer.parseInt(in.nextLine());
			// 자격
			System.out.println("자격증을 작성하세요. ',' 로 구분할 수 있습니다.");
			System.out.println("---------------");
			desc = in.next();

			// type converter
			Gender gender = ZooKeeperConverter.genderConverter(genderIndex);
			ZooKeeperRank rank = ZooKeeperConverter.rankConverter(rankIndex);
			Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
			boolean isWorking = ZooKeeperConverter.workingConverter(isWorkingIndex);
			boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(canHandleDangerAnimalIndex);
			boolean canAssignTask = ZooKeeperConverter.possibleImpossibleConverter(canAssignTaskIndex);
			List<String> license = List.of(desc.trim().split(","));

//			ZooKeeper newZooKeeper = new ZooKeeper();
		}

	}

	public Map<String, ZooKeeper> getZooKeeperList() {
		return repository;
	}

	public ZooKeeper getZooKeeperById(String id) {
		ZooKeeper zooKeeper = repository.get(id);
		return zooKeeper;
	}

	public List<Entry<String, ZooKeeper>> getZooKeeperByName(String name) {
		Set<Entry<String, ZooKeeper>> zooKeeperEntrySet = repository.entrySet();

		List<Entry<String, ZooKeeper>> collect = zooKeeperEntrySet.stream()
				.filter(keeper -> keeper.getValue().getName() == name).collect(Collectors.toList());
		return collect;
	}

	public ZooKeeper getZooKeeperByAnimal(Species species) {
		return null;
	}

	public List<Entry<String, ZooKeeper>> getZooKeeperByDepartment(int departmentIndex) {
		Set<Entry<String, ZooKeeper>> zooKeeperEntrySet = repository.entrySet();
		Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
		List<Entry<String, ZooKeeper>> collect = zooKeeperEntrySet.stream()
				.filter(keeper -> keeper.getValue().getDepartment() == department).collect(Collectors.toList());
		return collect;
	}

	private boolean existManager(String id) {
		ZooKeeper manager = getZooKeeperById(id);
		boolean existManager = manager.isWorking()
				&& (manager.getRank() == ZooKeeperRank.DIRECTOR || manager.getRank() == ZooKeeperRank.MANAGER);
		return existManager;
	}

	public void setIsWorking(String keeperId, String targetId) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean isWorking = ZooKeeperConverter.workingConverter(1);
				targetZooKeeper.setIsWorking(isWorking);
			} else {
				System.out.println("매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println("존재하지 않은 회원입니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCanAssignTask(String keeperId, String targetId) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(1);
				targetZooKeeper.setCanAssignTask(canHandleDangerAnimal);
			} else {
				System.out.println("매니저가 아닙니다.");
			}
		} catch (NullPointerException e) {
			System.out.println("존재하지 않은 회원입니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setPermissionDangerAnimal(String keeperId, String targetId) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(1);
				targetZooKeeper.setCanHandleDangerAnimal(canHandleDangerAnimal);
			} else {
				System.out.println("매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println("존재하지 않은 회원입니다.");
		} catch (Exception e) {
		}

	}

	public ZooKeeper removeZooKeeper(String keeperId, String targetId) {
		// TODO Auto-generated method stub
		ZooKeeper removedZooKeeper = null;
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				removedZooKeeper = repository.remove(targetZooKeeper.getId());
			} else {
				System.out.println("매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println("존재하지 않은 회원입니다.");
		} catch (Exception e) {
		}
		return removedZooKeeper;
	}

	public boolean removeCaredAnimal(String animalId, String keeperId) {
		// TODO Auto-generated method stub
		try {

			ZooKeeper zooKeeper = getZooKeeperById(keeperId);
			if (zooKeeper.isWorking()) {

			} else {
				System.out.println("퇴사한 직원입니다.");
			}

		} catch (Exception e) {
		}
		return false;
	}
}

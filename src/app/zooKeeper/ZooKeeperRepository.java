package app.zooKeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;

// 임시 enum
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

	public void createZooKeeper(String id, String name, int age, int genderIndex, int rankIndex, int departmentIndex,
			int isWorkingIndex, int experienceYear, int canHandleDangerAnimalIndex, int canAssignTaskIndex,
			String desc) {

		// type converter
		Gender gender = ZooKeeperConverter.genderConverter(genderIndex);
		ZooKeeperRank rank = ZooKeeperConverter.rankConverter(rankIndex);
		Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
		boolean isWorking = ZooKeeperConverter.workingConverter(isWorkingIndex);
		boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(canHandleDangerAnimalIndex);
		boolean canAssignTask = ZooKeeperConverter.possibleImpossibleConverter(canAssignTaskIndex);
		// 빈배열보내주기
		List<String> licenses = new ArrayList<String>();
		if (!desc.isEmpty()) {
			String[] licenseArr = desc.split(",");
			for (String license : licenseArr) {
				licenses.add(license);
			}
		}

		ZooKeeper newZooKeeper = new ZooKeeper(id, name, age, gender, rank, department, isWorking, experienceYear,
				canHandleDangerAnimal, canAssignTask, licenses);
		repository.put(id, newZooKeeper);
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
		boolean existManager = manager.isWorking() && (manager.getRank() == ZooKeeperRank.DIRECTOR
				|| manager.getRank() == ZooKeeperRank.MANAGER || manager.getRank() == ZooKeeperRank.HEAD_KEEPER);
		return existManager;
	}

	public void setIsWorking(String keeperId, String targetId, int index) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean isWorking = ZooKeeperConverter.workingConverter(index);
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

	public void setCanAssignTask(String keeperId, String targetId, int index) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(index);
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

	public void setPermissionDangerAnimal(String keeperId, String targetId, int index) {
		// TODO Auto-generated method stub
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(index);
				targetZooKeeper.setCanHandleDangerAnimal(canHandleDangerAnimal);
			} else {
				System.out.println("매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println("존재하지 않은 회원입니다.");
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return removedZooKeeper;
	}

	// 추후 animal과 연동계획
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

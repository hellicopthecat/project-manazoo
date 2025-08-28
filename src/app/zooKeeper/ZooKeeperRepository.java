package app.zooKeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import app.common.ui.MenuUtil;
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
			int isWorkingIndex, int experienceYear, int canHandleDangerAnimalIndex, String desc) {

		// type converter
		Gender gender = ZooKeeperConverter.genderConverter(genderIndex);
		ZooKeeperRank rank = ZooKeeperConverter.rankConverter(rankIndex);
		Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
		boolean isWorking = ZooKeeperConverter.workingConverter(isWorkingIndex);
		boolean canHandleDangerAnimal = ZooKeeperConverter.possibleImpossibleConverter(canHandleDangerAnimalIndex);
		boolean canAssignTask = canHaveAssingTask(rank);
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

	public Set<Entry<String, ZooKeeper>> getZooKeeperList() {
		return repository.entrySet();
	}

	public ZooKeeper getZooKeeperById(String id) {
		ZooKeeper zooKeeper = repository.get(id);
		return zooKeeper;
	}

	public List<Entry<String, ZooKeeper>> getZooKeeperByName(String name) {
		Set<Entry<String, ZooKeeper>> zooKeeperEntrySet = repository.entrySet();

		List<Entry<String, ZooKeeper>> collect = zooKeeperEntrySet.stream()
				.filter(keeper -> keeper.getValue().getName().equals(name)).collect(Collectors.toList());
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
				&& (manager.getRank().equals(ZooKeeperRank.DIRECTOR) || manager.getRank().equals(ZooKeeperRank.MANAGER)
						|| manager.getRank().equals(ZooKeeperRank.HEAD_KEEPER));
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
				System.out.println(MenuUtil.DEFAULT_PREFIX + "당신은 퇴사자이거나 매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "존재하지 않은 회원입니다.");
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
				boolean canAssignTask = ZooKeeperConverter.possibleImpossibleConverter(index);
				targetZooKeeper.setCanAssignTask(canAssignTask);
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "당신은 퇴사자이거나 매니저가 아닙니다.");
			}
		} catch (NullPointerException e) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "존재하지 않은 회원입니다.");
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
				System.out.println(MenuUtil.DEFAULT_PREFIX + "당신은 퇴사자이거나 매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "존재하지 않은 회원입니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean setSalary(String keeperId, String targetId, long money) {
		try {
			boolean isManager = existManager(keeperId);
			if (isManager) {
				ZooKeeper targetZooKeeper = getZooKeeperById(targetId);
				targetZooKeeper.setSalary(money);
				return true;
			} else {
				System.out.println(MenuUtil.DEFAULT_PREFIX + "당신은 퇴사자이거나 매니저가 아닙니다.");
				return false;
			}

		} catch (NullPointerException e) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "존재하지 않은 회원입니다.");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
				System.out.println(MenuUtil.DEFAULT_PREFIX + "당신은 퇴사자이거나 매니저가 아닙니다.");
			}

		} catch (NullPointerException e) {
			System.out.println(MenuUtil.DEFAULT_PREFIX + "존재하지 않은 회원입니다.");
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
				System.out.println(MenuUtil.DEFAULT_PREFIX + "퇴사한 직원입니다.");
			}

		} catch (Exception e) {
		}
		return false;
	}

	// util methods
	private boolean canHaveAssingTask(ZooKeeperRank rank) {
		if (rank.equals(ZooKeeperRank.DIRECTOR) || rank.equals(ZooKeeperRank.MANAGER)
				|| rank.equals(ZooKeeperRank.HEAD_KEEPER)) {
			return true;
		} else {
			return false;
		}
	}
}

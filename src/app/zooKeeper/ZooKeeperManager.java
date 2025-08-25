package app.zooKeeper;

import java.util.List;
import java.util.Map;

import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperInterface.ZooKeeperManagerService;

enum Species {
	MAMMAL, BIRD, REPTILE, FISH
}

public class ZooKeeperManager implements ZooKeeperManagerService {
	public Map<String, ZooKeeper> repository;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerZooKeeper() {

	}

	public ZooKeeper getZooKeeperById(String id) {
		return null;
	}

	public ZooKeeper getZooKeeperByName(String name) {
		return null;
	}

	public List<ZooKeeper> getZooKeeperList() {
		return null;
	}

	public ZooKeeper getZooKeeperByAnimal(Species species) {
		return null;
	}

	public List<ZooKeeper> getZooKeeperByDepartment(Department department) {
		return null;
	}

	@Override
	public void setIsWorking(String keeperId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIsWorkingByName(String keeperName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCanAssignTask(String keeperId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCanAssignTaskByName(String keeperName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPermissionDangerAnimal(String keeperId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPermissionDangerAnimalByName(String keeperName) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeZooKeeper(String keeperId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeCaredAnimal(String animalId, String keeperId) {
		// TODO Auto-generated method stub
		return false;
	}
}

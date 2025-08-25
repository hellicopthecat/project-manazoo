package app.zooKeeper.zooKeeperInterface;

import app.zooKeeper.ZooKeeper;

public interface ZooKeeperManagerService {
	void registerZooKeeper(ZooKeeper keeper);

	void setIsWorking(String keeperId);

	void setIsWorkingByName(String keeperName);

	void setCanAssignTask(String keeperId);

	void setCanAssignTaskByName(String keeperName);

	void setPermissionDangerAnimal(String keeperId);

	void setPermissionDangerAnimalByName(String keeperName);

	boolean removeZooKeeper(String keeperId);

	boolean removeCaredAnimal(String animalId, String keeperId);

}

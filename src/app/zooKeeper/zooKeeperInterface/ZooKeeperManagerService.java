package app.zooKeeper.zooKeeperInterface;

public interface ZooKeeperManagerService {
	void registerZooKeeper();

	void setIsWorking(String keeperId);

	void setIsWorkingByName(String keeperName);

	void setCanAssignTask(String keeperId);

	void setCanAssignTaskByName(String keeperName);

	void setPermissionDangerAnimal(String keeperId);

	void setPermissionDangerAnimalByName(String keeperName);

	boolean removeZooKeeper(String keeperId);

	boolean removeCaredAnimal(String animalId, String keeperId);

}

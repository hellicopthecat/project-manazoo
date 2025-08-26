package app.zooKeeper.zooKeeperInterface;

public interface ZooKeeperManagerService {
	void registerZooKeeper();

	void setCanAssignTask(String keeperId, String targetId);

	void setPermissionDangerAnimal(String keeperId, String targetId);

	boolean removeZooKeeper(String keeperId, String targetId);

	boolean removeCaredAnimal(String animalId, String keeperId);

}

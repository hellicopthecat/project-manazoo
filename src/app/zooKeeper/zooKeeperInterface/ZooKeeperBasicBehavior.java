package app.zooKeeper.zooKeeperInterface;

public interface ZooKeeperBasicBehavior {
	public void feedAnimal(String animalId);

	public void checkAnimalStatus(String animalId);

	public void checkEnclosureStatus(String enclosureId);

	public void cleanEnclosure(String enclosureId);

}

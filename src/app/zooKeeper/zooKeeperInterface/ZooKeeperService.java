package app.zooKeeper.zooKeeperInterface;

public interface ZooKeeperService {
	void addCaredAnimal(String animalId, String enclosureId);

	// List<Animal> getManagedAnimal();

	// List<Enclosure> getManagedEnclosure();

	void removeCaredAnimal(String animalId, String enclosureId);
}

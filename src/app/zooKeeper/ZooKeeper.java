package app.zooKeeper;

import java.util.List;

import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;

abstract public class ZooKeeper {
	// fields
	private String id;
	public String name;
	public int age;
	public Gender gender;
	public ZooKeeperRank rank;
	public boolean isWorking;
	public int experieneceYear;
	public boolean canHandleDangerAnimal;
	public boolean canAssignTask;
	public List<String> licenses;
	// public List<Animal> caredAnimals ;
	// public List<Enclosures> enclosures;

	// constructor

	// methods
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public ZooKeeperRank getRank() {
		return rank;
	}

	public void setRank(ZooKeeperRank rank) {
		this.rank = rank;
	}

	public boolean isWorking() {
		return isWorking;
	}

	public void setWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}

	public int getExperieneceYear() {
		return experieneceYear;
	}

	public void setExperieneceYear(int experieneceYear) {
		this.experieneceYear = experieneceYear;
	}

	public boolean isCanHandleDangerAnimal() {
		return canHandleDangerAnimal;
	}

	public void setCanHandleDangerAnimal(boolean canHandleDangerAnimal) {
		this.canHandleDangerAnimal = canHandleDangerAnimal;
	}

	public boolean isCanAssignTask() {
		return canAssignTask;
	}

	public void setCanAssignTask(boolean canAssignTask) {
		this.canAssignTask = canAssignTask;
	}

	public List<String> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<String> licenses) {
		this.licenses = licenses;
	}

	// abstract methods
	abstract public void feedAnimal(String animalId);

	abstract public void checkAnimalStatus(String animalId);

	abstract public void checkEnclosureStatus(String enclosureId);

	abstract public void cleanEnclosure(String enclosureId);

	abstract public void addLicenses(String license);
}

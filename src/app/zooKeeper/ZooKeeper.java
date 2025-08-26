package app.zooKeeper;

import java.util.ArrayList;
import java.util.List;

import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;
import app.zooKeeper.zooKeeperInterface.ZooKeeperBasicBehavior;

public class ZooKeeper implements ZooKeeperBasicBehavior {
	// fields
	private String id;
	private String name;
	private int age;
	private Gender gender;
	private ZooKeeperRank rank;
	private Department department;
	private boolean isWorking;
	private int experieneceYear;
	private boolean canHandleDangerAnimal;
	private boolean canAssignTask;
	private List<String> licenses = new ArrayList<>();
	// public List<Animal> caredAnimals ;
	// public List<Enclosures> enclosures;

	// constructor
	public ZooKeeper(String id, String name, int age, Gender gender, ZooKeeperRank rank, Department department,
			boolean isWorking, int experieneceYear, boolean canHandleDangerAnimal, boolean canAssignTask,
			List<String> licenses) {

		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.rank = rank;
		this.department = department;
		this.isWorking = isWorking;
		this.experieneceYear = experieneceYear;
		this.canHandleDangerAnimal = canHandleDangerAnimal;
		this.canAssignTask = canAssignTask;
		this.licenses = licenses;
	}

	// methods
	protected String getId() {
		return id;
	}

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

	public void setGender(int index) {
		Gender gender = ZooKeeperConverter.genderConverter(index);
		this.gender = gender;
	}

	public ZooKeeperRank getRank() {
		return rank;
	}

	public void setRank(int index) {
		ZooKeeperRank rank = ZooKeeperConverter.rankConverter(index);
		this.rank = rank;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(int index) {
		Department department = ZooKeeperConverter.departmentConverter(index);
		this.department = department;
	}

	public boolean isWorking() {
		return isWorking;
	}

	protected void setIsWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}

	public int getExperieneceYear() {
		return experieneceYear;
	}

	protected void setExperieneceYear(int experieneceYear) {
		this.experieneceYear = experieneceYear;
	}

	public boolean isCanHandleDangerAnimal() {
		return canHandleDangerAnimal;
	}

	protected void setCanHandleDangerAnimal(boolean canHandleDangerAnimal) {
		this.canHandleDangerAnimal = canHandleDangerAnimal;
	}

	public boolean isCanAssignTask() {
		return canAssignTask;
	}

	protected void setCanAssignTask(boolean canAssignTask) {
		this.canAssignTask = canAssignTask;
	}

	public List<String> getLicenses() {
		return licenses;
	}

	public void setLicenses(String licenses) {
		this.licenses.add(licenses);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String g = ZooKeeperConverter.genderStringConverter(gender);
		String r = ZooKeeperConverter.rankStringConverter(rank);
		String d = ZooKeeperConverter.departmentStringConverter(department);
		String w = ZooKeeperConverter.workingStringConverter(isWorking);
		String da = ZooKeeperConverter.possibleImpossibleStringConverter(canHandleDangerAnimal);
		String at = ZooKeeperConverter.possibleImpossibleStringConverter(canAssignTask);
		String licensesStr = licenses.isEmpty() ? "없음" : String.join(", ", licenses);

		return String.format(
				"""
						----------------------------------------------------------------------------------------------------------------------------------------------
						id : %s | 이름 : %s | 나이 : %d | 성별 : %s | 직급 : %s | 부서 : %s | 재직여부 : %s | 연차 : %d | 고위험군생물관리 : %s | 업무부여 : %s | 자격증 : %s
						----------------------------------------------------------------------------------------------------------------------------------------------
						""",
				id, name, age, g, r, d, w, experieneceYear, da, at, licensesStr);
	}

	/**
	 * pr후 animal과 enclosure 받고 작업진행 예
	 */
	@Override
	public void feedAnimal(String animalId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkAnimalStatus(String animalId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkEnclosureStatus(String enclosureId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanEnclosure(String enclosureId) {
		// TODO Auto-generated method stub

	}

}

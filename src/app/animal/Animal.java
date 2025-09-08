package app.animal;

import app.common.ui.TableUtil;

public class Animal {
	private String id;
	private String name;
	private String species;
	private int age;
	private String gender;
	private String healthStatus;
	private String enclosureId; // 케이지 아이디

	public Animal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {
		this.id = id;
		this.name = name;
		this.species = species;
		this.age = age;
		this.gender = gender;
		this.healthStatus = healthStatus;
		this.enclosureId = enclosureId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSpecies() {
		return species;
	}

	public int getAge() {
		return age;
	}

	public String getGender() {
		return gender;
	}

	public String getHealthStatus() {
		return healthStatus;
	}

	public String getEnclosureId() {
		return enclosureId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	public void setEnclosureId(String enclosureId) {
		this.enclosureId = enclosureId;
	}

	@Override
	public String toString() {
		return String.format("%s : %s / %s / %d / %s / %s / %s", id, name, species, age, gender, healthStatus,
				enclosureId);

	}

	public void showAnimal() {
		String[] headers = { "Animal ID", "Name", "Species", "Age", "Gender", "HealthStatus", "EnclosureId" };
		String[][] data = { { id, name, species, Integer.toString(age), gender, healthStatus, enclosureId } };
		TableUtil.printTable(" ", headers, data);
	}

}

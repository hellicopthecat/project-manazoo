package app.enclosure;

public class Enclosure implements EnclosureInterface {
	private String id;
	private String name;
	private Float areaSize;
	private Float temperature;
	private LocationType locationType;
	private EnvironmentType environmentType;

	@Override
	public EnclosureRepository getRepository() {
		return EnclosureRepository.getInstance();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public LocationType getLocationType() {
		return locationType;
	}

	@Override
	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	@Override
	public EnvironmentType getEnvironmentType() {
		return environmentType;
	}

	@Override
	public void setEnvironmentType(EnvironmentType environmentType) {
		this.environmentType = environmentType;
	}

	@Override
	public Float getAreaSize() {
		return areaSize;
	}

	@Override
	public void setAreaSize(Float size) {
		this.areaSize = size;
	}

	@Override
	public Float getTemperature() {
		return temperature;
	}

	@Override
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	private Enclosure() {
	}

	public Enclosure(String id, String name, Float areaSize, Float temperature, LocationType locationType,
			EnvironmentType environmentType) {
		this.id = id;
		this.name = name;
		this.areaSize = areaSize;
		this.temperature = temperature;
		this.locationType = locationType;
		this.environmentType = environmentType;
	}

	@Override
	public String toString() {
		return String.format(
				"id: '%s', name: '%s', areaSize: %.1f㎡, temperature: %.1f°C, locationType=%s, environmentType=%s", id,
				name, areaSize, temperature, locationType, environmentType);
	}
}

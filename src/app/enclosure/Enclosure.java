package app.enclosure;

import java.util.HashMap;
import java.util.Map;

import app.repository.MemoryEnclosureRepository;
import app.repository.interfaces.EnclosureRepository;

public class Enclosure implements EnclosureInterface {
	private String id;
	private String name;
	private Float areaSize;
	private Float temperature;
	private LocationType locationType;
	private EnvironmentType environmentType;
	private int maxCapacity;  // 최대 수용 인원
	
	/**
	 * 이 사육장에 거주하는 동물들을 저장하는 Map입니다.
	 * Key: 동물 ID, Value: 동물 객체
	 */
	private final Map<String, Object> inhabitants;
	
	/**
	 * 이 사육장을 담당하는 사육사들을 저장하는 Map입니다.
	 * Key: 사육사 ID, Value: 사육사 객체
	 */
	private final Map<String, Object> caretakers;

	/**
	 * Repository 패턴을 적용한 데이터 접근 계층입니다.
	 * MemoryEnclosureRepository를 사용하여 사육장 데이터를 관리합니다.
	 */
	private static final EnclosureRepository repository = new MemoryEnclosureRepository();

	/**
	 * Repository 인스턴스를 반환합니다.
	 * 
	 * @return EnclosureRepository 인스턴스
	 */
	@Override
	public EnclosureRepository getRepository() {
		return repository;
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

	// ==================== 동물(거주자) 관리 메서드 ====================
	
	/**
	 * 사육장에 동물을 입주시키는 메서드입니다.
	 * 
	 * @param animalId 동물 ID
	 * @param animal 동물 객체
	 */
	public void addInhabitant(String animalId, Object animal) {
		this.inhabitants.put(animalId, animal);
	}

	/**
	 * 사육장에서 동물을 이주시키는 메서드입니다.
	 * 
	 * @param animalId 동물 ID
	 * @return 이주된 동물 객체
	 */
	public Object removeInhabitant(String animalId) {
		return this.inhabitants.remove(animalId);
	}

	/**
	 * 특정 동물을 조회하는 메서드입니다.
	 * 
	 * @param animalId 동물 ID
	 * @return 동물 객체 (없으면 null)
	 */
	public Object getInhabitant(String animalId) {
		return this.inhabitants.get(animalId);
	}

	/**
	 * 사육장의 모든 거주 동물을 조회하는 메서드입니다.
	 * 
	 * @return 동물 Map의 방어적 복사본
	 */
	public Map<String, Object> getAllInhabitants() {
		return new HashMap<>(inhabitants);
	}

	/**
	 * 사육장에 거주하는 동물 수를 반환하는 메서드입니다.
	 * 
	 * @return 동물 수
	 */
	public int getInhabitantCount() {
		return inhabitants.size();
	}

	/**
	 * 특정 동물이 이 사육장에 거주하는지 확인하는 메서드입니다.
	 * 
	 * @param animalId 동물 ID
	 * @return 거주 여부
	 */
	public boolean hasInhabitant(String animalId) {
		return inhabitants.containsKey(animalId);
	}

	/**
	 * 사육장에 동물이 없는지 확인하는 메서드입니다.
	 * 
	 * @return 빈 사육장 여부
	 */
	public boolean isEmptyOfInhabitants() {
		return inhabitants.isEmpty();
	}

	// ==================== 사육사(관리자) 관리 메서드 ====================
	
	/**
	 * 사육장에 사육사를 배정하는 메서드입니다.
	 * 
	 * @param keeperId 사육사 ID
	 * @param keeper 사육사 객체
	 */
	public void assignCaretaker(String keeperId, Object keeper) {
		this.caretakers.put(keeperId, keeper);
	}

	/**
	 * 사육장에서 사육사 배정을 해제하는 메서드입니다.
	 * 
	 * @param keeperId 사육사 ID
	 * @return 배정 해제된 사육사 객체
	 */
	public Object unassignCaretaker(String keeperId) {
		return this.caretakers.remove(keeperId);
	}

	/**
	 * 특정 사육사를 조회하는 메서드입니다.
	 * 
	 * @param keeperId 사육사 ID
	 * @return 사육사 객체 (없으면 null)
	 */
	public Object getCaretaker(String keeperId) {
		return this.caretakers.get(keeperId);
	}

	/**
	 * 사육장에 배정된 모든 사육사를 조회하는 메서드입니다.
	 * 
	 * @return 사육사 Map의 방어적 복사본
	 */
	public Map<String, Object> getAllCaretakers() {
		return new HashMap<>(caretakers);
	}

	/**
	 * 사육장에 배정된 사육사 수를 반환하는 메서드입니다.
	 * 
	 * @return 사육사 수
	 */
	public int getCaretakerCount() {
		return caretakers.size();
	}

	/**
	 * 특정 사육사가 이 사육장에 배정되어 있는지 확인하는 메서드입니다.
	 * 
	 * @param keeperId 사육사 ID
	 * @return 배정 여부
	 */
	public boolean hasCaretaker(String keeperId) {
		return caretakers.containsKey(keeperId);
	}

	/**
	 * 사육장에 배정된 사육사가 없는지 확인하는 메서드입니다.
	 * 
	 * @return 사육사 없음 여부
	 */
	public boolean isUnmanned() {
		return caretakers.isEmpty();
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

	/**
	 * 사육장의 최대 수용 인원을 반환합니다.
	 * 
	 * @return 최대 수용 인원
	 */
	public int getMaxCapacity() {
		return maxCapacity;
	}

	/**
	 * 사육장의 최대 수용 인원을 설정합니다.
	 * 
	 * @param maxCapacity 최대 수용 인원
	 */
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	/**
	 * 사육장의 현재 수용 인원을 반환합니다.
	 * 
	 * @return 현재 수용 인원 (거주 동물 수)
	 */
	public int getCurrentCapacity() {
		return getInhabitantCount();
	}

	/**
	 * 기본 생성자 - 최대 수용 인원을 기본값(10)으로 설정합니다.
	 * 
	 * @param id 사육장 ID
	 * @param name 사육장 이름
	 * @param areaSize 면적 크기
	 * @param temperature 온도
	 * @param locationType 위치 타입
	 * @param environmentType 환경 타입
	 */
	public Enclosure(String id, String name, Float areaSize, Float temperature, LocationType locationType,
			EnvironmentType environmentType) {
		this.id = id;
		this.name = name;
		this.areaSize = areaSize;
		this.temperature = temperature;
		this.locationType = locationType;
		this.environmentType = environmentType;
		this.maxCapacity = 10; // 기본값 설정
		this.inhabitants = new HashMap<>();
		this.caretakers = new HashMap<>();
	}

	/**
	 * 최대 수용 인원을 지정하는 생성자입니다.
	 * 
	 * @param id 사육장 ID
	 * @param name 사육장 이름
	 * @param areaSize 면적 크기
	 * @param temperature 온도
	 * @param locationType 위치 타입
	 * @param environmentType 환경 타입
	 * @param maxCapacity 최대 수용 인원
	 */
	public Enclosure(String id, String name, Float areaSize, Float temperature, LocationType locationType,
			EnvironmentType environmentType, int maxCapacity) {
		this.id = id;
		this.name = name;
		this.areaSize = areaSize;
		this.temperature = temperature;
		this.locationType = locationType;
		this.environmentType = environmentType;
		this.maxCapacity = maxCapacity;
		this.inhabitants = new HashMap<>();
		this.caretakers = new HashMap<>();
	}

	@Override
	public String toString() {
		return String.format(
				"id: '%s', name: '%s', areaSize: %.1f㎡, temperature: %.1f°C, locationType=%s, environmentType=%s, " +
				"maxCapacity: %d명, inhabitants: %d마리, caretakers: %d명", 
				id, name, areaSize, temperature, locationType, environmentType, 
				maxCapacity, getInhabitantCount(), getCaretakerCount());
	}
}

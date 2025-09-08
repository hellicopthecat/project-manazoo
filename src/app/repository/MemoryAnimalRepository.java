package app.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import app.animal.Animal;
import app.repository.interfaces.AnimalRepository;

/**
 * 메모리 기반 동물 Repository 구현체입니다. 동물 데이터를 Map에 저장하여 빠른 조회와 조작을 제공합니다.
 * 
 * <p>
 * 주요 특징:
 * </p>
 * <ul>
 * <li>메모리 기반 데이터 저장</li>
 * <li>동물 배치 관리 기능</li>
 * <li>타입 안전성 확보</li>
 * <li>기존 AnimalManager와의 완전 호환</li>
 * </ul>
 */
public class MemoryAnimalRepository implements AnimalRepository {

	/**
	 * 동물 데이터를 저장하는 Map Key: 동물 ID (String), Value: 동물 객체 (Animal)
	 */
	private final Map<String, Animal> animals;

	/**
	 * 생성자 - 빈 저장소로 초기화합니다.
	 */
	public MemoryAnimalRepository() {
		this.animals = new HashMap<>();
	}

	// =================================================================
	// Repository<Animal, String> 기본 CRUD 구현
	// =================================================================

	/**
	 * 동물 객체를 저장소에 저장합니다.
	 * 
	 * @param animal 저장할 동물 객체
	 * @return 저장된 동물 객체
	 * @throws NullPointerException 동물 객체나 ID가 null인 경우
	 */
	@Override
	public Animal save(Animal animal) {
		Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
		Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");

		animals.put(animal.getId(), animal);
		return animal;
	}

	/**
	 * ID를 통해 동물을 조회합니다.
	 * 
	 * @param id 조회할 동물의 고유 식별자
	 * @return 조회된 동물 객체 (Optional로 래핑됨)
	 */
	@Override
	public Optional<Animal> findById(String id) {
		if (id == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(animals.get(id));
	}

	/**
	 * 저장소에 있는 모든 동물을 조회합니다.
	 * 
	 * @return 모든 동물의 리스트
	 */
	@Override
	public List<Animal> findAll() {
		return animals.values().stream().collect(Collectors.toList());
	}

	/**
	 * 기존 동물의 정보를 업데이트합니다.
	 * 
	 * @param animal 업데이트할 동물 객체
	 * @return 업데이트된 동물 객체
	 * @throws IllegalArgumentException 수정하려는 동물이 존재하지 않는 경우
	 * @throws NullPointerException     동물 객체나 ID가 null인 경우
	 */
	@Override
	public Animal update(Animal animal) {
		Objects.requireNonNull(animal, "동물 객체는 null일 수 없습니다.");
		Objects.requireNonNull(animal.getId(), "동물 ID는 null일 수 없습니다.");

		if (!existsById(animal.getId())) {
			throw new IllegalArgumentException("수정하려는 동물이 존재하지 않습니다: " + animal.getId());
		}

		animals.put(animal.getId(), animal);
		return animal;
	}

	/**
	 * ID를 통해 동물을 삭제합니다.
	 * 
	 * @param id 삭제할 동물의 고유 식별자
	 * @return 삭제 성공 여부
	 */
	@Override
	public boolean deleteById(String id) {
		if (id == null) {
			return false;
		}

		Animal removed = animals.remove(id);
		return removed != null;
	}

	/**
	 * 특정 ID의 동물이 존재하는지 확인합니다.
	 * 
	 * @param id 확인할 동물의 고유 식별자
	 * @return 존재 여부
	 */
	@Override
	public boolean existsById(String id) {
		return id != null && animals.containsKey(id);
	}

	/**
	 * 특정 ID의 동물이 존재하는지 확인합니다.
	 * 
	 * @param id 확인할 동물의 고유 식별자
	 * @return 존재 여부
	 */
	public boolean existsByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			return false;
		}
		return animals.values().stream().anyMatch(animal -> name.equals(animal.getName()));
	}

	/**
	 * 저장소의 모든 동물을 삭제합니다.
	 */
	@Override
	public void deleteAll() {
		animals.clear();
	}

	/**
	 * 저장소에 있는 동물의 총 개수를 반환합니다.
	 * 
	 * @return 동물 개수
	 */
	@Override
	public long count() {
		return animals.size();
	}

	// =================================================================
	// AnimalRepository 특화 메서드 구현
	// =================================================================

	/**
	 * 새로운 동물을 생성하여 저장소에 추가합니다.
	 * 
	 * @param id           동물 ID
	 * @param name         동물 이름
	 * @param species      종류
	 * @param age          나이
	 * @param gender       성별
	 * @param healthStatus 건강 상태
	 * @param enclosureId  사육장 ID
	 * @return 생성된 동물 객체
	 */
	@Override
	public Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId) {

		Animal animal = new Animal(id, name, species, age, gender, healthStatus, enclosureId);
		return save(animal);
	}

	/**
	 * 저장소의 모든 동물 목록을 반환합니다.
	 * 
	 * @return 전체 동물 목록
	 */
	@Override
	public List<Animal> getAnimalList() {
		return findAll();
	}

	/**
	 * 특정 ID의 동물을 조회합니다.
	 * 
	 * @param id 동물 ID
	 * @return 조회된 동물 객체 (없으면 null)
	 */
	@Override
	public Animal getAnimalById(String id) {
		return findById(id).orElse(null);
	}

	/**
	 * 특정 이름을 가진 동물들을 조회합니다.
	 * 
	 * @param name 동물 이름
	 * @return 해당 이름의 동물 목록
	 */
	@Override
	public List<Animal> getAnimalsByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			return List.of();
		}

		return animals.values().stream().filter(animal -> name.equals(animal.getName())).collect(Collectors.toList());
	}

	/**
	 * 특정 종의 동물들을 조회합니다.
	 * 
	 * @param species 동물 종류
	 * @return 해당 종의 동물 목록
	 */
	@Override
	public List<Animal> getAnimalsBySpecies(String species) {
		if (species == null || species.trim().isEmpty()) {
			return List.of();
		}

		return animals.values().stream().filter(animal -> species.equals(animal.getSpecies()))
				.collect(Collectors.toList());
	}

	/**
	 * 특정 동물의 정보를 업데이트합니다.
	 * 
	 * @param animalId 수정할 동물 ID
	 * @param animal   수정된 동물 객체
	 * @return 수정된 동물 객체
	 * @throws IllegalArgumentException 수정하려는 동물이 존재하지 않는 경우
	 */
	@Override
	public Animal updateAnimal(String animalId, Animal animal) {
		if (!existsById(animalId)) {
			throw new IllegalArgumentException("수정하려는 동물이 존재하지 않습니다: " + animalId);
		}

		return update(animal);
	}

	/**
	 * 특정 ID의 동물을 삭제합니다.
	 * 
	 * @param animalId 삭제할 동물 ID
	 * @return 삭제 성공 여부
	 */
	@Override
	public boolean removeAnimal(String animalId) {
		return deleteById(animalId);
	}

	// =================================================================
	// 사육장 배치 관리 메서드
	// =================================================================

	/**
	 * 배치 가능한 동물(사육장이 미배정된 동물)이 있는지 확인합니다. enclosureId가 null이거나 빈 문자열인 동물이 배치 가능한
	 * 동물로 간주됩니다.
	 * 
	 * @return 배치 가능한 동물 존재 여부
	 */
	@Override
	public boolean hasAvailableAnimals() {
		return animals.values().stream()
				.anyMatch(animal -> animal.getEnclosureId() == null || animal.getEnclosureId().trim().isEmpty());
	}

	/**
	 * 배치 가능한 동물들의 목록을 반환합니다. enclosureId가 null이거나 빈 문자열인 동물들을 필터링하여 반환합니다.
	 * 
	 * @return 배치 가능한 동물들의 Map (Key: Animal ID, Value: Animal 객체)
	 */
	@Override
	public Map<String, Animal> getAvailableAnimals() {
		return animals.entrySet().stream().filter(entry -> {
			String enclosureId = entry.getValue().getEnclosureId();
			return enclosureId == null || enclosureId.trim().isEmpty();
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	/**
	 * 배치 가능한 동물들의 작업용 복사본을 반환합니다. Working Data Pattern을 적용하여 원본 데이터를 수정하지 않고 작업할 수
	 * 있도록 합니다.
	 * 
	 * @return 배치 가능한 동물들의 복사본 Map
	 */
	@Override
	public Map<String, Animal> getWorkingCopyOfAvailableAnimals() {
		Map<String, Animal> availableAnimals = getAvailableAnimals();
		return new HashMap<>(availableAnimals);
	}

	/**
	 * 전체 동물 목록에서 특정 ID의 동물을 검색합니다.
	 * 
	 * @param animalId 검색할 동물 ID
	 * @return Optional<Animal> 검색된 동물 (없으면 empty)
	 */
	@Override
	public Optional<Animal> getAnimalFromAll(String animalId) {
		return Optional.ofNullable(animals.get(animalId));
	}

	/**
	 * 배치 가능한 상태의 동물을 특정 사육장에 배치합니다. 동물의 enclosureId를 설정하여 배치된 상태로 변경합니다.
	 * 
	 * @param animalId    배치할 동물 ID
	 * @param enclosureId 배치할 사육장 ID
	 * @return 배치된 동물 객체 (배치 불가능하면 null)
	 */
	@Override
	public Animal removeAvailableAnimal(String animalId, String enclosureId) {
		Animal animal = animals.get(animalId);
		if (animal != null) {
			String currentEnclosureId = animal.getEnclosureId();
			if (currentEnclosureId == null || currentEnclosureId.trim().isEmpty()) {
				animal.setEnclosureId(enclosureId);
				return animal;
			}
		}
		return null;
	}

	/**
	 * 동물을 사육장에서 해제하여 다시 배치 가능한 상태로 만듭니다. 동물의 enclosureId를 null로 설정합니다.
	 * 
	 * @param animalId 해제할 동물 ID
	 * @return 해제된 동물 객체 (동물이 없으면 null)
	 */
	@Override
	public Animal releaseAnimalFromEnclosure(String animalId) {
		Animal animal = animals.get(animalId);
		if (animal != null) {
			animal.setEnclosureId(null);
			return animal;
		}
		return null;
	}

	/**
	 * 동물이 특정 사육장에 배치되어 있는지 확인합니다.
	 * 
	 * @param animalId    확인할 동물 ID
	 * @param enclosureId 확인할 사육장 ID
	 * @return 해당 사육장에 배치되어 있으면 true
	 */
	@Override
	public boolean isAnimalInEnclosure(String animalId, String enclosureId) {
		Animal animal = animals.get(animalId);
		if (animal != null) {
			String animalEnclosureId = animal.getEnclosureId();
			return enclosureId.equals(animalEnclosureId);
		}
		return false;
	}

	/**
	 * Repository의 문자열 표현을 반환합니다. 현재 저장된 동물의 개수 정보를 포함합니다.
	 * 
	 * @return Repository 정보 문자열
	 */
	@Override
	public String toString() {
		return String.format("MemoryAnimalRepository{size=%d}", count());
	}
}

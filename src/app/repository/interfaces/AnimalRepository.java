package app.repository.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import app.animal.Animal;

/**
 * Animal 엔티티를 위한 특화된 Repository 인터페이스입니다. 기본 CRUD 연산 외에 Animal 도메인 특화 기능을
 * 제공합니다.
 * 
 * <p>
 * 주요 기능:
 * </p>
 * <ul>
 * <li>동물 등록 및 기본 관리</li>
 * <li>다양한 조회 옵션 (ID, 이름, 종별)</li>
 * <li>사육장 배치 관리</li>
 * <li>동물 정보 수정</li>
 * </ul>
 */
public interface AnimalRepository extends Repository<Animal, String> {

	/**
	 * 새로운 동물을 생성합니다.
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
	Animal createAnimal(String id, String name, String species, int age, String gender, String healthStatus,
			String enclosureId);

	/**
	 * 모든 동물 목록을 조회합니다.
	 * 
	 * @return 전체 동물 목록
	 */
	List<Animal> getAnimalList();

	/**
	 * ID로 동물을 조회합니다.
	 * 
	 * @param id 동물 ID
	 * @return 조회된 동물 객체 (없으면 null)
	 */
	Animal getAnimalById(String id);

	/**
	 * 이름으로 동물을 조회합니다.
	 * 
	 * @param name 동물 이름
	 * @return 해당 이름의 동물 목록
	 */
	List<Animal> getAnimalsByName(String name);

	/**
	 * 종별로 동물을 조회합니다.
	 * 
	 * @param species 동물 종류
	 * @return 해당 종의 동물 목록
	 */
	List<Animal> getAnimalsBySpecies(String species);

	/**
	 * 동물 정보를 수정합니다.
	 * 
	 * @param animalId 수정할 동물 ID
	 * @param animal   수정된 동물 객체
	 * @return 수정된 동물 객체
	 */
	Animal updateAnimal(String animalId, Animal animal);

	/**
	 * 동물을 삭제합니다.
	 * 
	 * @param animalId 삭제할 동물 ID
	 * @return 삭제 성공 여부
	 */
	boolean removeAnimal(String animalId);

	/**
	 * 배치 가능한 동물이 있는지 확인합니다.
	 * 
	 * @return 배치 가능한 동물 존재 여부
	 */
	boolean hasAvailableAnimals();

	/**
	 * 배치 가능한 동물들의 목록을 반환합니다.
	 * 
	 * @return 배치 가능한 동물들의 Map
	 */
	Map<String, Animal> getAvailableAnimals();

	/**
	 * 배치 가능한 동물들의 작업용 복사본을 반환합니다.
	 * 
	 * @return 배치 가능한 동물들의 복사본 Map
	 */
	Map<String, Animal> getWorkingCopyOfAvailableAnimals();

	/**
	 * 전체 동물 목록에서 특정 ID의 동물을 검색합니다.
	 * 
	 * @param animalId 검색할 동물 ID
	 * @return Optional<Animal> 검색된 동물 (없으면 empty)
	 */
	Optional<Animal> getAnimalFromAll(String animalId);

	/**
	 * 특정 동물을 배치 가능한 상태에서 제거합니다.
	 * 
	 * @param animalId    배치할 동물 ID
	 * @param enclosureId 배치할 사육장 ID
	 * @return 배치된 동물 객체 (없으면 null)
	 */
	Animal removeAvailableAnimal(String animalId, String enclosureId);

	/**
	 * 동물을 사육장에서 해제하여 다시 배치 가능한 상태로 만듭니다.
	 * 
	 * @param animalId 해제할 동물 ID
	 * @return 해제된 동물 객체 (없으면 null)
	 */
	Animal releaseAnimalFromEnclosure(String animalId);

	/**
	 * 동물이 특정 사육장에 배치되어 있는지 확인합니다.
	 * 
	 * @param animalId    확인할 동물 ID
	 * @param enclosureId 확인할 사육장 ID
	 * @return 해당 사육장에 배치되어 있으면 true
	 */
	boolean isAnimalInEnclosure(String animalId, String enclosureId);
}

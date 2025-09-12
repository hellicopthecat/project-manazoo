package app.repository.interfaces;

import app.enclosure.Enclosure;
import app.enclosure.EnvironmentType;
import app.enclosure.LocationType;
import app.animal.Animal;
import app.zooKeeper.ZooKeeper;

import java.util.List;
import java.util.Map;

/**
 * Enclosure 엔티티를 위한 특화된 Repository 인터페이스입니다.
 * 기본 CRUD 연산 외에 Enclosure 특화 기능을 제공합니다.
 */
public interface EnclosureRepository extends Repository<Enclosure, String> {
    
    /**
     * 환경 타입별로 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @return 해당 환경 타입의 인클로저 리스트
     */
    List<Enclosure> findByEnvironmentType(EnvironmentType environmentType);
    
    /**
     * 위치 타입별로 인클로저를 조회합니다.
     *
     * @param locationType 위치 타입
     * @return 해당 위치 타입의 인클로저 리스트
     */
    List<Enclosure> findByLocationType(LocationType locationType);
    
    /**
     * 현재 수용 가능한 인클로저를 조회합니다.
     * max_capacity 컬럼이 제거되어 모든 사육장이 수용 가능한 것으로 처리됩니다.
     *
     * @return 모든 인클로저 리스트
     */
    List<Enclosure> findAvailableEnclosures();
    
    /**
     * 특정 환경과 위치 조건을 모두 만족하는 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @param locationType 위치 타입
     * @return 조건에 맞는 인클로저 리스트
     */
    List<Enclosure> findByEnvironmentTypeAndLocationType(EnvironmentType environmentType, LocationType locationType);
    
    // =================================================================
    // 동물 및 사육사 관리를 위한 추가 메서드들
    // =================================================================
    
    /**
     * 사육장에 거주하는 동물 목록을 조회합니다.
     * 
     * @param enclosureId 사육장 ID
     * @return 동물 ID와 Animal 객체의 맵
     */
    Map<String, Animal> getEnclosureInhabitants(String enclosureId);
    
    /**
     * 사육장에 배정된 사육사 목록을 조회합니다.
     * 
     * @param enclosureId 사육장 ID
     * @return 사육사 ID와 ZooKeeper 객체의 맵
     */
    Map<String, ZooKeeper> getEnclosureCaretakers(String enclosureId);
    
    /**
     * 사육장에 동물을 추가합니다.
     * 
     * @param enclosureId 사육장 ID
     * @param animalId 동물 ID
     * @param animal 동물 객체
     * @return 성공 여부
     */
    boolean addAnimalToEnclosure(String enclosureId, String animalId, Animal animal);
    
    /**
     * 사육장에 사육사를 배정합니다.
     * 
     * @param enclosureId 사육장 ID
     * @param keeperId 사육사 ID
     * @param keeper 사육사 객체
     * @return 성공 여부
     */
    boolean assignKeeperToEnclosure(String enclosureId, String keeperId, ZooKeeper keeper);
}

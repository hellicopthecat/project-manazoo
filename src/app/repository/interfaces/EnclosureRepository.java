package app.repository.interfaces;

import app.enclosure.Enclosure;
import app.enclosure.EnvironmentType;
import app.enclosure.LocationType;

import java.util.List;

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
     * 최대 수용 인원 이하로 필터링하여 인클로저를 조회합니다.
     *
     * @param maxCapacity 최대 수용 인원
     * @return 조건에 맞는 인클로저 리스트
     */
    List<Enclosure> findByMaxCapacityLessThanEqual(int maxCapacity);
    
    /**
     * 현재 수용 가능한 인클로저를 조회합니다 (현재 인원 < 최대 수용 인원).
     *
     * @return 수용 가능한 인클로저 리스트
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
}

package app.repository.memory;

import app.enclosure.Enclosure;
import app.enclosure.EnvironmentType;
import app.enclosure.LocationType;
import app.repository.interfaces.EnclosureRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 메모리 기반 EnclosureRepository 구현체입니다.
 * 데이터를 Map에 저장하여 빠른 조회와 조작을 제공합니다.
 * Holder Pattern을 사용한 Singleton으로 성능과 Thread Safety를 보장합니다.
 */
public class MemoryEnclosureRepository implements EnclosureRepository {
    
    private final Map<String, Enclosure> enclosures = new HashMap<>();
    
    /**
     * private 생성자 - Singleton 패턴 적용
     */
    private MemoryEnclosureRepository() {}
    
    /**
     * Initialization-on-demand holder pattern을 사용한 Thread-safe Singleton
     * JVM의 클래스 로딩 메커니즘을 활용하여 동기화 오버헤드 없이 lazy loading 구현
     */
    private static class SingletonHolder {
        private static final MemoryEnclosureRepository INSTANCE = new MemoryEnclosureRepository();
    }
    
    /**
     * Singleton 인스턴스를 반환합니다.
     * Holder Pattern을 사용하여 최적의 성능과 Thread Safety를 보장합니다.
     * 
     * @return MemoryEnclosureRepository 인스턴스
     */
    public static MemoryEnclosureRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    /**
     * 인클로저를 저장합니다.
     *
     * @param enclosure 저장할 인클로저
     * @return 저장된 인클로저
     */
    @Override
    public Enclosure save(Enclosure enclosure) {
        if (enclosure == null) {
            throw new IllegalArgumentException("인클로저는 null일 수 없습니다.");
        }
        if (enclosure.getId() == null) {
            throw new IllegalArgumentException("인클로저 ID는 null일 수 없습니다.");
        }
        
        enclosures.put(enclosure.getId(), enclosure);
        return enclosure;
    }
    
    /**
     * ID로 인클로저를 조회합니다.
     *
     * @param id 인클로저 ID
     * @return 조회된 인클로저 (Optional로 래핑됨)
     */
    @Override
    public Optional<Enclosure> findById(String id) {
        return Optional.ofNullable(enclosures.get(id));
    }
    
    /**
     * 모든 인클로저를 조회합니다.
     *
     * @return 모든 인클로저의 리스트
     */
    @Override
    public List<Enclosure> findAll() {
        return new ArrayList<>(enclosures.values());
    }
    
    /**
     * 인클로저를 업데이트합니다.
     *
     * @param enclosure 업데이트할 인클로저
     * @return 업데이트된 인클로저
     */
    @Override
    public Enclosure update(Enclosure enclosure) {
        if (enclosure == null) {
            throw new IllegalArgumentException("인클로저는 null일 수 없습니다.");
        }
        if (enclosure.getId() == null) {
            throw new IllegalArgumentException("인클로저 ID는 null일 수 없습니다.");
        }
        if (!enclosures.containsKey(enclosure.getId())) {
            throw new IllegalArgumentException("업데이트할 인클로저가 존재하지 않습니다: " + enclosure.getId());
        }
        
        enclosures.put(enclosure.getId(), enclosure);
        return enclosure;
    }
    
    /**
     * ID로 인클로저를 삭제합니다.
     *
     * @param id 삭제할 인클로저 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteById(String id) {
        return enclosures.remove(id) != null;
    }
    
    /**
     * 인클로저가 존재하는지 확인합니다.
     *
     * @param id 확인할 인클로저 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        return enclosures.containsKey(id);
    }
    
    /**
     * 모든 인클로저를 삭제합니다.
     */
    @Override
    public void deleteAll() {
        enclosures.clear();
    }
    
    /**
     * 총 인클로저 개수를 반환합니다.
     *
     * @return 인클로저 개수
     */
    @Override
    public long count() {
        return enclosures.size();
    }
    
    /**
     * 환경 타입별로 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @return 해당 환경 타입의 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByEnvironmentType(EnvironmentType environmentType) {
        return enclosures.values().stream()
                .filter(enclosure -> enclosure.getEnvironmentType() == environmentType)
                .collect(Collectors.toList());
    }
    
    /**
     * 위치 타입별로 인클로저를 조회합니다.
     *
     * @param locationType 위치 타입
     * @return 해당 위치 타입의 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByLocationType(LocationType locationType) {
        return enclosures.values().stream()
                .filter(enclosure -> enclosure.getLocationType() == locationType)
                .collect(Collectors.toList());
    }
    
        
    /**
     * 현재 수용 가능한 인클로저를 조회합니다.
     * max_capacity 컬럼이 제거되어 모든 사육장이 수용 가능한 것으로 처리됩니다.
     *
     * @return 모든 인클로저 리스트
     */
    @Override
    public List<Enclosure> findAvailableEnclosures() {
        // max_capacity 컬럼이 제거되어 모든 사육장이 수용 가능한 것으로 처리
        return new ArrayList<>(enclosures.values());
    }
    
    /**
     * 특정 환경과 위치 조건을 모두 만족하는 인클로저를 조회합니다.
     *
     * @param environmentType 환경 타입
     * @param locationType 위치 타입
     * @return 조건에 맞는 인클로저 리스트
     */
    @Override
    public List<Enclosure> findByEnvironmentTypeAndLocationType(EnvironmentType environmentType, LocationType locationType) {
        return enclosures.values().stream()
                .filter(enclosure -> enclosure.getEnvironmentType() == environmentType 
                                  && enclosure.getLocationType() == locationType)
                .collect(Collectors.toList());
    }
}

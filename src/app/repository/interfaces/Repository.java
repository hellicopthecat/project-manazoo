package app.repository.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * 기본 Repository 인터페이스 - CRUD 연산을 위한 공통 계약을 정의합니다.
 * Generic 타입을 사용하여 다양한 엔티티 타입에 재사용 가능합니다.
 *
 * @param <T> 엔티티 타입
 * @param <ID> 엔티티의 고유 식별자 타입
 */
public interface Repository<T, ID> {
    
    /**
     * 엔티티를 저장소에 저장합니다.
     *
     * @param entity 저장할 엔티티
     * @return 저장된 엔티티
     */
    T save(T entity);
    
    /**
     * ID를 통해 엔티티를 조회합니다.
     *
     * @param id 엔티티의 고유 식별자
     * @return 조회된 엔티티 (Optional로 래핑됨)
     */
    Optional<T> findById(ID id);
    
    /**
     * 모든 엔티티를 조회합니다.
     *
     * @return 모든 엔티티의 리스트
     */
    List<T> findAll();
    
    /**
     * 엔티티를 업데이트합니다.
     *
     * @param entity 업데이트할 엔티티
     * @return 업데이트된 엔티티
     */
    T update(T entity);
    
    /**
     * ID를 통해 엔티티를 삭제합니다.
     *
     * @param id 삭제할 엔티티의 고유 식별자
     * @return 삭제 성공 여부
     */
    boolean deleteById(ID id);
    
    /**
     * 엔티티가 존재하는지 확인합니다.
     *
     * @param id 확인할 엔티티의 고유 식별자
     * @return 존재 여부
     */
    boolean existsById(ID id);
    
    /**
     * 모든 엔티티를 삭제합니다.
     */
    void deleteAll();
    
    /**
     * 총 엔티티 개수를 반환합니다.
     *
     * @return 엔티티 개수
     */
    long count();
}

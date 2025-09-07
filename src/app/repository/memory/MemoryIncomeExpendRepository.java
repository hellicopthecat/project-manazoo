package app.repository.memory;

import app.incomeExpend.IncomeExpend;
import app.incomeExpend.IncomeExpendType;
import app.repository.interfaces.IncomeExpendRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IncomeExpend 엔티티를 위한 메모리 기반 Repository 구현체입니다.
 * Singleton 패턴을 적용하여 애플리케이션 전체에서 단일 데이터 저장소를 사용합니다.
 * 
 * <p>메모리 내 HashMap을 사용하여 수입/지출 데이터를 관리합니다.</p>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-03
 */
public class MemoryIncomeExpendRepository implements IncomeExpendRepository {
    
    /**
     * Singleton 인스턴스
     */
    private static MemoryIncomeExpendRepository instance;
    
    /** 수입/지출 내역을 저장하는 메모리 저장소 */
    private final Map<String, IncomeExpend> storage = new HashMap<>();
    
    /**
     * private 생성자 - Singleton 패턴 적용
     */
    private MemoryIncomeExpendRepository() {}
    
    /**
     * Singleton 인스턴스를 반환합니다.
     * Thread-safe lazy initialization을 적용했습니다.
     * 
     * @return MemoryIncomeExpendRepository 인스턴스
     */
    public static synchronized MemoryIncomeExpendRepository getInstance() {
        if (instance == null) {
            instance = new MemoryIncomeExpendRepository();
        }
        return instance;
    }
    
    /**
     * IncomeExpend를 저장소에 저장합니다.
     * 
     * @param incomeExpend 저장할 IncomeExpend 객체
     * @return 저장된 IncomeExpend 객체
     */
    @Override
    public IncomeExpend save(IncomeExpend incomeExpend) {
        if (incomeExpend == null) {
            throw new IllegalArgumentException("IncomeExpend는 null일 수 없습니다.");
        }
        
        String id = incomeExpend.getId();
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("IncomeExpend ID는 null이거나 빈 문자열일 수 없습니다.");
        }
        
        storage.put(id, incomeExpend);
        return incomeExpend;
    }
    
    /**
     * ID로 IncomeExpend를 조회합니다.
     * 
     * @param id 조회할 IncomeExpend의 ID
     * @return 조회된 IncomeExpend 객체, 없으면 empty Optional
     */
    @Override
    public Optional<IncomeExpend> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }
    
    /**
     * 모든 IncomeExpend를 조회합니다.
     * 
     * @return 모든 IncomeExpend 목록
     */
    @Override
    public List<IncomeExpend> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    /**
     * IncomeExpend를 업데이트합니다.
     * 
     * @param incomeExpend 업데이트할 IncomeExpend 객체
     * @return 업데이트된 IncomeExpend 객체
     */
    @Override
    public IncomeExpend update(IncomeExpend incomeExpend) {
        if (incomeExpend == null) {
            throw new IllegalArgumentException("IncomeExpend는 null일 수 없습니다.");
        }
        
        String id = incomeExpend.getId();
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("IncomeExpend ID는 null이거나 빈 문자열일 수 없습니다.");
        }
        
        if (!storage.containsKey(id)) {
            throw new IllegalArgumentException("업데이트하려는 IncomeExpend가 존재하지 않습니다: " + id);
        }
        
        storage.put(id, incomeExpend);
        return incomeExpend;
    }
    
    /**
     * ID로 IncomeExpend를 삭제합니다.
     * 
     * @param id 삭제할 IncomeExpend의 ID
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return storage.remove(id) != null;
    }
    
    /**
     * IncomeExpend가 존재하는지 확인합니다.
     * 
     * @param id 확인할 IncomeExpend의 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return storage.containsKey(id);
    }
    
    /**
     * 모든 IncomeExpend를 삭제합니다.
     */
    @Override
    public void deleteAll() {
        storage.clear();
    }
    
    /**
     * 전체 IncomeExpend 개수를 반환합니다.
     * 
     * @return 전체 개수
     */
    @Override
    public long count() {
        return storage.size();
    }
    
    /**
     * 새로운 수입/지출 내역을 생성합니다.
     * 
     * @param incomeExpend 생성할 수입/지출 객체
     * @return 생성된 내역 객체
     */
    @Override
    public IncomeExpend createIncomeExpend(IncomeExpend incomeExpend) {
        return save(incomeExpend);
    }
    
    /**
     * 모든 수입 내역을 조회합니다.
     * 
     * @return 수입 내역 목록
     */
    @Override
    public List<IncomeExpend> getIncomeList() {
        return storage.values().stream()
                .filter(ie -> ie.IEType == IncomeExpendType.INCOME)
                .collect(Collectors.toList());
    }
    
    /**
     * 모든 지출 내역을 조회합니다.
     * 
     * @return 지출 내역 목록
     */
    @Override
    public List<IncomeExpend> getExpendList() {
        return storage.values().stream()
                .filter(ie -> ie.IEType == IncomeExpendType.EXPEND)
                .collect(Collectors.toList());
    }
    
    /**
     * 총 수입 금액을 계산합니다.
     * 
     * @return 총 수입 금액
     */
    @Override
    public Long getTotalIncomes() {
        return getIncomeList().stream()
                .mapToLong(ie -> ie.money)
                .sum();
    }
    
    /**
     * 총 지출 금액을 계산합니다.
     * 
     * @return 총 지출 금액
     */
    @Override
    public Long getTotalExpends() {
        return getExpendList().stream()
                .mapToLong(ie -> ie.money)
                .sum();
    }
}

package app.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import app.visitor.Reservation;
import app.repository.interfaces.VisitorRepository;

/**
 * 메모리 기반 방문객 예약 Repository 구현체입니다.
 * 예약 데이터를 Map에 저장하여 빠른 조회와 조작을 제공합니다.
 * Singleton 패턴을 적용하여 애플리케이션 전체에서 단일 데이터 저장소를 사용합니다.
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>메모리 기반 데이터 저장</li>
 *   <li>예약 관리 기능</li>
 *   <li>타입 안전성 확보</li>
 *   <li>기존 VisitorManager와의 완전 호환</li>
 *   <li>Singleton 패턴으로 데이터 일관성 보장</li>
 * </ul>
 * 
 * <p>이 구현체는 VisitorManager의 기존 기능을 Repository 패턴으로 재구현하여
 * 데이터 접근 계층을 분리하고 코드의 유지보수성을 향상시킵니다.</p>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-03
 * @see VisitorRepository
 * @see Reservation
 */
public class MemoryVisitorRepository implements VisitorRepository {
    
    /**
     * 예약 데이터를 저장하는 Map
     * Key: 예약 ID (String), Value: 예약 객체 (Reservation)
     */
    private final Map<String, Reservation> reservations;
    
    /**
     * private 생성자 - Singleton 패턴 적용
     */
    private MemoryVisitorRepository() {
        this.reservations = new HashMap<>();
    }
    
    /**
     * Initialization-on-demand holder pattern을 사용한 Thread-safe Singleton
     * JVM의 클래스 로딩 메커니즘을 활용하여 동기화 오버헤드 없이 lazy loading 구현
     */
    private static class SingletonHolder {
        private static final MemoryVisitorRepository INSTANCE = new MemoryVisitorRepository();
    }
    
    /**
     * Singleton 인스턴스를 반환합니다.
     * Holder Pattern을 사용하여 최적의 성능과 Thread Safety를 보장합니다.
     * 
     * @return MemoryVisitorRepository 인스턴스
     */
    public static MemoryVisitorRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    // =================================================================
    // Repository<Reservation, String> 기본 CRUD 구현
    // =================================================================
    
    /**
     * 예약 객체를 저장소에 저장합니다.
     * 
     * @param reservation 저장할 예약 객체
     * @return 저장된 예약 객체
     * @throws NullPointerException 예약 객체나 ID가 null인 경우
     */
    @Override
    public Reservation save(Reservation reservation) {
        Objects.requireNonNull(reservation, "예약 객체는 null일 수 없습니다.");
        Objects.requireNonNull(reservation.getId(), "예약 ID는 null일 수 없습니다.");
        
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }
    
    /**
     * ID를 통해 예약을 조회합니다.
     * 
     * @param id 조회할 예약의 고유 식별자
     * @return 조회된 예약 객체 (Optional로 래핑됨)
     */
    @Override
    public Optional<Reservation> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(reservations.get(id));
    }
    
    /**
     * 저장소에 있는 모든 예약을 조회합니다.
     * 
     * @return 모든 예약의 리스트 (Working Copy Pattern 적용)
     */
    @Override
    public List<Reservation> findAll() {
        return reservations.values().stream()
                .collect(Collectors.toList());
    }
    
    /**
     * 기존 예약의 정보를 업데이트합니다.
     * 
     * @param reservation 업데이트할 예약 객체
     * @return 업데이트된 예약 객체
     * @throws IllegalArgumentException 수정하려는 예약이 존재하지 않는 경우
     * @throws NullPointerException 예약 객체나 ID가 null인 경우
     */
    @Override
    public Reservation update(Reservation reservation) {
        Objects.requireNonNull(reservation, "예약 객체는 null일 수 없습니다.");
        Objects.requireNonNull(reservation.getId(), "예약 ID는 null일 수 없습니다.");
        
        if (!existsById(reservation.getId())) {
            throw new IllegalArgumentException("수정하려는 예약이 존재하지 않습니다: " + reservation.getId());
        }
        
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }
    
    /**
     * ID를 통해 예약을 삭제합니다.
     * 
     * @param id 삭제할 예약의 고유 식별자
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        
        Reservation removed = reservations.remove(id);
        return removed != null;
    }
    
    /**
     * 특정 ID의 예약이 존재하는지 확인합니다.
     * 
     * @param id 확인할 예약의 고유 식별자
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        return id != null && reservations.containsKey(id);
    }
    
    /**
     * 모든 예약을 삭제합니다.
     * 저장소를 초기화할 때 사용됩니다.
     */
    @Override
    public void deleteAll() {
        reservations.clear();
    }
    
    /**
     * 저장소에 있는 예약의 총 개수를 반환합니다.
     * 
     * @return 예약 개수
     */
    @Override
    public long count() {
        return reservations.size();
    }
    
    // =================================================================
    // VisitorRepository 특화 메서드 구현
    // =================================================================
    
    /**
     * 새로운 예약을 생성하여 저장소에 추가합니다.
     * VisitorManager의 reservation() 메서드에서 사용됩니다.
     * 
     * @param id 예약 ID
     * @param name 방문객 이름
     * @param phone 전화번호
     * @param date 방문 날짜
     * @param adultCount 성인 수
     * @param childCount 어린이 수
     * @param totalPrice 총 가격
     * @return 생성된 예약 객체
     */
    @Override
    public Reservation createReservation(String id, String name, String phone, String date,
                                       int adultCount, int childCount, int totalPrice) {
        
        Reservation reservation = new Reservation(id, name, phone, date, adultCount, childCount, totalPrice);
        return save(reservation);
    }
    
    /**
     * 저장소의 모든 예약 목록을 반환합니다.
     * 내부적으로 findAll()을 호출하여 Working Copy Pattern을 적용합니다.
     * 
     * @return 전체 예약 목록
     */
    @Override
    public List<Reservation> getReservationList() {
        return findAll();
    }
    
    /**
     * 특정 ID의 예약을 조회합니다.
     * VisitorManager의 viewReservation() 메서드에서 사용됩니다.
     * 
     * @param reservationId 예약 ID
     * @return 조회된 예약 객체 (없으면 null)
     */
    @Override
    public Reservation getReservationById(String reservationId) {
        return findById(reservationId).orElse(null);
    }
    
    /**
     * 특정 예약의 방문 날짜를 수정합니다.
     * VisitorManager의 editReservationDate() 메서드에서 사용됩니다.
     * 
     * @param reservationId 수정할 예약 ID
     * @param newDate 새로운 방문 날짜
     * @return 수정 성공 여부
     */
    @Override
    public boolean updateReservationDate(String reservationId, String newDate) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation != null) {
            reservation.setDate(newDate);
            return true;
        }
        return false;
    }
    
    /**
     * 특정 ID의 예약을 취소합니다.
     * VisitorManager의 removeReservation() 메서드에서 사용됩니다.
     * 
     * @param reservationId 취소할 예약 ID
     * @return 취소 성공 여부
     */
    @Override
    public boolean cancelReservation(String reservationId) {
        return deleteById(reservationId);
    }
    
    /**
     * 예약이 존재하는지 확인합니다.
     * VisitorManager의 예약 존재 여부 확인에 사용됩니다.
     * 
     * @param reservationId 확인할 예약 ID
     * @return 예약 존재 여부
     */
    @Override
    public boolean hasReservation(String reservationId) {
        return existsById(reservationId);
    }
    
    /**
     * Repository의 문자열 표현을 반환합니다.
     * 현재 저장된 예약의 개수 정보를 포함합니다.
     * 
     * @return Repository 정보 문자열
     */
    @Override
    public String toString() {
        return String.format("MemoryVisitorRepository{size=%d}", count());
    }
}

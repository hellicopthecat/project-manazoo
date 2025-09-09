package app.repository.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import app.visitor.Reservation;
import app.repository.interfaces.VisitorRepository;
import app.config.DatabaseConnection;
import app.common.SimpleLogger;

/**
 * JDBC 기반 방문객 예약 Repository 구현체입니다.
 * MemoryVisitorRepository와 동일한 기능을 데이터베이스 기반으로 제공합니다.
 * Singleton 패턴을 적용하여 애플리케이션 전체에서 단일 데이터 저장소를 사용합니다.
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>데이터베이스 기반 데이터 저장</li>
 *   <li>예약 관리 기능</li>
 *   <li>타입 안전성 확보</li>
 *   <li>기존 VisitorManager와의 완전 호환</li>
 *   <li>Singleton 패턴으로 데이터 일관성 보장</li>
 * </ul>
 * 
 * <p>이 구현체는 MemoryVisitorRepository의 기능을 JDBC 기반으로 재구현하여
 * 데이터 영속성을 제공하고 애플리케이션의 확장성을 향상시킵니다.</p>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-09
 * @see VisitorRepository
 * @see Reservation
 */
public class JdbcVisitorRepository implements VisitorRepository {
    
    // 로거 인스턴스
    private static final SimpleLogger logger = SimpleLogger.getLogger(JdbcVisitorRepository.class);
    
    /**
     * private 생성자 - Singleton 패턴 적용
     */
    private JdbcVisitorRepository() {
        // private 생성자로 외부 인스턴스 생성 방지
    }
    
    /**
     * Initialization-on-demand holder pattern을 사용한 Thread-safe Singleton
     * JVM의 클래스 로딩 메커니즘을 활용하여 동기화 오버헤드 없이 lazy loading 구현
     */
    private static class SingletonHolder {
        private static final JdbcVisitorRepository INSTANCE = new JdbcVisitorRepository();
    }
    
    /**
     * Singleton 인스턴스를 반환합니다.
     * Holder Pattern을 사용하여 최적의 성능과 Thread Safety를 보장합니다.
     * 
     * @return JdbcVisitorRepository 인스턴스
     */
    public static JdbcVisitorRepository getInstance() {
        logger.debug("JdbcVisitorRepository 싱글톤 인스턴스 반환");
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
        
        logger.debug("예약 저장 시작: ID=%s, 이름=%s", reservation.getId(), reservation.getName());
        
        String sql = """
            INSERT INTO reservations (id, name, phone_number, visit_date, 
                                    number_of_visitors, number_of_adults, number_of_childs) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservation.getId());
            pstmt.setString(2, reservation.getName());
            pstmt.setString(3, reservation.getPhone());
            pstmt.setDate(4, Date.valueOf(reservation.getDate()));
            pstmt.setInt(5, reservation.getAdultCount() + reservation.getChildCount());
            pstmt.setInt(6, reservation.getAdultCount());
            pstmt.setInt(7, reservation.getChildCount());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("예약 저장에 실패했습니다.");
            }
            
            logger.debug("예약 저장 완료: ID=%s", reservation.getId());
            return reservation;
            
        } catch (SQLException e) {
            logger.error("예약 저장 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 저장 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
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
        
        String sql = """
            SELECT id, name, phone_number, visit_date, number_of_adults, number_of_childs
            FROM reservations 
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    logger.debug("예약 조회 성공: ID=%s", id);
                    return Optional.of(reservation);
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("예약 조회 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 저장소에 있는 모든 예약을 조회합니다.
     * 
     * @return 모든 예약의 리스트 (Working Copy Pattern 적용)
     */
    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        
        String sql = """
            SELECT id, name, phone_number, visit_date, number_of_adults, number_of_childs
            FROM reservations 
            ORDER BY created_at DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
            
            logger.debug("전체 예약 조회 완료: %d건", reservations.size());
            return reservations;
            
        } catch (SQLException e) {
            logger.error("전체 예약 조회 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("전체 예약 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
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
        
        String sql = """
            UPDATE reservations 
            SET name = ?, phone_number = ?, visit_date = ?, 
                number_of_visitors = ?, number_of_adults = ?, number_of_childs = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservation.getName());
            pstmt.setString(2, reservation.getPhone());
            pstmt.setDate(3, Date.valueOf(reservation.getDate()));
            pstmt.setInt(4, reservation.getAdultCount() + reservation.getChildCount());
            pstmt.setInt(5, reservation.getAdultCount());
            pstmt.setInt(6, reservation.getChildCount());
            pstmt.setString(7, reservation.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("예약 수정에 실패했습니다.");
            }
            
            logger.debug("예약 수정 완료: ID=%s", reservation.getId());
            return reservation;
            
        } catch (SQLException e) {
            logger.error("예약 수정 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 수정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
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
        
        String sql = "DELETE FROM reservations WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.debug("예약 삭제 완료: ID=%s", id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("예약 삭제 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 ID의 예약이 존재하는지 확인합니다.
     * 
     * @param id 확인할 예약의 고유 식별자
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        
        String sql = "SELECT 1 FROM reservations WHERE id = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("예약 존재 확인 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 존재 확인 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 모든 예약을 삭제합니다.
     * 저장소를 초기화할 때 사용됩니다.
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM reservations";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int deletedCount = pstmt.executeUpdate();
            logger.debug("전체 예약 삭제 완료: %d건 삭제", deletedCount);
            
        } catch (SQLException e) {
            logger.error("전체 예약 삭제 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("전체 예약 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 저장소에 있는 예약의 총 개수를 반환합니다.
     * 
     * @return 예약 개수
     */
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM reservations";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("예약 개수 조회: %d건", count);
                return count;
            }
            
        } catch (SQLException e) {
            logger.error("예약 개수 조회 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 개수 조회 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        return 0;
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
        
        logger.debug("예약 생성: ID=%s, 이름=%s, 성인=%d, 어린이=%d", id, name, adultCount, childCount);
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
        if (reservationId == null || newDate == null) {
            logger.debug("예약 날짜 수정 실패: ID 또는 날짜가 null입니다");
            return false;
        }
        
        String sql = """
            UPDATE reservations 
            SET visit_date = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(newDate));
            pstmt.setString(2, reservationId);
            
            int rowsAffected = pstmt.executeUpdate();
            boolean updated = rowsAffected > 0;
            
            if (updated) {
                logger.debug("예약 날짜 수정 완료: ID=%s, 새 날짜=%s", reservationId, newDate);
            } else {
                logger.debug("예약 날짜 수정 실패: 해당 예약을 찾을 수 없음 ID=%s", reservationId);
            }
            
            return updated;
            
        } catch (SQLException e) {
            logger.error("예약 날짜 수정 중 데이터베이스 오류가 발생했습니다", e);
            throw new RuntimeException("예약 날짜 수정 중 데이터베이스 오류가 발생했습니다: " + e.getMessage(), e);
        }
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
        try {
            return String.format("JdbcVisitorRepository{size=%d}", count());
        } catch (Exception e) {
            return "JdbcVisitorRepository{connection=error}";
        }
    }
    
    // =================================================================
    // 헬퍼 메서드
    // =================================================================
    
    /**
     * ResultSet을 Reservation 객체로 매핑합니다.
     * 
     * @param rs 데이터베이스 조회 결과
     * @return 매핑된 Reservation 객체
     * @throws SQLException SQL 처리 오류 시
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String phone = rs.getString("phone_number");
        String date = rs.getDate("visit_date").toString();
        int adultCount = rs.getInt("number_of_adults");
        int childCount = rs.getInt("number_of_childs");
        
        // totalPrice는 실시간 계산 (VisitorManager의 상수값 사용)
        final int ADULT_PRICE = 13000;
        final int CHILD_PRICE = 5000;
        int totalPrice = adultCount * ADULT_PRICE + childCount * CHILD_PRICE;
        
        return new Reservation(id, name, phone, date, adultCount, childCount, totalPrice);
    }
}

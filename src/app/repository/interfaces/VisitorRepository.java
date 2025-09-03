package app.repository.interfaces;

import app.visitor.Reservation;
import java.util.List;

/**
 * Reservation 엔티티를 위한 특화된 Repository 인터페이스입니다.
 * 기본 CRUD 연산 외에 방문객 예약 도메인 특화 기능을 제공합니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>예약 생성 및 기본 관리</li>
 *   <li>예약 조회 및 검색</li>
 *   <li>예약 정보 수정</li>
 *   <li>예약 취소</li>
 * </ul>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-03
 */
public interface VisitorRepository extends Repository<Reservation, String> {
    
    /**
     * 새로운 예약을 생성합니다.
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
    Reservation createReservation(String id, String name, String phone, String date, 
                                int adultCount, int childCount, int totalPrice);
    
    /**
     * 모든 예약 목록을 조회합니다.
     * 
     * @return 전체 예약 목록
     */
    List<Reservation> getReservationList();
    
    /**
     * ID로 예약을 조회합니다.
     * 
     * @param reservationId 예약 ID
     * @return 조회된 예약 객체 (없으면 null)
     */
    Reservation getReservationById(String reservationId);
    
    /**
     * 예약의 방문 날짜를 수정합니다.
     * 
     * @param reservationId 수정할 예약 ID
     * @param newDate 새로운 방문 날짜
     * @return 수정 성공 여부
     */
    boolean updateReservationDate(String reservationId, String newDate);
    
    /**
     * 예약을 취소합니다.
     * 
     * @param reservationId 취소할 예약 ID
     * @return 취소 성공 여부
     */
    boolean cancelReservation(String reservationId);
    
    /**
     * 예약이 존재하는지 확인합니다.
     * 
     * @param reservationId 확인할 예약 ID
     * @return 예약 존재 여부
     */
    boolean hasReservation(String reservationId);
}

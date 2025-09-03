package app.repository.interfaces;

import app.incomeExpend.IncomeExpend;

import java.util.List;

/**
 * IncomeExpend 엔티티를 위한 특화된 Repository 인터페이스입니다.
 * 기본 CRUD 연산 외에 수입/지출 도메인 특화 기능을 제공합니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>수입/지출 내역 기록 및 관리</li>
 *   <li>수입/지출별 조회</li>
 *   <li>총액 계산</li>
 * </ul>
 * 
 * @author ManazooTeam
 * @version 1.0
 * @since 2025-09-03
 */
public interface IncomeExpendRepository extends Repository<IncomeExpend, String> {
    
    /**
     * 새로운 수입/지출 내역을 생성합니다.
     * 
     * @param incomeExpend 생성할 수입/지출 객체
     * @return 생성된 내역 객체
     */
    IncomeExpend createIncomeExpend(IncomeExpend incomeExpend);
    
    /**
     * 모든 수입 내역을 조회합니다.
     * 
     * @return 수입 내역 목록
     */
    List<IncomeExpend> getIncomeList();
    
    /**
     * 모든 지출 내역을 조회합니다.
     * 
     * @return 지출 내역 목록
     */
    List<IncomeExpend> getExpendList();
    
    /**
     * 총 수입 금액을 계산합니다.
     * 
     * @return 총 수입 금액
     */
    Long getTotalIncomes();
    
    /**
     * 총 지출 금액을 계산합니다.
     * 
     * @return 총 지출 금액
     */
    Long getTotalExpends();
}

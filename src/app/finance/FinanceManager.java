package app.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import app.incomeExpend.EventType;
import app.incomeExpend.IncomeExpend;
import app.incomeExpend.IncomeExpendType;

/**
 * FinanceManager 클래스 
 * --------------------- 
 * 수입, 지출, 자본(자산)을 관리하는 정적(Static) 재무 관리 클래스 
 * - 수입/지출 내역 기록 
 * - 자본(총자산) 관리 
 * - 총수입, 총지출, 순이익 계산 
 * - 재무 보고서 생성
 */
public class FinanceManager {

	// 수입 내역 리스트
	public static List<IncomeExpend> incomes = new ArrayList<>();
	// 지출 내역 리스트
	public static List<IncomeExpend> expends = new ArrayList<>();
	// 자본 (초기값:10조)
	public static Long capital = 10000000000000l; // 자본
	// test 실행시 현재 재무 보고서를 콘솔에 출력
	// public static void main(String[] args) {
	// System.out.println(generateReport());
	// }

	/**
	 * 수입/지출 내역을 기록하고 자본을 갱신하는 메서드
	 * 
	 * @param money    금액
	 * @param desc     설명
	 * @param ieNum    수입(1) / 지출(2) 구분 번호
	 * @param eventNum 이벤트 종류 번호 (예: 직원 월급, 사파리 운영비 등)
	 */
	public static void useMoney(Long money, String desc, int ieNum, int eventNum) {
		// 수입/지출 타입변환
		IncomeExpendType ieType = switchIEType(ieNum);
		// 이벤트 타입변환
		EventType spendType = switchSpendType(eventNum);
		// 수입/지출 객체 생성
		IncomeExpend ie = new IncomeExpend(money, desc, ieType, spendType);
		// 수입일 경우
		if (ieType == IncomeExpendType.INCOME) {
			// 자본증가
			capital += money;
			// 수입리스트 추가
			incomes.add(ie);
		} else {
			// 지출일경우
			// 자본감소
			capital -= money;
			// 지출리스트에 추가
			expends.add(ie);
		}
	}

	/**
	 * 이벤트 번호를 EventType Enum으로 변환
	 * 
	 * @param eventNum 이벤트 번호
	 * @return EventType
	 */

	private static EventType switchSpendType(int eventNum) {
		EventType eventType = switch (eventNum) {
		case 1 -> EventType.FEE;
		case 2 -> EventType.EMPLOYEE_MONTH;
		case 3 -> EventType.EMPLOYEE_EXTRA;
		case 4 -> EventType.ENCLOSURE;
		case 5 -> EventType.SAFARI;
		case 6 -> EventType.AQUASHOW;
		case 7 -> EventType.EXPERIENCE;
		default -> throw new IllegalArgumentException(eventNum + "은 잘못된 번호 입니다.");
		};
		return eventType;
	}

	/**
	 * 수입/지출 번호를 IncomeExpendType Enum으로 변환
	 * 
	 * @param ieNum 1: 수입, 2: 지출
	 * @return IncomeExpendType
	 */
	private static IncomeExpendType switchIEType(int ieNum) {
		IncomeExpendType ieType = switch (ieNum) {
		case 1 -> IncomeExpendType.INCOME;
		case 2 -> IncomeExpendType.EXPEND;
		default -> throw new IllegalArgumentException(ieNum + "은 잘못된 번호 입니다.");
		};
		return ieType;
	}

	// 수입 내역 리스트 반환
	public static List<IncomeExpend> getIncomes() {
		return incomes;
	}

	// 지출 내역 리스트 반환
	public static List<IncomeExpend> getExpends() {
		return expends;
	}

	// 현재 자본(총자산) 반환
	public static Long getCapital() {
		return capital;
	}

	/**
	 * 자본 수정 메서드
	 * - 현재 자본(capital)에 파라미터 값을 누적
	 *   ex) setCapital(500) → 자본 +500
	 *   ex) setCapital(-500) → 자본 -500
	 */
	public static void setCapital(Long capital) {
		FinanceManager.capital += capital;
	}

	/**
	 * 순이익 계산
	 * @return 총수입 - 총지출
	 */
	public static Long getNetProfit() {
		Long result = getTotalIncomes() - getTotalExpends();
		return result;
	}

	/**
	 * 총수입 계산
	 * @return 수입 리스트 합계 (없으면 0 반환)
	 */
	public static Long getTotalIncomes() {
		OptionalLong incomeReduce = incomes.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return incomeReduce.orElse(0l);
	}

	/**
	 * 총지출 계산
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	public static Long getTotalExpends() {
		OptionalLong expendsReduce = expends.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return expendsReduce.orElse(0l);
	}

	/**
	 * 재무 보고서 생성
	 * @return 문자열 형태의 요약 보고서
	 */

	public static String generateReport() {
		return String.format("[총 자산 : %d , 총 수입 : %d , 총 지출 : %d , 순이익 : %d]", capital, getTotalIncomes(),
				getTotalExpends(), getNetProfit());
	}
}

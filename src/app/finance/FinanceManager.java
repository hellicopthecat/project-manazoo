package app.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import app.common.id.IdGeneratorUtil;
import app.incomeExpend.EventType;
import app.incomeExpend.IEConverter;
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
	private List<IncomeExpend> incomes = new ArrayList<>();
	// 지출 내역 리스트
	private List<IncomeExpend> expends = new ArrayList<>();
	// 자본 (초기값:10조)
	private Long capital = 1000000000l; // 자본

	// test 실행시 현재 재무 보고서를 콘솔에 출력
	public static void main(String[] args) {
		FinanceManager f = new FinanceManager();
		f.handleFinanceManagement();
//		System.out.println(f.generateReport());

	}

	// Constructor
	private FinanceManager() {
		String id = IdGeneratorUtil.generateId();
		incomes.add(new IncomeExpend(id, 3000000l, "설명", IncomeExpendType.INCOME, EventType.AQUASHOW));
		incomes.add(new IncomeExpend(id, 4000000l, "설명", IncomeExpendType.INCOME, EventType.AQUASHOW));
		expends.add(new IncomeExpend(id, 4000000l, "설명", IncomeExpendType.EXPEND, EventType.AQUASHOW));
		expends.add(new IncomeExpend(id, 2000000l, "설명", IncomeExpendType.EXPEND, EventType.AQUASHOW));
		expends.add(new IncomeExpend(id, 1000000l, "설명", IncomeExpendType.EXPEND, EventType.AQUASHOW));
		expends.add(new IncomeExpend(id, 3000000l, "설명", IncomeExpendType.EXPEND, EventType.AQUASHOW));
		Long incomeReduce = incomes.stream().map(i -> i.money).reduce((x, y) -> x + y).orElse(0l);
		Long expendReduce = expends.stream().map(i -> i.money).reduce((x, y) -> x + y).orElse(0l);
		setCapital(incomeReduce - expendReduce);
	}

	private Scanner in = new Scanner(System.in);

	public void handleFinanceManagement() {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			System.out.println("메뉴를 고르세요.");
			System.out.println("1: 수입/지출서 작성, 2: 수입/지출 조회, 3: 자본금 수정, 0: 뒤로가기");
			switch (Integer.parseInt(in.nextLine())) {
			case 1 -> useMoney(in);
			case 2 -> getAssetData(in);
			case 3 -> editAssetData(in);
			case 0 -> goBack(run);
			}
		}
	}

	/**
	 * 수입/지출 내역을 기록하고 자본을 갱신하는 메서드
	 * 
	 * @param in Scanner
	 */
	public void useMoney(Scanner in) {
		while (true) {
			// 수입/지출
			System.out.println("수입 지출 구분 번호를 입력하세요.");
			System.out.println("1: 수입 , 2: 지출");
			int ieNum = Integer.parseInt(in.nextLine());
			// 수입/지출 타입변환
			IncomeExpendType ieType = IEConverter.IETypeConverter(ieNum);
			if (ieType == null) {
				continue;
			}

			// 이벤트
			System.out.println("이벤트 종류 번호를 입력하세요.");
			System.out.println("1: 입장료, 2: 사파리, 3: 아쿠아쇼, 4:체험, 5: 식비, 6: 사육장, 7: 직원월급, 8: 직원수당");
			// 이벤트 타입변환
			int eventNum = Integer.parseInt(in.nextLine());
			EventType eventType = IEConverter.eventTypeConverter(eventNum);
			if (eventType == null) {
				continue;
			}

			// 금액 작성
			System.out.println("금액을 입력하세요.");
			long money = Long.parseLong(in.nextLine());

			// 설명작성
			System.out.println("설명을 작성하세요.");
			String desc = in.nextLine();

			String id = IdGeneratorUtil.generateId();
			// 수입/지출 객체 생성
			IncomeExpend ie = new IncomeExpend(id, money, desc, ieType, eventType);
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
			System.out.println("""
					=======================
					수입지출결의서가 작성되었습니다.
					=======================
					""");
			break;
		}
		// 수입일 경우
	}

	/**
	 * 수입/지출 내역, 자산 및 재무보고서를 조회하는 메서드
	 * 
	 * @param in Scanner
	 */
	public void getAssetData(Scanner in) {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			System.out.println("메뉴를 고르세요.");
			System.out.println(
					"1: 한줄 재무 보고서 출력, 2: 자본금 조회, 3: 순이익 조회, 4:총 수입금액, 5: 총 지출금액, 6: 수입 전체 조회, 7: 지출 전체 조회, 0: 뒤로가기");
			String index = in.nextLine();
			switch (Integer.parseInt(index)) {
			case 1 -> System.out.println(generateReport());
			case 2 -> System.out.println(getCapital());
			case 3 -> System.out.println(getNetProfit());
			case 4 -> System.out.println(getTotalIncomes());
			case 5 -> System.out.println(getTotalExpends());
			case 6 -> System.out.println(getIncomes());
			case 7 -> System.out.println(getExpends());
			case 0 -> goBack(run);
			default -> {
				System.out.println("잘못된 번호를 입력하셨습니다.");
				System.out.println(
						"1: 한줄 재무 보고서 출력, 2: 자본금 조회, 3: 순이익 조회, 4:총 수입금액, 5: 총 지출금액, 6: 수입 전체 조회, 7: 지출 전체 조회, 0: 뒤로가기");
			}
			}
		}
	}

	/**
	 * 지본금을 수정하는 메서드
	 * 
	 * @param in Scanner
	 */
	private void editAssetData(Scanner in) {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			System.out.println("메뉴를 고르세요.");
			System.out.println("1: 자본금 수정, 0: 뒤로가기");
			switch (Integer.parseInt(in.nextLine())) {
			case 1 -> {
				System.out.println("금액을 입력하세요.");
				Long money = Long.parseLong(in.nextLine());
				setCapital(money);
				System.out.println("금액이 수정되었습니다.");
			}
			case 0 -> goBack(run);
			}
		}
	}

	// 수입 내역 리스트 반환
	private List<IncomeExpend> getIncomes() {
		return incomes;
	}

	// 지출 내역 리스트 반환
	private List<IncomeExpend> getExpends() {
		return expends;
	}

	// 현재 자본(총자산) 반환
	private Long getCapital() {
		return capital;
	}

	/**
	 * 순이익 계산
	 * @return 총수입 - 총지출
	 */
	private Long getNetProfit() {
		Long result = getTotalIncomes() - getTotalExpends();
		return result;
	}

	/**
	 * 총수입 계산
	 * @return 수입 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalIncomes() {
		OptionalLong incomeReduce = incomes.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return incomeReduce.orElse(0l);
	}

	/**
	 * 총지출 계산
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalExpends() {
		OptionalLong expendsReduce = expends.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return expendsReduce.orElse(0l);
	}

	/**
	 * 재무 보고서 생성
	 * @return 문자열 형태의 요약 보고서
	 */

	private String generateReport() {
		return String.format("[총 자산 : %d , 총 수입 : %d , 총 지출 : %d , 순이익 : %d]", capital, getTotalIncomes(),
				getTotalExpends(), getNetProfit());
	}

	/**
	 * 자본 수정 메서드
	 * - 현재 자본(capital)에 파라미터 값을 누적
	 *   ex) setCapital(500) → 자본 +500
	 *   ex) setCapital(-500) → 자본 -500
	 */
	private void setCapital(Long capital) {
		this.capital += capital;
	}

	// util methods
	private void goBack(AtomicBoolean run) {
		System.out.println("뒤로갑니다.");
		run.set(false);
	}
}

package app.finance;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;

import app.common.IdGeneratorUtil;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
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
public final class FinanceManager {
	private static final FinanceManager instance = new FinanceManager();
	// 수입 내역 리스트
	private List<IncomeExpend> incomes = new ArrayList<>();
	// 지출 내역 리스트
	private List<IncomeExpend> expends = new ArrayList<>();
	// 자본 (초기값:1억)
	private Long capital = 1000000000l; // 자본

	// test 실행시 현재 재무 보고서를 콘솔에 출력
	// public static void main(String[] args) {
	// FinanceManager f = new FinanceManager();
	// f.handleFinanceManagement();
	// }

	/** 
	 * Singleton Class 입니다.
	 * 
	 * FinanceManager.getInstance().useMoney();로 자본금 차감과 증감을 사용하실 수 있습니다. 
	 * 
	 * @return instance
	 */
	public static FinanceManager getInstance() {
		return instance;
	}

	// Constructor
	private FinanceManager() {
		incomes.add(new IncomeExpend(IdGeneratorUtil.generateId(), 30000000l, "아쿠아쇼 수익금", IncomeExpendType.INCOME,
				EventType.AQUASHOW));
		expends.add(new IncomeExpend(IdGeneratorUtil.generateId(), 4000000l, "아기 코끼리 일주일 식비", IncomeExpendType.EXPEND,
				EventType.FOOD));
		expends.add(new IncomeExpend(IdGeneratorUtil.generateId(), 50000040l, "시설비", IncomeExpendType.EXPEND,
				EventType.EMPLOYEE_EXTRA));
		incomes.add(new IncomeExpend(IdGeneratorUtil.generateId(), 400112000l, "일주일 입장료", IncomeExpendType.INCOME,
				EventType.FEE));
		expends.add(new IncomeExpend(IdGeneratorUtil.generateId(), 9002100l, "수도세", IncomeExpendType.EXPEND,
				EventType.AQUASHOW));
		expends.add(new IncomeExpend(IdGeneratorUtil.generateId(), 3053000l, "직원월급", IncomeExpendType.EXPEND,
				EventType.EMPLOYEE_MONTH));
		Long incomeReduce = incomes.stream().map(i -> i.money).reduce((x, y) -> x + y).orElse(0l);
		Long expendReduce = expends.stream().map(i -> i.money).reduce((x, y) -> x + y).orElse(0l);
		setCapital(incomeReduce - expendReduce);
	}

	public void handleFinanceManagement() {
		AtomicBoolean run = new AtomicBoolean(true);
		UIUtil.printSeparator('━');
		TextArtUtil.printFinanceMenuTitle();
		UIUtil.printSeparator('━');
		while (run.get()) {
			String[] menu = { "수입/지출서 작성", "수입/지출 조회", "자본금 수정" };
			String[] s_menu = { "뒤로가기" };
			MenuUtil.generateMenuWithTextTitle("재 정 관 리", menu, s_menu);
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> useMoney();
			case 2 -> getAssetData();
			case 3 -> editAssetData();
			case 0 -> goBack(run);
			default -> wrongIndex();
			}
		}
	}

	/**
	 * 수입/지출 내역을 기록하고 자본을 갱신하는 메서드입니다.
	 * 
	 * 수입과 지출을 결정해주시고 금액까지 작성하면 자본금은 자동으로 계산됩니다.
	 * 
	 * @return money
	 */
	public Long useMoney() {
		while (true) {
			// 수입/지출
			UIUtil.printSeparator('━');
			System.out.println(MenuUtil.DEFAULT_PREFIX + "1: 수입 , 2: 지출");
			System.out.println(MenuUtil.DEFAULT_PREFIX + "수입 지출 구분 번호를 입력하세요 ▶");
			int ieNum = InputUtil.getIntInput();
			// 수입/지출 타입변환
			IncomeExpendType ieType = IEConverter.IETypeConverter(ieNum);

			// 이벤트
			System.out.println(
					MenuUtil.DEFAULT_PREFIX + "1: 입장료, 2: 사파리, 3: 아쿠아쇼, 4:체험, 5: 식비, 6: 사육장, 7: 직원월급, 8: 직원수당");
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "이벤트 종류 번호를 입력하세요 ▶");
			// 이벤트 타입변환
			int eventNum = InputUtil.getIntInput();
			EventType eventType = IEConverter.eventTypeConverter(eventNum);

			// 금액 작성
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "금액을 입력하세요 ▶");
			long money = InputUtil.getLongInput();

			// 설명작성
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "설명을 작성하세요 ▶");
			String desc = InputUtil.getStringInput();

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
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "수입지출결의서가 작성되었습니다.");
			System.out.println();
			UIUtil.printSeparator('━');
			return money;
		}
		// 수입일 경우
	}

	/**
	 * 수입/지출 내역, 자산 및 재무보고서를 조회하는 메서드
	 * 
	 * @param in Scanner
	 */
	private void getAssetData() {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			UIUtil.printSeparator('━');
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX
					+ "1: 한줄 재무 보고서 출력, 2: 자본금 조회, 3: 순이익 조회, 4:총 수입금액, 5: 총 지출금액, 6: 수입 전체 조회, 7: 지출 전체 조회, 0: 뒤로가기");
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "메뉴를 고르세요 ▶");
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> System.out.println(generateReport());
			case 2 -> System.out.println(getCapital());
			case 3 -> System.out.println(getNetProfit());
			case 4 -> System.out.println(getTotalIncomesString());
			case 5 -> System.out.println(getTotalExpendsString());
			case 6 -> getIncomesList();
			case 7 -> getExpendList();
			case 0 -> goBack(run);
			default -> wrongIndex();
			}
		}
	}

	// Getter & Setter
	// 수입 내역 리스트 반환
	// private List<IncomeExpend> getIncomes() {
	// return incomes;
	// }
	private void getIncomesList() {
		incomes.stream().forEach(t -> System.out.println(t));
	}

	// 지출 내역 리스트 반환
	// private List<IncomeExpend> getExpends() {
	// return expends;
	// }
	private void getExpendList() {
		expends.stream().forEach(t -> System.out.println(t));
	}

	/**
	 * 현재 자본(총자산) 반환
	 * 
	 * @return capital
	 */
	private String formattingMoney(long money) {
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		return formatter.format(money) + " 원 ";
	}

	/**
	 * 현재 자본(총자산) 반환
	 * 
	 * @return capital String
	 */
	private String getCapital() {
		return formattingMoney(capital);
	}

	/**
	 * 순이익 계산
	 * 
	 * @return 총수입 - 총지출 String
	 */
	private String getNetProfit() {
		Long result = getTotalIncomes() - getTotalExpends();
		return formattingMoney(result);
	}

	/**
	 * 총수입 계산
	 * 
	 * @return 수입 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalIncomes() {
		OptionalLong incomeReduce = incomes.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return incomeReduce.orElse(0l);
	}

	/**
	 * 총수입 계산
	 * 
	 * @return 수입 리스트 합계 (없으면 0 반환) String
	 */
	private String getTotalIncomesString() {
		return formattingMoney(getTotalIncomes());
	}

	/**
	 * 총지출 계산
	 * 
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalExpends() {
		OptionalLong expendsReduce = expends.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return expendsReduce.orElse(0l);
	}

	/**
	 * 총지출 계산
	 * 
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	private String getTotalExpendsString() {
		OptionalLong expendsReduce = expends.stream().mapToLong((val) -> val.money).reduce((x, y) -> x + y);
		return formattingMoney(expendsReduce.orElse(0l));
	}

	/**
	 * 재무 보고서 생성
	 * 
	 * @return 문자열 형태의 요약 보고서
	 */

	private String generateReport() {
		return String.format(MenuUtil.DEFAULT_PREFIX + "[총 자산 : %s , 총 수입 : %s , 총 지출 : %s , 순이익 : %s]", getCapital(),
				getTotalIncomesString(), getTotalExpendsString(), getNetProfit());
	}

	/**
	 * 자본 수정 메서드
	 * - 현재 자본(capital)에 파라미터 값을 누적
	 *   ex) setCapital(500) → 자본 +500
	 *   ex) setCapital(-500) → 자본 -500
	 *   
	 *   @param money Long type
	 */
	private void setCapital(Long money) {
		this.capital += money;
	}

	/**
	 * 지본금을 수정하는 메뉴 분기
	 */
	private void editAssetData() {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			UIUtil.printSeparator('━');
			System.out.println(MenuUtil.DEFAULT_PREFIX + "1: 자본금 수정, 0: 뒤로가기");
			System.out.println();
			System.out.println(MenuUtil.DEFAULT_PREFIX + "메뉴를 고르세요 ▶");
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> editSalary();
			case 0 -> goBack(run);
			default -> wrongIndex();
			}
		}
	}

	/**
	 * 자본금을 수정하는 메서드
	 */
	private void editSalary() {
		UIUtil.printSeparator('━');
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "추가되는 금액이나 감소하는 금액을 입력하세요.");
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "▶");
		Long money = Long.parseLong(InputUtil.getStringInput());
		setCapital(money);
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "금액이 수정되었습니다. 현재 회사의 자산은 " + capital + " 원 입니다.");
	}
	// util methods

	/**
	 * 뒤로가기 매서드 입니다.
	 * 
	 * 일반 boolean은 매개변수로 보낼때 변경이 안된다고 합니다.
	 * AtomicBoolean을 사용해 bool값을 변경할 수 있습니다.
	 * 
	 * @param run
	 */
	private void goBack(AtomicBoolean run) {
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "뒤로갑니다.");
		System.out.println();
		run.set(false);
	}

	private void wrongIndex() {
		System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 번호를 입력하셨습니다.");
	}
}

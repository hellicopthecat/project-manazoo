package app.finance;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import app.common.DatabaseIdGenerator;
import app.common.InputUtil;
import app.common.ui.MenuUtil;
import app.common.ui.TableUtil;
import app.common.ui.TextArtUtil;
import app.common.ui.UIUtil;
import app.incomeExpend.EventType;
import app.incomeExpend.IEConverter;
import app.incomeExpend.IncomeExpend;
import app.incomeExpend.IncomeExpendType;
import app.repository.jdbc.JdbcIncomeExpendRepository;

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

	// Repository를 통한 데이터 관리
	private final JdbcIncomeExpendRepository jdbcIERepository = JdbcIncomeExpendRepository.getInstance();

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
//		setCapital(incomeReduce - expendReduce);
	}

	public void handleFinanceManagement() {
		AtomicBoolean run = new AtomicBoolean(true);
		UIUtil.printSeparator('━');
		TextArtUtil.printFinanceMenuTitle();
		UIUtil.printSeparator('━');
		while (run.get()) {
//			String[] menu = { "수입/지출서 작성", "수입/지출 조회", "자본금 수정" };
			String[] menu = { "수입/지출서 작성", "수입/지출 조회" };
			String[] s_menu = { "뒤로가기" };
			MenuUtil.generateMenuWithTextTitle("재 정 관 리", menu, s_menu);
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> useMoney();
			case 2 -> getAssetData();
//			case 3 -> editAssetData();
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

//			String id = IdGeneratorUtil.generateId();
			String id = DatabaseIdGenerator.generateId();
			// 수입/지출 객체 생성
			if (ieType == IncomeExpendType.INCOME) {
				// 자본증가
				capital += money;
				// Repository를 통한 데이터 저장
			} else {
				// 지출일경우
				// 자본감소
				capital -= money;
				// Repository를 통한 데이터 저장
			}
			int inex = jdbcIERepository.createInEx(id, money, desc, ieNum, eventNum);
			if (inex > 0) {
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + "수입지출결의서가 작성되었습니다.");
				System.out.println();
				return money;
			} else {
				System.out.println();
				System.out.println(MenuUtil.DEFAULT_PREFIX + "수입지출결의서 작성이 실패했습니다.");
				System.out.println();
			}
		}
		// 수입일 경우
	}

	/**
	 * 수입/지출 내역, 자산 및 재무보고서를 조회하는 메서드
	 */
	private void getAssetData() {
		AtomicBoolean run = new AtomicBoolean(true);
		while (run.get()) {
			String[] menu = { "한줄 재무 보고서 출력", "자본금 조회", "순이익 조회", "총 수입금액", "총 지출금액", "수입 전체 조회", "지출 전체 조회" };
			String[] s_menu = { "뒤로가기" };
			MenuUtil.generateMenuWithTextTitle("재 정 정 보", menu, s_menu);
			int index = InputUtil.getIntInput();
			switch (index) {
			case 1 -> generateReport();
			case 2 -> getCapitalTable();
			case 3 -> getNetProfitTable();
			case 4 -> getTotalIncomesStringTable();
			case 5 -> getTotalExpendsStringTable();
			case 6 -> getIncomesList();
			case 7 -> getExpendList();
			case 0 -> goBack(run);
			default -> wrongIndex();
			}
		}
	}

	// Getter & Setter
	/**
	 * 리스트를 표로 전환해주는 메서드 입니다.
	 * 
	 * inex는 incomeExpend의 리스트를 받아옵니다.
	 * in은 boolean으로 true면 수입 false면 지출입니다.
	 * 
	 * @param inex
	 * @param in
	 */
	private void inexTable(List<IncomeExpend> inex, boolean in) {
		if (inex == null) {
			String noDataTitle = "데이터 없음";
			String[] noDataHeaders = { "No Data" };
			String[] noDataValues = { "No Data" };
			TableUtil.printSingleRowTable(noDataTitle, noDataHeaders, noDataValues);
		} else {
			String title = in ? "수입 리스트" : "지출 리스트";
			String[] headers = { "ID", "Type", "Event", "Amount", "Description" };
			String[][] data = new String[inex.size()][5];
			for (int i = 0; i < inex.size(); i++) {
				IncomeExpend income = inex.get(i);
				data[i][0] = income.getId();
				data[i][1] = IEConverter.IETypeStringConverter(income.IEType);
				data[i][2] = IEConverter.eventTypeStringConverter(income.eventType);
				data[i][3] = income.money + "";
				data[i][4] = income.desc;
			}
			TableUtil.printTable(title, headers, data);
		}
	}

	/**
	 * 수입 내역 리스트
	 */
	private void getIncomesList() {
		List<IncomeExpend> il = jdbcIERepository.getIncomeList();
		inexTable(il, true);
	}

	/**
	 * 지출 내역 리스트 
	 */
	private void getExpendList() {
		List<IncomeExpend> el = jdbcIERepository.getExpenseList();
		inexTable(el, false);
	}

	/**
	 * 현재 자본(총자산) 반환
	 * 
	 * @return capital
	 */
	private String formattingMoney(long money) {
		NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
		return "₩ " + formatter.format(money);
	}

	private void singleMoneyTable(String title, String[] header, String[] data) {
		TableUtil.printSingleRowTable(title, header, data);
	}

	/**
	 * 현재 자본(총자산) 반환
	 * 
	 * @return capital String
	 */
	private String getCapital() {
		return formattingMoney(capital + getTotalIncomes() - getTotalExpends());
	}

	private void getCapitalTable() {
		singleMoneyTable("Total Capital", new String[] { "Total Capital" }, new String[] { getCapital() });
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

	private void getNetProfitTable() {
		singleMoneyTable("NetProfit", new String[] { "Net Profit" }, new String[] { getNetProfit() });
	}

	/**
	 * 총수입 계산
	 * 
	 * @return 수입 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalIncomes() {
		List<IncomeExpend> il = jdbcIERepository.getIncomeList();
		il.stream().map(ie -> ie.money).reduce(0l, (x, y) -> x + y);
		return il.stream().map(ie -> ie.money).reduce(0l, (x, y) -> x + y);
	}

	/**
	 * 총수입 계산
	 * 
	 * @return 수입 리스트 합계 (없으면 0 반환) String
	 */
	private String getTotalIncomesString() {
		return formattingMoney(getTotalIncomes());
	}

	private void getTotalIncomesStringTable() {
		singleMoneyTable("Total Incomes", new String[] { "Total Incomes" }, new String[] { getTotalIncomesString() });
	}

	/**
	 * 총지출 계산
	 * 
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	private Long getTotalExpends() {
		List<IncomeExpend> il = jdbcIERepository.getExpenseList();
		il.stream().map(ie -> ie.money).reduce(0l, (x, y) -> x + y);
		return il.stream().map(ie -> ie.money).reduce(0l, (x, y) -> x + y);
	}

	/**
	 * 총지출 계산
	 * 
	 * @return 지출 리스트 합계 (없으면 0 반환)
	 */
	private String getTotalExpendsString() {
		return formattingMoney(getTotalExpends());
	}

	private void getTotalExpendsStringTable() {
		singleMoneyTable("Total Expends", new String[] { "Total Expends" }, new String[] { getTotalExpendsString() });
	}

	/**
	 * 재무 보고서 생성
	 * 
	 * @return 문자열 형태의 요약 보고서
	 */

	private void generateReport() {
		String title = "Asset Report";
		String[] dataHeaders = { "Total Asset", "Total Income", "Total Expend", "NetProfit" };
		String[] dataValues = { getCapital(), getTotalIncomesString(), getTotalExpendsString(), getNetProfit() };
		TableUtil.printSingleRowTable(title, dataHeaders, dataValues);
	}

	// finance table 생성이 되어야 해당 기능 사용가능
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
			String[] menu = { "자본금 수정" };
			String[] s_menu = { "뒤로가기" };
			MenuUtil.generateMenuWithTextTitle("자 본 금 수 정", menu, s_menu);
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
		System.out.println(MenuUtil.DEFAULT_PREFIX + "금액이 수정되었습니다. 현재 회사의 자산은 " + getCapital() + " 입니다.");
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
		System.out.println();
		System.out.println(MenuUtil.DEFAULT_PREFIX + "잘못된 번호를 입력하셨습니다.");
		System.out.println();
	}
}

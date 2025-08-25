package app.incomeExpend;

/**
 * IncomeExpend 클래스
 * -------------------
 * - 수입/지출 내역을 나타내는 모델 클래스
 * - 금액, 수입/지출 구분, 이벤트 타입(어떤 항목에 대한 지출/수입인지), 설명 등을 포함
 */
public class IncomeExpend {
	// 고유 ID - ID generator 업데이트 후 사용 예정
	String id;
	// 거래 금액
	public Long money;
	// 수입(INCOME) 또는 지출(EXPEND) 타입
	public IncomeExpendType spendType;
	// 구체적인 이벤트 타입 (입장료, 월급, 식비 등)
	public EventType eventType;
	// 설명 (해당 수입/지출에 대한 부가 설명)
	public String desc;

	/**
	 * 생성자
	 * @param money 거래 금액
	 * @param desc 설명
	 * @param spendType 수입/지출 구분
	 * @param eventType 구체적인 이벤트 구분
	 */
	public IncomeExpend(Long money, String desc, IncomeExpendType spendType, EventType eventType) {
		super();
		this.money = money;
		this.spendType = spendType;
		this.eventType = eventType;
		this.desc = desc;
	}

	/**
	 * 객체 정보를 문자열로 변환 (출력용)
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String convertSpendType = spendTypeConverter(spendType);
		String convertEventType = eventTypeConverter(eventType);
		return String.format("금액 : %d , 수입지출타 : %s , 수입지출모델 : %s , 설명 : %s]", money, convertSpendType, convertEventType,
				desc);
	}

	/**
	 * 수입/지출 타입을 한글로 변환
	 */
	private String spendTypeConverter(IncomeExpendType spendType) {
		String convert = switch (spendType) {
		case INCOME -> "수입";
		case EXPEND -> "지출";
		default -> throw new IllegalArgumentException(spendType + "은 잘못된 타입입니다.");
		};
		return convert;
	}

	/**
	 * 이벤트 타입을 한글로 변환
	 */
	private String eventTypeConverter(EventType eventType) {
		String convert = switch (eventType) {
		case FEE -> "입장료";
		case EMPLOYEE_MONTH -> "월급";
		case EMPLOYEE_EXTRA -> "수당";
		case ENCLOSURE -> "사육장 시설";
		case SAFARI -> "사파리";
		case AQUASHOW -> "아쿠아쇼";
		case EXPERIENCE -> "체험";
		case FOOD -> "식비";
		default -> throw new IllegalArgumentException(eventType + "은 잘못된 타입입니다.");
		};
		return convert;
	}
}

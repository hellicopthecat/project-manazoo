package app.incomeExpend;

public class IEConverter {
	/**
	 * 이벤트 번호를 EventType Enum으로 변환
	 * 
	 * @param eventNum 이벤트 번호
	 * @return EventType
	 */
	// error eventNum + "은 잘못된 번호 입니다."
	public static EventType eventTypeConverter(int eventNum) {
		EventType eventType = switch (eventNum) {
		case 1 -> EventType.FEE;
		case 2 -> EventType.SAFARI;
		case 3 -> EventType.AQUASHOW;
		case 4 -> EventType.EXPERIENCE;
		case 5 -> EventType.FOOD;
		case 6 -> EventType.ENCLOSURE;
		case 7 -> EventType.EMPLOYEE_MONTH;
		case 8 -> EventType.EMPLOYEE_EXTRA;
		default -> null;
		};
		return eventType;
	}

	/**
	 * 이벤트 타입을 한글로 변환
	 * 
	 * @param eventType EventType
	 * @return convert String
	 */
	public static String eventTypeStringConverter(EventType eventType) {
		String convert = switch (eventType) {
		case FEE -> "Fee";
		case EMPLOYEE_MONTH -> "Salary";
		case EMPLOYEE_EXTRA -> "Extra";
		case ENCLOSURE -> "Enclosure";
		case SAFARI -> "Safari";
		case AQUASHOW -> "Aquashow";
		case EXPERIENCE -> "Experience";
		case FOOD -> "Food";
		default -> null;
		};
		return convert;
	}

	/**
	 * 수입/지출 번호를 IncomeExpendType Enum으로 변환
	 * 
	 * @param ieNum 1: 수입, 2: 지출
	 * @return IncomeExpendType
	 */
	public static IncomeExpendType IETypeConverter(int ieNum) {
		IncomeExpendType ieType = switch (ieNum) {
		case 1 -> IncomeExpendType.INCOME;
		case 2 -> IncomeExpendType.EXPENSE;
		default -> null;
		};
		return ieType;
	}

	/**
	 * 수입/지출 타입을 한글로 변환
	 * 
	 * @param spendType Enum IncomeExpendType
	 * @return convert String
	 */
	public static String IETypeStringConverter(IncomeExpendType spendType) {
		String convert = switch (spendType) {
		case INCOME -> "Income";
		case EXPENSE -> "Expense";
		default -> null;
		};
		return convert;
	}
}

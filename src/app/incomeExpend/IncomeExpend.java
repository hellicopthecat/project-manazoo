package app.incomeExpend;

import app.common.ui.MenuUtil;

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
	public IncomeExpendType IEType;
	// 구체적인 이벤트 타입 (입장료, 월급, 식비 등)
	public EventType eventType;
	// 설명 (해당 수입/지출에 대한 부가 설명)
	public String desc;

	/**
	 * 생성자
	 * @param money 거래 금액
	 * @param desc 설명
	 * @param eventType 구체적인 이벤트 구분
	 */
	public IncomeExpend(String id, Long money, String desc, IncomeExpendType IEType, EventType eventType) {
		this.id = id;
		this.money = money;
		this.IEType = IEType;
		this.eventType = eventType;
		this.desc = desc;
	}

	/**
	 * 객체 정보를 문자열로 변환 (출력용)
	 */
	@Override
	public String toString() {
		String convertIEType = IEConverter.IETypeStringConverter(IEType);
		String convertEventType = IEConverter.eventTypeStringConverter(eventType);
		return String.format(MenuUtil.DEFAULT_PREFIX + "[id: %s , 금액 : %d , 수입지출타 : %s , 수입지출모델 : %s , 설명 : %s]\n", id,
				money, convertIEType, convertEventType, desc);
	}

	/**
	 * ID getter 메서드
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * ID setter 메서드
	 * @param id 설정할 ID
	 */
	public void setId(String id) {
		this.id = id;
	}

}

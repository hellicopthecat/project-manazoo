package app.incomeExpend;

/**
 * EventType 열거형(Enum)
 * ----------------------
 * - 수입/지출의 구체적인 항목(이벤트 종류)을 정의
 * - 입장료 (FEE), 
 * - 월급 (EMPLOYEE_MONTH),
 * - 수당 (EMPLOYEE_EXTRA),
 * - 사육장 관리비 (ENCLOSURE),
 * - 사파	리 (SAFARI),
 * - 아쿠아쇼 (AQUASHOW),
 * - 체험 (EXPERIENCE),
 * - 식비(FOOD)
 */
public enum EventType {
	FEE, EMPLOYEE_MONTH, EMPLOYEE_EXTRA, ENCLOSURE, SAFARI, AQUASHOW, EXPERIENCE, FOOD
}

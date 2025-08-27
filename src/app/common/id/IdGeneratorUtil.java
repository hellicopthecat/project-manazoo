package app.common.id;

import java.util.HashSet;
import java.util.Set;

/**
 * ID 생성을 담당하는 유틸리티 클래스입니다.
 * 호출자 클래스를 자동 감지하여 적절한 접두사로 고유한 ID를 생성합니다.
 * 
 * <p>지원되는 Manager 클래스:
 * <ul>
 *   <li>EnclosureManager: E-0001, E-0002, E-0003, ...</li>
 *   <li>AnimalManager: A-0001, A-0002, A-0003, ...</li>
 *   <li>ZooKeeperManager: K-0001, K-0002, K-0003, ...</li>
 * </ul>
 * 
 * <p>사용법:
 * <pre>{@code
 * // EnclosureManager.java에서 호출
 * String id = IdGeneratorUtil.generateId(); // "E-0001" 반환
 * }</pre>
 */
public final class IdGeneratorUtil {

	/**
	 * ID 타입을 정의하는 열거형입니다.
	 * 각 타입별로 고유한 접두사를 가집니다.
	 */
	public enum IdType {
		/** 사육장 타입 (접두사: E) */
		ENCLOSURE("E"),
		/** 동물 타입 (접두사: A) */
		ANIMAL("A"),
		/** 사육사 타입 (접두사: K) */
		ZOOKEEPER("K"),
		/** 방문객 타입 (접두사: V) */
		VISITOR("V");

		private final String prefix;

		IdType(String prefix) {
			this.prefix = prefix;
		}

		/**
		 * ID 접두사를 반환합니다.
		 * @return ID 접두사 문자열
		 */
		public String getPrefix() {
			return prefix;
		}
	}

	// ==================== 내부 데이터 저장소 ====================

	/** 사육장 ID 저장소 - 생성된 모든 사육장 ID를 추적 */
	private static final Set<String> usedEnclosureIds = new HashSet<>();
	/** 동물 ID 저장소 - 생성된 모든 동물 ID를 추적 */
	private static final Set<String> usedAnimalIds = new HashSet<>();
	/** 사육사 ID 저장소 - 생성된 모든 사육사 ID를 추적 */
	private static final Set<String> usedZooKeeperIds = new HashSet<>();
	/** 방문객 ID 저장소 - 생성된 모든 방문객 ID를 추적 */
	private static final Set<String> usedVisitorIds = new HashSet<>();

	/** 사육장 ID 카운터 - 다음에 생성될 사육장 ID 번호 */
	private static int enclosureCounter = 1;
	/** 동물 ID 카운터 - 다음에 생성될 동물 ID 번호 */
	private static int animalCounter = 1;
	/** 사육사 ID 카운터 - 다음에 생성될 사육사 ID 번호 */
	private static int zooKeeperCounter = 1;
	/** 방문객 ID 카운터 - 다음에 생성될 방문객 ID 번호 */
	private static int visitorCounter = 1;

	private IdGeneratorUtil() {
	}

	// ==================== 내부 유틸리티 메서드 ====================

	/**
	 * 타입별 사용된 ID Set을 반환합니다.
	 * @param type ID 타입
	 * @return 해당 타입의 사용된 ID 저장소
	 */
	private static Set<String> getUsedIdsSet(IdType type) {
		return switch (type) {
		case ENCLOSURE -> usedEnclosureIds;
		case ANIMAL -> usedAnimalIds;
		case ZOOKEEPER -> usedZooKeeperIds;
		case VISITOR -> usedVisitorIds;
		};
	}

	/**
	 * 타입별 현재 카운터 값을 반환합니다.
	 * @param type ID 타입
	 * @return 해당 타입의 현재 카운터 값
	 */
	private static int getCounter(IdType type) {
		return switch (type) {
		case ENCLOSURE -> enclosureCounter;
		case ANIMAL -> animalCounter;
		case ZOOKEEPER -> zooKeeperCounter;
		case VISITOR -> visitorCounter;
		};
	}

	/**
	 * 타입별 카운터를 새로운 값으로 업데이트합니다.
	 * @param type ID 타입
	 * @param newValue 새로운 카운터 값
	 */
	private static void updateCounter(IdType type, int newValue) {
		switch (type) {
		case ENCLOSURE -> enclosureCounter = newValue;
		case ANIMAL -> animalCounter = newValue;
		case ZOOKEEPER -> zooKeeperCounter = newValue;
		case VISITOR -> visitorCounter = newValue;
		}
	}

	/**
	 * 스택 트레이스를 분석하여 호출자 클래스를 기반으로 ID 타입을 자동 결정합니다.
	 * 
	 * @return 결정된 ID 타입
	 * @throws IllegalStateException 지원되지 않는 클래스에서 호출된 경우
	 */
	private static IdType determineIdType() {
		StackTraceElement[] stackTrace = Thread.currentThread()
				.getStackTrace();

		for (StackTraceElement element : stackTrace) {
			String className = element.getClassName();

			if (className.contains("EnclosureManager")) {
				return IdType.ENCLOSURE;
			} else if (className.contains("AnimalManager")) {
				return IdType.ANIMAL;
			} else if (className.contains("ZooKeeperManager")) {
				return IdType.ZOOKEEPER;
			} else if (className.contains("VisitorManager")) { // ✅ 추가
				return IdType.VISITOR;
			}
		}

		throw new IllegalStateException(
				"ID 생성 요청이 지원되지 않는 클래스에서 호출되었습니다. "
						+ "EnclosureManager, AnimalManager, ZooKeeperManager, VisitorManager에서만 호출 가능합니다.");
	}

	// ==================== 공개 ID 생성 메서드 ====================

	/**
	 * 호출자를 자동 감지하여 적절한 ID를 생성합니다.
	 * 
	 * <p>스택 트레이스를 분석하여 호출한 Manager 클래스를 확인하고, 
	 * 해당 타입에 맞는 ID를 자동으로 생성합니다.
	 * 
	 * <p>생성 규칙:
	 * <ul>
	 *   <li>EnclosureManager → E-0001, E-0002, ...</li>
	 *   <li>AnimalManager → A-0001, A-0002, ...</li>
	 *   <li>ZooKeeperManager → K-0001, K-0002, ...</li>
	 * </ul>
	 *
	 * @return 생성된 고유 ID (예: "E-0001")
	 * @throws IllegalStateException 지원되지 않는 클래스에서 호출된 경우
	 */
	public static String generateId() {
		IdType type = determineIdType();
		Set<String> usedIds = getUsedIdsSet(type);
		int counter = getCounter(type);
		String prefix = type.getPrefix();

		String id;
		do {
			id = String.format("%s-%04d", prefix, counter++);
		} while (usedIds.contains(id));

		usedIds.add(id);
		updateCounter(type, counter);
		return id;
	}

	// ==================== 통계 조회 메서드 ====================

	/**
	 * 현재 생성된 사육장 ID의 총 개수를 반환합니다.
	 * @return 사육장 ID 개수
	 */
	public static int getEnclosureIdCount() {
		return usedEnclosureIds.size();
	}

	/**
	 * 현재 생성된 동물 ID의 총 개수를 반환합니다.
	 * @return 동물 ID 개수
	 */
	public static int getAnimalIdCount() {
		return usedAnimalIds.size();
	}

	/**
	 * 현재 생성된 사육사 ID의 총 개수를 반환합니다.
	 * @return 사육사 ID 개수
	 */
	public static int getZooKeeperIdCount() {
		return usedZooKeeperIds.size();
	}

	// ==================== 디버그/테스트용 메서드 ====================

	/**
	 * 🔍 [DEBUG] ID 사용 여부 확인을 위한 공통 로직입니다.
	 * 
	 * <p><strong>⚠️ 이 메서드는 디버깅 및 테스트 목적으로만 사용하세요.</strong>
	 * 
	 * @param id 확인할 ID
	 * @param usedIds 사용된 ID를 저장하는 Set
	 * @return 사용된 ID면 true, 아니면 false
	 */
	private static boolean isIdUsed(String id, Set<String> usedIds) {
		return usedIds.contains(id);
	}

	/**
	 * 🔍 [DEBUG] 특정 사육장 ID가 이미 사용되었는지 확인합니다.
	 * 
	 * <p><strong>⚠️ 디버깅 및 중복 확인 목적으로만 사용하세요.</strong>
	 * 
	 * @param id 확인할 사육장 ID (예: "E-0001")
	 * @return 사용된 ID면 true, 아니면 false
	 */
	public static boolean isEnclosureIdUsed(String id) {
		return isIdUsed(id, usedEnclosureIds);
	}

	/**
	 * 🔍 [DEBUG] 특정 동물 ID가 이미 사용되었는지 확인합니다.
	 * 
	 * <p><strong>⚠️ 디버깅 및 중복 확인 목적으로만 사용하세요.</strong>
	 * 
	 * @param id 확인할 동물 ID (예: "A-0001")
	 * @return 사용된 ID면 true, 아니면 false
	 */
	public static boolean isAnimalIdUsed(String id) {
		return isIdUsed(id, usedAnimalIds);
	}

	/**
	 * 🔍 [DEBUG] 특정 사육사 ID가 이미 사용되었는지 확인합니다.
	 * 
	 * <p><strong>⚠️ 디버깅 및 중복 확인 목적으로만 사용하세요.</strong>
	 * 
	 * @param id 확인할 사육사 ID (예: "K-0001")
	 * @return 사용된 ID면 true, 아니면 false
	 */
	public static boolean isZooKeeperIdUsed(String id) {
		return isIdUsed(id, usedZooKeeperIds);
	}

	/**
	 * 🔍 [DEBUG] 현재 저장된 모든 ID들을 타입별로 나열합니다.
	 * 
	 * <p><strong>⚠️ 디버깅 및 개발 목적으로만 사용하세요.</strong>
	 * 
	 * @return 타입별로 구분된 ID 목록 문자열
	 */
	public static String getStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append("IdGeneratorUtil 현재 상태:\n");

		sb.append("├─ 사육장 ID (").append(usedEnclosureIds.size())
				.append("개): ");
		if (usedEnclosureIds.isEmpty()) {
			sb.append("없음");
		} else {
			sb.append(usedEnclosureIds.toString());
		}
		sb.append("\n");

		sb.append("├─ 동물 ID (").append(usedAnimalIds.size())
				.append("개): ");
		if (usedAnimalIds.isEmpty()) {
			sb.append("없음");
		} else {
			sb.append(usedAnimalIds.toString());
		}
		sb.append("\n");

		sb.append("└─ 사육사 ID (").append(usedZooKeeperIds.size())
				.append("개): ");
		if (usedZooKeeperIds.isEmpty()) {
			sb.append("없음");
		} else {
			sb.append(usedZooKeeperIds.toString());
		}

		return sb.toString();
	}

}

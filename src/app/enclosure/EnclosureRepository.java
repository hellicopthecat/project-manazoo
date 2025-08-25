package app.enclosure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 우리(사육장) 데이터를 메모리에서 관리하는 저장소 클래스입니다. 싱글톤 패턴을 사용하여 애플리케이션 전체에서 하나의 인스턴스만 존재합니다.
 */
public class EnclosureRepository {

	private static EnclosureRepository instance;
	private final Map<String, Object> enclosures;

	/**
	 * private 생성자로 외부에서 직접 인스턴스 생성을 방지합니다.
	 */
	private EnclosureRepository() {
		this.enclosures = new HashMap<>();
	}

	/**
	 * EnclosureRepository의 싱글톤 인스턴스를 반환합니다.
	 * 
	 * @return EnclosureRepository 인스턴스
	 */
	public static EnclosureRepository getInstance() {
		if (instance == null) {
			instance = new EnclosureRepository();
		}
		return instance;
	}

	/**
	 * 우리를 저장소에 저장합니다.
	 * 
	 * @param id        우리 고유 식별자
	 * @param enclosure 저장할 우리 객체
	 */
	public void save(String id, Object enclosure) {
		enclosures.put(id, enclosure);
	}

	/**
	 * ID로 우리를 조회합니다.
	 * 
	 * @param id 우리 고유 식별자
	 * @return 조회된 우리 객체, 없으면 null
	 */
	public boolean findById(String id) {
		return enclosures.containsKey(id);
	}

	/**
	 * 모든 우리 목록을 반환합니다.
	 * 
	 * @return 모든 우리 객체의 컬렉션
	 */
	public Collection<Object> findAll() {
		return enclosures.values();
	}

	/**
	 * ID로 우리를 삭제합니다.
	 * 
	 * @param id 삭제할 우리의 고유 식별자
	 * @return 삭제된 우리 객체, 없으면 null
	 */
	public Object deleteById(String id) {
		return enclosures.remove(id);
	}

	/**
	 * 저장소가 비어있는지 확인합니다.
	 * 
	 * @return 비어있으면 true, 아니면 false
	 */
	public boolean isEmpty() {
		return enclosures.isEmpty();
	}

	/**
	 * 저장소의 우리 수를 반환합니다.
	 * 
	 * @return 우리 수
	 */
	public int size() {
		return enclosures.size();
	}

	@Override
	public String toString() {
		if (enclosures.isEmpty()) {
			return "등록된 사육장이 없습니다.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n=== 사육장 목록 ===\n");

		for (Map.Entry<String, Object> entry : enclosures.entrySet()) {
			Enclosure enclosure = (Enclosure) entry.getValue();

			sb.append(String.format("""
					[%s]사육장 정보
					├─ 이름: %s
					├─ 크기: %.1f㎡
					├─ 온도: %.1f°C
					├─ 위치: %s
					└─ 환경: %s

					""", enclosure.getId(), enclosure.getName(), enclosure.getAreaSize(), enclosure.getTemperature(),
					getLocationTypeKorean(enclosure.getLocationType()),
					getEnvironmentTypeKorean(enclosure.getEnvironmentType())));
		}

		return sb.toString();
	}

	/**
	 * LocationType을 한국어로 변환합니다.
	 */
	private String getLocationTypeKorean(LocationType locationType) {
		return switch (locationType) {
		case INDOOR -> "실내";
		case OUTDOOR -> "야외";
		};
	}

	/**
	 * EnvironmentType을 한국어로 변환합니다.
	 */
	private String getEnvironmentTypeKorean(EnvironmentType environmentType) {
		return switch (environmentType) {
		case LAND -> "육지";
		case AQUATIC -> "수중";
		case MIXED -> "혼합";
		};
	}

}

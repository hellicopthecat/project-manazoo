package app.zooKeeper.zooKeeperEnum;

public class ZooKeeperConverter {
	/**
	 * int값으로 Gender Enum을 반환하는 메서드
	 * 
	 *  @param index
	 */
	public static Gender genderConverter(int index) {
		Gender convert = switch (index) {
		case 1 -> Gender.MALE;
		case 2 -> Gender.FEMALE;
		default -> throw new IllegalArgumentException("1 : 남성 , 2 : 여성"); // refactor 이후 수정예
		};
		return convert;
	}

	/**
	 * Gender Enum값으로 String을 반환하는 메서드
	 * 
	 *  @param g
	 */
	public static String genderStringConverter(Gender g) {
		String convert = switch (g) {
		case MALE -> "남성";
		case FEMALE -> "여성";
		};
		return convert;
	}

	/**
	 * int값으로 ZooKeeperRank Enum을 반환하는 메서드
	 * 
	 *  @param index
	 */
	public static ZooKeeperRank rankConverter(int index) {
		ZooKeeperRank convert = switch (index) {
		case 1 -> ZooKeeperRank.JUNIOR_KEEPER;
		case 2 -> ZooKeeperRank.KEEPER;
		case 3 -> ZooKeeperRank.SENIOR_KEEPER;
		case 4 -> ZooKeeperRank.HEAD_KEEPER;
		case 5 -> ZooKeeperRank.MANAGER;
		case 6 -> ZooKeeperRank.DIRECTOR;
		default ->
			throw new IllegalArgumentException("1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장"); // refactor
																														// 이후
																														// 수정예
		};
		return convert;
	}

	/**
	 * ZooKeeperRank Enum값으로 String을 반환하는 메서드
	 * 
	 *  @param keeper
	 */
	public static String rankStringConverter(ZooKeeperRank keeper) {
		String convert = switch (keeper) {
		case JUNIOR_KEEPER -> "신입사육사";
		case KEEPER -> "일반사육사";
		case SENIOR_KEEPER -> "시니어 사육사";
		case HEAD_KEEPER -> "팀장 사육사";
		case MANAGER -> "관리자";
		case DIRECTOR -> "동물원장";
		};
		return convert;
	}

	/**
	 * int값으로 Department Enum을 반환하는 메서드
	 * 
	 *  @param index
	 */
	public static Department departmentConverter(int index) {
		Department convert = switch (index) {
		case 1 -> Department.MAMMAL;
		case 2 -> Department.BIRD;
		case 3 -> Department.REPTILE;
		case 4 -> Department.FISH;
		case 5 -> Department.MIXED;
		case 6 -> Department.BREEDING_RESEARCH;
		case 7 -> Department.VETERINARY_REHAB;
		case 8 -> Department.EDUCATION;
		default -> throw new IllegalArgumentException(
				"1 : 포유류 , 2 : 조류 , 3 : 파충류 , 4 : 어류 , 5 : 양서류 , 6 : 번식/연구 , 7 : 수의/재활 , 8 : 교육"); // refactor 이후 수정예
		};
		return convert;
	}

	/**
	 * Department Enum값으로 String을 반환하는 메서드
	 * 
	 *  @param d
	 */
	public static String departmentStringConverter(Department d) {
		String convert = switch (d) {
		case MAMMAL -> "포유류부서";
		case BIRD -> "조류부서";
		case REPTILE -> "파충류부서";
		case FISH -> "어류부서";
		case MIXED -> "양서류부서";
		case BREEDING_RESEARCH -> "번식/연구";
		case VETERINARY_REHAB -> "수의/재활";
		case EDUCATION -> "교육";
		};
		return convert;
	}

	/**
	 * int값으로 boolean을 반환하는 메서드
	 * 
	 *  @param index
	 */
	public static boolean workingConverter(int index) {
		boolean convert = switch (index) {
		case 1 -> true;
		case 2 -> false;
		default -> throw new IllegalArgumentException("1 : 재직중 , 2 : 퇴사");// refactor 이후 수정예
		};
		return convert;
	}

	/**
	 * boolean값으로 String을 반환하는 메서드
	 * 
	 *  @param isWorking
	 */
	public static String workingStringConverter(boolean isWorking) {
		if (isWorking) {
			return "재직중";
		} else {
			return "퇴사";
		}
	}

	/**
	 * int값으로 boolean을 반환하는 메서드
	 * 
	 *  @param index
	 */
	public static boolean possibleImpossibleConverter(int index) {
		boolean convert = switch (index) {
		case 1 -> true;
		case 2 -> false;
		default -> throw new IllegalArgumentException("1 : 가능 , 2 : 불가능");// refactor 이후 수정예
		};
		return convert;
	}

	/**
	 * boolean값으로 String을 반환하는 메서드
	 * 
	 *  @param ok
	 */
	public static String possibleImpossibleStringConverter(boolean ok) {
		if (ok) {
			return "가능";
		} else {
			return "불가능";
		}
	}

}

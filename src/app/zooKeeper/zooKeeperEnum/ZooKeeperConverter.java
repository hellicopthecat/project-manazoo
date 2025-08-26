package app.zooKeeper.zooKeeperEnum;

public class ZooKeeperConverter {
	public static Gender genderConverter(int index) {
		Gender convert = switch (index) {
		case 1 -> Gender.MALE;
		case 2 -> Gender.FEMALE;
		default -> throw new IllegalArgumentException("1 : 남성 , 2 : 여성");
		};
		return convert;
	}

	public static ZooKeeperRank rankConverter(int index) {
		ZooKeeperRank convert = switch (index) {
		case 1 -> ZooKeeperRank.JUNIOR_KEEPER;
		case 2 -> ZooKeeperRank.KEEPER;
		case 3 -> ZooKeeperRank.SENIOR_KEEPER;
		case 4 -> ZooKeeperRank.HEAD_KEEPER;
		case 5 -> ZooKeeperRank.MANAGER;
		case 6 -> ZooKeeperRank.DIRECTOR;
		default ->
			throw new IllegalArgumentException("1 : 신입사육사 , 2 : 사육사 , 3 : 시니어 사육사 , 4 : 팀장 사육사 , 5 : 관리자 , 6 : 동물원장");
		};
		return convert;
	}

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
				"1 : 포유류 , 2 : 조류 , 3 : 파충류 , 4 : 어류 , 5 : 양서류 , 6 : 번식/연 , 7 : 수의/재활 , 8 : 교육");
		};
		return convert;
	}

	public static boolean workingConverter(int index) {
		boolean convert = switch (index) {
		case 1 -> true;
		case 2 -> false;
		default -> throw new IllegalArgumentException("1 : 재직중 , 2 : 퇴사");
		};
		return convert;
	}

	public static boolean possibleImpossibleConverter(int index) {
		boolean convert = switch (index) {
		case 1 -> true;
		case 2 -> false;
		default -> throw new IllegalArgumentException("1 : 가능 , 2 : 불가능");
		};
		return convert;
	}

}

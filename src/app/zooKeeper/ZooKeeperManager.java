package app.zooKeeper;

public class ZooKeeperManager {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		repository.put("1", new ZooKeeper("1", "이름", 30, Gender.MALE, ZooKeeperRank.JUNIOR_KEEPER, Department.BIRD,
//				true, 4, true, true, List.of("hoho")));
//		repository.put("2", new ZooKeeper("2", "이름", 30, Gender.MALE, ZooKeeperRank.DIRECTOR, Department.BIRD, true, 4,
//				true, true, List.of("hoho")));
////		System.out.println(getZooKeeperById("1"));
////		System.out.println(getZooKeeperByName("이름"));
////		setIsWorking("1", "3");
//		removeZooKeeper("2", "1");
//		System.out.println(getZooKeeperList());
		ZooKeeperRepository.registerZooKeeper();
	}

}

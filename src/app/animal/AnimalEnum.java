package app.animal;

public enum AnimalEnum {
	Lion, Tiger, Bear, Elephant, Wolf, Eagle, Owl, Snake;

	// < 입력받은 String 값이 enum에 있는지 체크하는 메소드 >
	public static boolean isValid(String input) {
		for (AnimalEnum s : values()) {
			if (s.name().equals(input)) {
				return true;
			}
		}
		return false;
	}
}

package app.animal;

public class AnimalEnum {

	public enum Species {
		사자, 호랑이, 늑대, 여우, 하마, 코끼리, 기린, 얼룩말, 코뿔소, 곰, 오랑우탄, 침팬지, 고릴라, 코알라, 미어캣,
		고슴도치, 펭귄, 공작, 타조, 앵무새, 부엉이, 독수리, 매, 황새, 팬더, 악어, 도마뱀, 이구아나, 카멜레온, 거북이, 뱀,
		코브라, 개구리, 도롱뇽, 해마;

		//	< 입력받은 String 값이 enum에 있는지 체크하는 메소드 >
		public static boolean isValid(String input) {
			for (Species s : values()) {
				if (s.name().equals(input)) {
					return true;
				}
			}
			return false;
		}
	}
}

package app.common.exception;

/**
 * 동물을 찾을 수 없을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class AnimalNotFoundException extends BusinessException {
    
    /**
     * 동물 ID로 예외를 생성합니다.
     * 
     * @param animalId 찾을 수 없는 동물 ID
     */
    public AnimalNotFoundException(int animalId) {
        super("ANIMAL_NOT_FOUND", 
              "동물을 찾을 수 없습니다. ID: " + animalId,
              "요청하신 동물 정보를 찾을 수 없습니다. 동물 ID를 다시 확인해 주세요.");
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public AnimalNotFoundException(String message) {
        super("ANIMAL_NOT_FOUND", message);
    }
}
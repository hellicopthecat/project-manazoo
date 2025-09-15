package app.controller.sample;

import app.animal.Animal;
import app.common.exception.AnimalNotFoundException;
import app.common.exception.TransactionException;
import app.common.transaction.TransactionManager;
import app.repository.interfaces.AnimalRepository;
import java.util.Optional;

/**
 * 트랜잭션 매니저 사용 예시를 보여주는 샘플 Controller입니다.
 * 실제 Animal Controller 구현 시 참고용으로 활용하세요.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class SampleAnimalController {
    
    private final AnimalRepository animalRepository;
    private final TransactionManager transactionManager;
    
    public SampleAnimalController(AnimalRepository animalRepository, 
                                TransactionManager transactionManager) {
        this.animalRepository = animalRepository;
        this.transactionManager = transactionManager;
    }
    
    /**
     * 트랜잭션을 사용한 동물 생성 예시입니다.
     * 
     * @param animal 생성할 동물
     * @return 생성된 동물
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public Animal createAnimal(Animal animal) throws TransactionException {
        return transactionManager.executeInTransaction(() -> {
            // 비즈니스 로직 검증
            if (animal.getName() == null || animal.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("동물 이름은 필수입니다.");
            }
            
            // Repository를 통한 저장
            return animalRepository.save(animal);
        });
    }
    
    /**
     * 읽기 전용 트랜잭션을 사용한 동물 조회 예시입니다.
     * 
     * @param animalId 조회할 동물 ID
     * @return 조회된 동물
     * @throws AnimalNotFoundException 동물을 찾을 수 없는 경우
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public Animal findAnimalById(int animalId) throws AnimalNotFoundException, TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            Optional<Animal> animal = animalRepository.findById(String.valueOf(animalId));
            return animal.orElseThrow(() -> new AnimalNotFoundException(animalId));
        });
    }
    
    /**
     * 복잡한 비즈니스 로직을 트랜잭션으로 처리하는 예시입니다.
     * 동물을 다른 사육장으로 이동시키는 작업입니다.
     * 
     * @param animalId 이동할 동물 ID
     * @param newEnclosureId 새로운 사육장 ID
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public void moveAnimalToEnclosure(int animalId, int newEnclosureId) throws TransactionException {
        transactionManager.executeInTransaction(() -> {
            // 1. 동물 조회
            Optional<Animal> animalOpt = animalRepository.findById(String.valueOf(animalId));
            if (animalOpt.isEmpty()) {
                throw new AnimalNotFoundException(animalId);
            }
            
            Animal animal = animalOpt.get();
            // String oldEnclosureId = animal.getEnclosureId(); // 추후 사육장 점유율 업데이트에 사용 예정
            
            // 2. 동물 사육장 변경
            animal.setEnclosureId(String.valueOf(newEnclosureId));
            animalRepository.update(animal);
            
            // 3. 사육장 점유율 업데이트 (추후 EnclosureRepository에서 구현 예정)
            // enclosureRepository.updateOccupancy(oldEnclosureId, -1);
            // enclosureRepository.updateOccupancy(newEnclosureId, 1);
            
            return null; // void 메서드이므로 null 반환
        });
    }
}
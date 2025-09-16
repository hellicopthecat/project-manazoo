package app.controller;

import app.animal.Animal;
import app.animal.AnimalEnum;
import app.common.DatabaseIdGenerator;
import app.common.SimpleLogger;
import app.common.exception.*;
import app.common.transaction.TransactionManager;
import app.common.transaction.JdbcTransactionManager;
import app.repository.interfaces.AnimalRepository;
import app.repository.JdbcAnimalRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 동물 관리 비즈니스 로직을 담당하는 컨트롤러입니다.
 * <p>
 * UI와 분리된 순수한 비즈니스 로직만을 처리하며, 모든 작업은 트랜잭션으로 보장됩니다.
 * Manager 클래스는 이 Controller를 통해서만 비즈니스 로직을 실행합니다.
 * </p>
 * 
 * @author MANAZOO Team
 * @version 2.0
 * @since 2.0
 */
public class AnimalController {
    
    // 비즈니스 규칙 상수
    private static final int ANIMAL_NAME_MIN_LENGTH = 1;
    private static final int ANIMAL_NAME_MAX_LENGTH = 50;
    private static final int ANIMAL_MIN_AGE = 0;
    private static final int ANIMAL_MAX_AGE = 100;
    
    private final AnimalRepository animalRepository;
    private final TransactionManager transactionManager;
    private final SimpleLogger logger;
    
    private static AnimalController instance;
    
    /**
     * 프라이빗 생성자입니다.
     * 싱글톤 패턴으로 구현됩니다.
     */
    private AnimalController() {
        this.animalRepository = JdbcAnimalRepository.getInstance();
        this.transactionManager = new JdbcTransactionManager();
        this.logger = SimpleLogger.getLogger(AnimalController.class);
    }
    
    /**
     * AnimalController의 싱글톤 인스턴스를 반환합니다.
     * 
     * @return AnimalController 인스턴스
     */
    public static AnimalController getInstance() {
        if (instance == null) {
            synchronized (AnimalController.class) {
                if (instance == null) {
                    instance = new AnimalController();
                }
            }
        }
        return instance;
    }
    
    /**
     * 새로운 동물을 생성하고 등록합니다.
     * <p>
     * 비즈니스 규칙:
     * <ul>
     *   <li>동물 이름은 필수이며 1-50자 제한을 따릅니다</li>
     *   <li>동물 종류는 AnimalEnum에 정의된 값이어야 합니다</li>
     *   <li>나이는 0-100세 범위를 따릅니다</li>
     *   <li>성별은 "Male" 또는 "Female"이어야 합니다</li>
     *   <li>동일한 이름의 동물이 있을 경우 경고를 발생시킵니다</li>
     * </ul>
     * </p>
     * 
     * @param name 동물 이름
     * @param species 동물 종류
     * @param age 나이
     * @param gender 성별
     * @param healthStatus 건강 상태
     * @return 생성된 동물 객체
     * @throws InvalidAnimalDataException 유효하지 않은 동물 데이터인 경우
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public Animal createAnimal(String name, String species, int age, 
                              String gender, String healthStatus) 
            throws InvalidAnimalDataException, TransactionException {
        
        return transactionManager.executeInTransaction(() -> {
            logger.info("동물 생성 시작: %s (%s, %d세)", name, species, age);
            
            // 1. 입력 데이터 유효성 검증
            validateAnimalData(name, species, age, gender, healthStatus);
            
            // 2. 동물 ID 생성
            String animalId = DatabaseIdGenerator.generateId();
            
            // 3. 중복 이름 체크 (경고용)
            List<Animal> existingAnimals = animalRepository.getAnimalsByName(name);
            if (!existingAnimals.isEmpty()) {
                logger.info("중복된 이름의 동물 생성: %s (기존 %d마리)", name, existingAnimals.size());
            }
            
            // 4. Animal 객체 생성 및 저장
            Animal savedAnimal = animalRepository.createAnimal(
                animalId, name, species, age, gender, healthStatus, null);
            
            // 5. 결과 확인 및 반환
            if (savedAnimal == null) {
                throw new TransactionException("동물 저장에 실패했습니다: " + animalId);
            }
            
            logger.info("동물 생성 완료: %s (ID: %s)", name, animalId);
            return savedAnimal;
        });
    }
    
    /**
     * 동물 정보를 수정합니다.
     * 
     * @param animalId 수정할 동물 ID
     * @param updateField 수정할 필드 ("name", "age", "health", "gender")
     * @param newValue 새로운 값
     * @return 수정된 동물 객체
     * @throws AnimalNotFoundException 동물을 찾을 수 없는 경우
     * @throws InvalidAnimalDataException 유효하지 않은 데이터인 경우
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public Animal updateAnimalInfo(String animalId, String updateField, String newValue) 
            throws AnimalNotFoundException, InvalidAnimalDataException, TransactionException {
        
        return transactionManager.executeInTransaction(() -> {
            logger.info("동물 정보 수정 시작: %s (%s -> %s)", animalId, updateField, newValue);
            
            // 1. 동물 조회
            Animal animal = findAnimalByIdInternal(animalId);
            
            // 2. 기존 값 백업 (로그용)
            String oldValue = getFieldValue(animal, updateField);
            
            // 3. 필드별 유효성 검증 및 업데이트
            switch (updateField.toLowerCase()) {
                case "name" -> {
                    validateAnimalName(newValue);
                    animal.setName(newValue);
                }
                case "age" -> {
                    int newAge = parseAndValidateAge(newValue);
                    animal.setAge(newAge);
                }
                case "health" -> {
                    validateHealthStatus(newValue);
                    animal.setHealthStatus(newValue);
                }
                case "gender" -> {
                    validateGender(newValue);
                    animal.setGender(newValue);
                }
                default -> throw new InvalidAnimalDataException(
                    "지원하지 않는 수정 필드입니다: " + updateField);
            }
            
            // 4. 저장
            animalRepository.updateAnimal(animalId, animal);
            
            logger.info("동물 정보 수정 완료: %s (%s: %s -> %s)", 
                animalId, updateField, oldValue, newValue);
            
            return animal;
        });
    }
    
    /**
     * 동물을 삭제합니다.
     * 사육장에 배치된 경우도 안전하게 처리합니다.
     * 
     * @param animalId 삭제할 동물 ID
     * @throws AnimalNotFoundException 동물을 찾을 수 없는 경우
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public void deleteAnimal(String animalId) 
            throws AnimalNotFoundException, TransactionException {
        
        transactionManager.executeInTransaction(() -> {
            logger.info("동물 삭제 시작: %s", animalId);
            
            // 1. 동물 조회
            Animal animal = findAnimalByIdInternal(animalId);
            
            // 2. 사육장 배치 해제 (필요시)
            if (animal.getEnclosureId() != null && !animal.getEnclosureId().isEmpty()) {
                logger.info("삭제 전 사육장 해제: %s", animalId);
                animal.setEnclosureId(null);
                animalRepository.updateAnimal(animalId, animal);
            }
            
            // 3. 동물 삭제
            animalRepository.removeAnimal(animalId);
            
            logger.info("동물 삭제 완료: %s (%s)", animalId, animal.getName());
            return null;
        });
    }
    
    /**
     * 동물 ID로 동물을 조회합니다.
     * 
     * @param animalId 조회할 동물 ID
     * @return 조회된 동물 객체
     * @throws AnimalNotFoundException 동물을 찾을 수 없는 경우
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public Animal findAnimalById(String animalId) 
            throws AnimalNotFoundException, TransactionException {
        
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("동물 조회: %s", animalId);
            return findAnimalByIdInternal(animalId);
        });
    }
    
    /**
     * 모든 동물 목록을 조회합니다.
     * 
     * @return 전체 동물 목록
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public List<Animal> findAllAnimals() throws TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("전체 동물 목록 조회");
            return animalRepository.findAll();
        });
    }
    
    /**
     * 이름으로 동물을 검색합니다.
     * 
     * @param name 검색할 이름
     * @return 해당 이름의 동물 목록
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public List<Animal> findAnimalsByName(String name) throws TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("이름으로 동물 검색: %s", name);
            return animalRepository.getAnimalsByName(name);
        });
    }
    
    /**
     * 종류별 동물을 조회합니다.
     * 
     * @param species 동물 종류
     * @return 해당 종류의 동물 목록
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public List<Animal> findAnimalsBySpecies(String species) throws TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("종류별 동물 조회: %s", species);
            return animalRepository.getAnimalsBySpecies(species);
        });
    }
    
    /**
     * 배치되지 않은 동물 목록을 조회합니다.
     * 
     * @return 배치 가능한 동물 목록
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public List<Animal> findUnassignedAnimals() throws TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("배치되지 않은 동물 목록 조회");
            return animalRepository.findAll().stream()
                .filter(animal -> animal.getEnclosureId() == null || animal.getEnclosureId().isEmpty())
                .collect(Collectors.toList());
        });
    }
    
    /**
     * 건강하지 않은 동물 목록을 조회합니다.
     * 
     * @return 관리가 필요한 동물 목록
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    public List<Animal> findUnhealthyAnimals() throws TransactionException {
        return transactionManager.executeInReadOnlyTransaction(() -> {
            logger.debug("건강하지 않은 동물 목록 조회");
            return animalRepository.findAll().stream()
                .filter(animal -> !"Good".equalsIgnoreCase(animal.getHealthStatus()))
                .collect(Collectors.toList());
        });
    }
    
    // ========================================
    // Private 헬퍼 메서드들
    // ========================================
    
    /**
     * 내부용 동물 조회 메서드입니다.
     * 트랜잭션 내에서 사용되므로 별도의 트랜잭션을 시작하지 않습니다.
     */
    private Animal findAnimalByIdInternal(String animalId) throws AnimalNotFoundException {
        Animal animal = animalRepository.getAnimalById(animalId);
        if (animal == null) {
            throw new AnimalNotFoundException("존재하지 않는 동물: " + animalId);
        }
        return animal;
    }
    
    /**
     * 동물 데이터 유효성을 종합적으로 검증합니다.
     */
    private void validateAnimalData(String name, String species, int age, 
                                  String gender, String healthStatus) 
            throws InvalidAnimalDataException {
        
        validateAnimalName(name);
        validateSpecies(species);
        validateAge(age);
        validateGender(gender);
        validateHealthStatus(healthStatus);
    }
    
    private void validateAnimalName(String name) throws InvalidAnimalDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidAnimalDataException("동물 이름은 필수입니다.");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < ANIMAL_NAME_MIN_LENGTH) {
            throw new InvalidAnimalDataException(
                String.format("동물 이름은 최소 %d자 이상이어야 합니다: %s", 
                    ANIMAL_NAME_MIN_LENGTH, trimmedName));
        }
        
        if (trimmedName.length() > ANIMAL_NAME_MAX_LENGTH) {
            throw new InvalidAnimalDataException(
                String.format("동물 이름은 최대 %d자까지만 가능합니다: %s", 
                    ANIMAL_NAME_MAX_LENGTH, trimmedName));
        }
    }
    
    private void validateSpecies(String species) throws InvalidAnimalDataException {
        if (species == null || species.trim().isEmpty()) {
            throw new InvalidAnimalDataException("동물 종류는 필수입니다.");
        }
        
        if (!AnimalEnum.isValid(species.trim())) {
            throw new InvalidAnimalDataException("지원하지 않는 동물 종류입니다: " + species);
        }
    }
    
    private void validateAge(int age) throws InvalidAnimalDataException {
        if (age < ANIMAL_MIN_AGE) {
            throw new InvalidAnimalDataException(
                String.format("동물 나이는 최소 %d세 이상이어야 합니다: %d", 
                    ANIMAL_MIN_AGE, age));
        }
        
        if (age > ANIMAL_MAX_AGE) {
            throw new InvalidAnimalDataException(
                String.format("동물 나이는 최대 %d세 이하여야 합니다: %d", 
                    ANIMAL_MAX_AGE, age));
        }
    }
    
    private int parseAndValidateAge(String ageStr) throws InvalidAnimalDataException {
        try {
            int age = Integer.parseInt(ageStr);
            validateAge(age);
            return age;
        } catch (NumberFormatException e) {
            throw new InvalidAnimalDataException("나이는 숫자여야 합니다: " + ageStr);
        }
    }
    
    private void validateGender(String gender) throws InvalidAnimalDataException {
        if (gender == null || gender.trim().isEmpty()) {
            throw new InvalidAnimalDataException("동물 성별은 필수입니다.");
        }
        
        String trimmedGender = gender.trim();
        if (!trimmedGender.equalsIgnoreCase("Male") && !trimmedGender.equalsIgnoreCase("Female")) {
            throw new InvalidAnimalDataException("성별은 'Male' 또는 'Female'이어야 합니다: " + gender);
        }
    }
    
    private void validateHealthStatus(String healthStatus) throws InvalidAnimalDataException {
        if (healthStatus != null && !healthStatus.trim().isEmpty()) {
            String trimmedStatus = healthStatus.trim();
            if (!trimmedStatus.equalsIgnoreCase("Good") && 
                !trimmedStatus.equalsIgnoreCase("Fair") && 
                !trimmedStatus.equalsIgnoreCase("Poor")) {
                throw new InvalidAnimalDataException(
                    "건강 상태는 'Good', 'Fair', 'Poor' 중 하나여야 합니다: " + healthStatus);
            }
        }
    }
    
    /**
     * 동물 객체에서 필드 값을 추출합니다 (로깅용).
     */
    private String getFieldValue(Animal animal, String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "name" -> animal.getName();
            case "age" -> String.valueOf(animal.getAge());
            case "health" -> animal.getHealthStatus();
            case "gender" -> animal.getGender();
            default -> "unknown";
        };
    }
}
package app.enclosure;

import java.util.Map;

import app.repository.interfaces.EnclosureRepository;

/**
 * 동물원 사육장를 위한 인터페이스로, 동물 및 사육장 속성 관리를 위한 필수 메서드를 정의합니다.
 */

public interface EnclosureInterface {

    /**
     * 사육장 저장소를 반환합니다.
     *
     * @return 사육장 저장소
     */
    EnclosureRepository getRepository();

    /**
     * 사육장 고유 식별자를 반환합니다.
     *
     * @return 사육장 ID
     */
    String getId();

    /**
     * 사육장 이름을 반환합니다.
     *
     * @return 사육장 이름
     */

    String getName();

    /**
     * 사육장 이름을 설정합니다.
     */

    void setName(String name);

    /**
     * 사육장 위치 유형(실내/실외)을 반환합니다.
     *
     * @return 위치 유형
     */
    LocationType getLocationType();

    /**
     * 사육장 위치 유형을 설정합니다.
     *
     * @param locationType 설정할 위치 유형
     */
    void setLocationType(LocationType locationType);

    /**
     * 사육장 환경 유형(육지/수생/혼합)을 반환합니다.
     *
     * @return 환경 유형
     */
    EnvironmentType getEnvironmentType();

    /**
     * 사육장 환경 유형을 설정합니다.
     *
     * @param environmentType 설정할 환경 유형
     */
    void setEnvironmentType(EnvironmentType environmentType);

    /**
     * 사육장 면적을 반환합니다.
     *
     * @return 면적(단위: 제곱미터 등)
     */
    Float getAreaSize();

    /**
     * 사육장 면적을 설정합니다.
     */
    void setAreaSize(Float areaSize);

    /**
     * 사육장 온도를 반환합니다.
     *
     * @return 온도
     */
    Float getTemperature();

    /**
     * 사육장 온도를 설정합니다.
     */
    void setTemperature(Float temperature);

    // ==================== 동물 관리 핵심 메서드 ====================
    
    /**
     * 사육장에 동물을 입주시킵니다.
     *
     * @param animalId 동물 ID
     * @param animal 동물 객체
     */
    void addInhabitant(String animalId, Object animal);

    /**
     * 사육장에서 동물을 이주시킵니다.
     *
     * @param animalId 동물 ID
     * @return 이주된 동물 객체
     */
    Object removeInhabitant(String animalId);

    /**
     * 사육장의 모든 거주 동물을 조회합니다.
     *
     * @return 동물 Map
     */
    Map<String, Object> getAllInhabitants();

    /**
     * 사육장에 거주하는 동물 수를 반환합니다.
     *
     * @return 동물 수
     */
    int getInhabitantCount();

    // ==================== 사육사 관리 핵심 메서드 ====================
    
    /**
     * 사육장에 사육사를 배정합니다.
     *
     * @param keeperId 사육사 ID
     * @param keeper 사육사 객체
     */
    void assignCaretaker(String keeperId, Object keeper);

    /**
     * 사육장에서 사육사 배정을 해제합니다.
     *
     * @param keeperId 사육사 ID
     * @return 배정 해제된 사육사 객체
     */
    Object unassignCaretaker(String keeperId);

    /**
     * 사육장에 배정된 모든 사육사를 조회합니다.
     *
     * @return 사육사 Map
     */
    Map<String, Object> getAllCaretakers();

    /**
     * 사육장에 배정된 사육사 수를 반환합니다.
     *
     * @return 사육사 수
     */
    int getCaretakerCount();

}


/**
 * 동물원 사육장를 위한 인터페이스로, 동물 및 사육장 속성 관리를 위한 필수 메서드를 정의합니다.
 */
package app.enclosure;

public interface EnclosureInterface {

    /**
     * 사육장 저장소를 반환합니다.
     * @return 사육장 저장소
     */
    EnclosureRepository getRepository();

    /**
      * 사육장 고유 식별자를 반환합니다.
      * @return 사육장 ID
      */
    String getEnclosureId();

    /**
     * 사육장 이름을 반환합니다.
     * @return 사육장 이름
     */

    String getName();

    /**
     * 사육장 이름을 설정합니다.
     */

    void setName(String name);

    /**
      * 사육장 위치 유형(실내/실외)을 반환합니다.
      * @return 위치 유형
      */
    LocationType getLocationType();

     /**
      * 사육장 환경 유형(육지/수생/혼합)을 반환합니다.
      * @return 환경 유형
      */
    EnvironmentType getEnvironmentType();

     /**
      * 사육장 면적을 반환합니다.
      * @return 면적(단위: 제곱미터 등)
      */
    Float getAreaSize();

     /**
      * 사육장 면적을 설정합니다.
      */
    void setAreaSize(Float areaSize);

     /**
      * 사육장 온도를 반환합니다.
      * @return 온도
      */
    Float getTemperature();

     /**
      * 사육장 온도를 설정합니다.
      */
    void setTemperature(Float temperature);

//    void addAnimal();
//
//    void removeAnimal(Animal animal);
//
//    void addZooKeeper();
//
//    void removeZooKeeper(ZooKeeper zooKeeper);

}

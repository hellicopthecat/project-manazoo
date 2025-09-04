package app.repository.interfaces;

import app.zooKeeper.ZooKeeper;

import java.util.List;

/**
 * ZooKeeper 엔티티를 위한 특화된 Repository 인터페이스입니다.
 * 기본 CRUD 연산 외에 ZooKeeper 특화 기능을 제공합니다.
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사육사 등록 및 관리</li>
 *   <li>부서별, 이름별 조회</li>
 *   <li>권한 기반 수정 및 삭제</li>
 *   <li>급여 관리</li>
 * </ul>
 */
public interface ZooKeeperRepository extends Repository<ZooKeeper, String> {
    
    /**
     * 상세 정보를 포함한 사육사를 생성합니다.
     * 
     * @param id                        사육사 ID
     * @param name                      이름
     * @param age                       나이
     * @param genderIndex               성별 인덱스 (1: 남성, 2: 여성)
     * @param rankIndex                 직급 인덱스 (1-6)
     * @param departmentIndex           부서 인덱스 (1-8)
     * @param isWorkingIndex            재직 상태 인덱스 (1: 재직, 2: 퇴사)
     * @param experienceYear            경력 연수
     * @param canHandleDangerIndex      위험동물 관리 가능 인덱스 (1: 가능, 2: 불가능)
     * @param qualifications            자격증 정보
     * @return 생성된 사육사 객체
     */
    ZooKeeper createZooKeeper(String id, String name, int age, int genderIndex, 
                             int rankIndex, int departmentIndex, int isWorkingIndex,
                             int experienceYear, int canHandleDangerIndex, String qualifications);
    
    /**
     * 모든 사육사 목록을 조회합니다.
     * 
     * @return 전체 사육사 목록
     */
    List<ZooKeeper> getZooKeeperList();
    
    /**
     * ID로 사육사를 조회합니다.
     * 
     * @param id 사육사 ID
     * @return 조회된 사육사 객체, 없으면 null
     */
    ZooKeeper getZooKeeperById(String id);
    
    /**
     * 이름으로 사육사를 조회합니다.
     * 동일한 이름을 가진 여러 사육사가 있을 수 있으므로 문자열로 결과를 반환합니다.
     * 
     * @param name 검색할 사육사 이름
     * @return 검색 결과를 포함한 문자열
     */
    String getZooKeeperByName(String name);
    
    /**
     * 부서별로 사육사를 조회합니다.
     * 
     * @param departmentIndex 부서 인덱스 (1-8)
     * @return 해당 부서의 사육사 목록
     */
    List<ZooKeeper> getZooKeeperByDepartment(int departmentIndex);
    
    /**
     * 사육사의 재직 상태를 변경합니다.
     * 권한 검증을 포함합니다.
     * 
     * @param myId 변경 요청자 ID
     * @param targetId 변경 대상자 ID
     * @param index 새로운 재직 상태 (1: 재직, 2: 퇴사)
     */
    void setIsWorking(String myId, String targetId, int index);
    
    /**
     * 사육사의 업무 배정 가능 상태를 변경합니다.
     * 권한 검증을 포함합니다.
     * 
     * @param myId 변경 요청자 ID
     * @param targetId 변경 대상자 ID
     * @param index 새로운 업무 배정 가능 상태 (1: 가능, 2: 불가능)
     */
    void setCanAssignTask(String myId, String targetId, int index);
    
    /**
     * 사육사의 위험동물 관리 권한을 변경합니다.
     * 권한 검증을 포함합니다.
     * 
     * @param myId 변경 요청자 ID
     * @param targetId 변경 대상자 ID
     * @param index 새로운 위험동물 관리 권한 (1: 가능, 2: 불가능)
     */
    void setPermissionDangerAnimal(String myId, String targetId, int index);
    
    /**
     * 사육사를 삭제합니다.
     * 권한 검증을 포함합니다.
     * 
     * @param myId 삭제 요청자 ID
     * @param targetId 삭제 대상자 ID
     */
    void removeZooKeeper(String myId, String targetId);
    
    /**
     * 사육사의 급여 정보를 설정합니다.
     * 권한 검증을 포함합니다.
     * 
     * @param myId 설정 요청자 ID
     * @param targetId 급여 설정 대상자 ID
     * @param money 급여 금액
     * @return 설정 성공 여부
     */
    boolean setSalary(String myId, String targetId, long money);
}

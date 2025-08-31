package app.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import app.zooKeeper.ZooKeeper;
import app.zooKeeper.zooKeeperEnum.Department;
import app.zooKeeper.zooKeeperEnum.Gender;
import app.zooKeeper.zooKeeperEnum.ZooKeeperConverter;
import app.zooKeeper.zooKeeperEnum.ZooKeeperRank;
import app.repository.interfaces.ZooKeeperRepository;

/**
 * 메모리 기반 사육사 Repository 구현체입니다.
 * 사육사 데이터를 Map에 저장하여 빠른 조회와 조작을 제공합니다.
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>메모리 기반 데이터 저장</li>
 *   <li>권한 기반 접근 제어</li>
 *   <li>타입 안전성 확보</li>
 *   <li>기존 ZooKeeperRepository와의 완전 호환</li>
 * </ul>
 */
public class MemoryZooKeeperRepository implements ZooKeeperRepository {
    
    /**
     * 사육사 데이터를 저장하는 Map
     * Key: 사육사 ID (String), Value: 사육사 객체 (ZooKeeper)
     */
    private final Map<String, ZooKeeper> zooKeepers;
    
    /**
     * 생성자 - 빈 저장소로 초기화합니다.
     */
    public MemoryZooKeeperRepository() {
        this.zooKeepers = new HashMap<>();
    }
    
    // =================================================================
    // Repository<ZooKeeper, String> 기본 CRUD 구현
    // =================================================================
    
    @Override
    public ZooKeeper save(ZooKeeper zooKeeper) {
        Objects.requireNonNull(zooKeeper, "사육사 객체는 null일 수 없습니다.");
        
        // ZooKeeper 클래스의 getId()는 protected이므로 다른 방법 사용
        String id = findZooKeeperIdFromObject(zooKeeper);
        Objects.requireNonNull(id, "사육사 ID는 null일 수 없습니다.");
        
        zooKeepers.put(id, zooKeeper);
        return zooKeeper;
    }
    
    @Override
    public Optional<ZooKeeper> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(zooKeepers.get(id));
    }
    
    @Override
    public List<ZooKeeper> findAll() {
        return zooKeepers.values().stream()
                .collect(Collectors.toList());
    }
    
    @Override
    public ZooKeeper update(ZooKeeper zooKeeper) {
        Objects.requireNonNull(zooKeeper, "사육사 객체는 null일 수 없습니다.");
        
        String id = findZooKeeperIdFromObject(zooKeeper);
        Objects.requireNonNull(id, "사육사 ID는 null일 수 없습니다.");
        
        if (!existsById(id)) {
            throw new IllegalArgumentException("수정하려는 사육사가 존재하지 않습니다: " + id);
        }
        
        zooKeepers.put(id, zooKeeper);
        return zooKeeper;
    }
    
    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        
        ZooKeeper removed = zooKeepers.remove(id);
        return removed != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return id != null && zooKeepers.containsKey(id);
    }
    
    @Override
    public void deleteAll() {
        zooKeepers.clear();
    }
    
    @Override
    public long count() {
        return zooKeepers.size();
    }
    
    // =================================================================
    // ZooKeeperRepository 특화 메서드 구현
    // =================================================================
    
    @Override
    public ZooKeeper createZooKeeper(String id, String name, int age, int genderIndex,
                                    int rankIndex, int departmentIndex, int isWorkingIndex,
                                    int experienceYear, int canHandleDangerIndex, String qualifications) {
        
        // 컨버터를 사용해 int 값을 Enum으로 변환
        Gender gender = ZooKeeperConverter.genderConverter(genderIndex);
        ZooKeeperRank rank = ZooKeeperConverter.rankConverter(rankIndex);
        Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
        boolean isWorking = ZooKeeperConverter.workingConverter(isWorkingIndex);
        boolean canHandleDanger = ZooKeeperConverter.possibleImpossibleConverter(canHandleDangerIndex);
        
        // 자격증 리스트 생성
        List<String> licenses = new ArrayList<>();
        if (qualifications != null && !qualifications.trim().isEmpty()) {
            String[] qualArray = qualifications.split(",");
            for (String qual : qualArray) {
                licenses.add(qual.trim());
            }
        }
        
        // ZooKeeper 객체 생성 (canAssignTask는 기본값 true로 설정)
        ZooKeeper zooKeeper = new ZooKeeper(id, name, age, gender, rank, department,
                                          isWorking, experienceYear, canHandleDanger, true, licenses);
        
        return save(zooKeeper);
    }
    
    @Override
    public List<ZooKeeper> getZooKeeperList() {
        return findAll();
    }
    
    @Override
    public ZooKeeper getZooKeeperById(String id) {
        return findById(id).orElse(null);
    }
    
    @Override
    public String getZooKeeperByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "검색할 이름이 입력되지 않았습니다.";
        }
        
        List<ZooKeeper> matchedKeepers = zooKeepers.values().stream()
                .filter(zk -> name.equals(zk.getName()))
                .collect(Collectors.toList());
        
        if (matchedKeepers.isEmpty()) {
            return String.format("'%s' 이름의 사육사가 없습니다.", name);
        }
        
        StringBuilder result = new StringBuilder();
        result.append(String.format("'%s' 이름의 사육사 목록:\n", name));
        for (int i = 0; i < matchedKeepers.size(); i++) {
            ZooKeeper zk = matchedKeepers.get(i);
            String id = findZooKeeperIdFromObject(zk);
            result.append(String.format("%d. %s (%s) - %s\n", 
                         i + 1, zk.getName(), id, zk.getDepartment()));
        }
        
        return result.toString();
    }
    
    @Override
    public List<ZooKeeper> getZooKeeperByDepartment(int departmentIndex) {
        Department department = ZooKeeperConverter.departmentConverter(departmentIndex);
        
        return zooKeepers.values().stream()
                .filter(zk -> department.equals(zk.getDepartment()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void setIsWorking(String myId, String targetId, int index) {
        if (!hasManagementPermission(myId, targetId)) {
            return;
        }
        
        ZooKeeper target = getZooKeeperById(targetId);
        if (target != null) {
            boolean newIsWorking = (index == 1);
            
            // 새로운 ZooKeeper 객체 생성하여 교체
            ZooKeeper updatedTarget = new ZooKeeper(
                findZooKeeperIdFromObject(target),
                target.getName(),
                target.getAge(),
                target.getGender(),
                target.getRank(),
                target.getDepartment(),
                newIsWorking,  // 업데이트할 값
                target.getExperieneceYear(),
                target.isCanHandleDangerAnimal(),
                target.isCanAssignTask(),
                target.getLicenses()
            );
            
            zooKeepers.put(findZooKeeperIdFromObject(target), updatedTarget);
        }
    }
    
    @Override
    public void setCanAssignTask(String myId, String targetId, int index) {
        if (!hasManagementPermission(myId, targetId)) {
            return;
        }
        
        ZooKeeper target = getZooKeeperById(targetId);
        if (target != null) {
            boolean newCanAssignTask = (index == 1);
            
            // 새로운 ZooKeeper 객체 생성하여 교체
            ZooKeeper updatedTarget = new ZooKeeper(
                findZooKeeperIdFromObject(target),
                target.getName(),
                target.getAge(),
                target.getGender(),
                target.getRank(),
                target.getDepartment(),
                target.isWorking(),
                target.getExperieneceYear(),
                target.isCanHandleDangerAnimal(),
                newCanAssignTask,  // 업데이트할 값
                target.getLicenses()
            );
            
            zooKeepers.put(findZooKeeperIdFromObject(target), updatedTarget);
        }
    }
    
    @Override
    public void setPermissionDangerAnimal(String myId, String targetId, int index) {
        if (!hasManagementPermission(myId, targetId)) {
            return;
        }
        
        ZooKeeper target = getZooKeeperById(targetId);
        if (target != null) {
            boolean newCanHandleDanger = (index == 1);
            
            // 새로운 ZooKeeper 객체 생성하여 교체
            ZooKeeper updatedTarget = new ZooKeeper(
                findZooKeeperIdFromObject(target),
                target.getName(),
                target.getAge(),
                target.getGender(),
                target.getRank(),
                target.getDepartment(),
                target.isWorking(),
                target.getExperieneceYear(),
                newCanHandleDanger,  // 업데이트할 값
                target.isCanAssignTask(),
                target.getLicenses()
            );
            
            zooKeepers.put(findZooKeeperIdFromObject(target), updatedTarget);
        }
    }
    
    @Override
    public void removeZooKeeper(String myId, String targetId) {
        if (!hasManagementPermission(myId, targetId)) {
            return;
        }
        
        deleteById(targetId);
    }
    
    @Override
    public boolean setSalary(String myId, String targetId, long money) {
        if (!hasManagementPermission(myId, targetId)) {
            return false;
        }
        
        ZooKeeper target = getZooKeeperById(targetId);
        if (target != null) {
            target.setSalary(money);
            update(target);
            return true;
        }
        
        return false;
    }
    
    // =================================================================
    // 권한 검증 및 헬퍼 메서드
    // =================================================================
    
    /**
     * 사육사가 다른 사육사를 관리할 권한이 있는지 확인합니다.
     * 
     * <p>권한 규칙:</p>
     * <ul>
     *   <li>자기 자신은 항상 수정 가능</li>
     *   <li>관리자 이상 직급(rank 5 이상)만 타인 관리 가능</li>
     * </ul>
     * 
     * @param managerId 권한 확인 대상 ID
     * @param targetId 관리 대상 ID
     * @return 관리 권한 보유 여부
     */
    private boolean hasManagementPermission(String managerId, String targetId) {
        if (managerId == null || targetId == null) {
            return false;
        }
        
        ZooKeeper manager = getZooKeeperById(managerId);
        ZooKeeper target = getZooKeeperById(targetId);
        
        if (manager == null || target == null) {
            return false;
        }
        
        if (managerId.equals(targetId)) {
            return true;
        }
        
        return manager.getRank().ordinal() + 1 >= 5;
    }
    
    /**
     * ZooKeeper 객체에서 ID를 추출하는 헬퍼 메서드입니다.
     * ZooKeeper의 getId()가 protected이므로 reflection 또는 저장된 맵에서 찾습니다.
     * 
     * @param zooKeeper ID를 찾을 ZooKeeper 객체
     * @return 찾은 ID 또는 null
     */
    private String findZooKeeperIdFromObject(ZooKeeper zooKeeper) {
        // 저장된 맵에서 동일한 객체를 찾아 ID 반환
        for (Map.Entry<String, ZooKeeper> entry : zooKeepers.entrySet()) {
            if (entry.getValue() == zooKeeper) {
                return entry.getKey();
            }
        }
        
        // 새로운 객체인 경우 toString()을 파싱하여 ID 추출
        String toString = zooKeeper.toString();
        if (toString.contains("id : ")) {
            int start = toString.indexOf("id : ") + 5;
            int end = toString.indexOf(" |", start);
            if (end > start) {
                return toString.substring(start, end);
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("MemoryZooKeeperRepository{size=%d}", count());
    }
}

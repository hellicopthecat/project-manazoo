package app.common.exception;

/**
 * 사육사를 찾을 수 없을 때 발생하는 예외입니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class ZooKeeperNotFoundException extends BusinessException {
    
    /**
     * 사육사 ID로 예외를 생성합니다.
     * 
     * @param zooKeeperId 찾을 수 없는 사육사 ID
     */
    public ZooKeeperNotFoundException(int zooKeeperId) {
        super("ZOOKEEPER_NOT_FOUND",
              "사육사를 찾을 수 없습니다. ID: " + zooKeeperId,
              "요청하신 사육사 정보를 찾을 수 없습니다. 사육사 번호를 다시 확인해 주세요.");
    }
    
    /**
     * 사용자 정의 메시지로 예외를 생성합니다.
     * 
     * @param message 예외 메시지
     */
    public ZooKeeperNotFoundException(String message) {
        super("ZOOKEEPER_NOT_FOUND", message);
    }
}
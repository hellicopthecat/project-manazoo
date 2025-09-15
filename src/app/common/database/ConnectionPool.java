package app.common.database;

import app.config.DatabaseConnection;
import app.common.SimpleLogger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 간단한 Connection Pool 구현체입니다.
 * 기존 DatabaseConnection과 호환되도록 설계되었습니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class ConnectionPool {
    
    private final ConcurrentLinkedQueue<Connection> availableConnections;
    private final ConcurrentLinkedQueue<Connection> usedConnections;
    private final AtomicInteger connectionCount;
    private final SimpleLogger logger;
    
    // 설정값들
    private final int minPoolSize = 5;      // 최소 연결 수
    private final int maxPoolSize = 20;     // 최대 연결 수
    private final long connectionTimeout = 30000; // 30초
    
    private static ConnectionPool instance;
    
    private ConnectionPool() {
        this.availableConnections = new ConcurrentLinkedQueue<>();
        this.usedConnections = new ConcurrentLinkedQueue<>();
        this.connectionCount = new AtomicInteger(0);
        this.logger = SimpleLogger.getLogger(ConnectionPool.class);
        
        initializePool();
    }
    
    /**
     * ConnectionPool 싱글톤 인스턴스를 반환합니다.
     * 
     * @return ConnectionPool 인스턴스
     */
    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }
    
    /**
     * 초기 연결 풀을 생성합니다.
     */
    private void initializePool() {
        logger.info("Connection Pool 초기화 시작 (최소: " + minPoolSize + ", 최대: " + maxPoolSize + ")");
        
        try {
            // 최소 연결 수만큼 미리 생성
            for (int i = 0; i < minPoolSize; i++) {
                Connection connection = createNewConnection();
                availableConnections.offer(connection);
                connectionCount.incrementAndGet();
            }
            
            logger.info("✅ Connection Pool 초기화 완료 (생성된 연결: " + connectionCount.get() + "개)");
            
        } catch (SQLException e) {
            logger.error("❌ Connection Pool 초기화 실패", e);
            throw new RuntimeException("Connection Pool 초기화 실패", e);
        }
    }
    
    /**
     * 연결 풀에서 연결을 가져옵니다.
     * 기존 DatabaseConnection.getConnection()과 호환됩니다.
     * 
     * @return 사용 가능한 Connection
     * @throws SQLException 연결 획득 실패 시
     */
    public Connection getConnection() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < connectionTimeout) {
            // 1. 사용 가능한 연결이 있는지 확인
            Connection connection = availableConnections.poll();
            
            if (connection != null && isValidConnection(connection)) {
                // 사용 중인 연결 목록으로 이동
                usedConnections.offer(connection);
                logger.debug("연결 풀에서 연결 획득 (사용 가능: " + availableConnections.size() + 
                           ", 사용 중: " + usedConnections.size() + ")");
                return connection;
            }
            
            // 2. 사용 가능한 연결이 없으면 새로 생성 시도
            if (connectionCount.get() < maxPoolSize) {
                try {
                    connection = createNewConnection();
                    connectionCount.incrementAndGet();
                    usedConnections.offer(connection);
                    
                    logger.debug("새 연결 생성 및 할당 (전체 연결: " + connectionCount.get() + ")");
                    return connection;
                    
                } catch (SQLException e) {
                    logger.error("새 연결 생성 실패", e);
                }
            }
            
            // 3. 잠시 대기 후 재시도
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("연결 대기 중 인터럽트 발생", e);
            }
        }
        
        throw new SQLException("Connection Pool에서 연결 획득 시간 초과");
    }
    
    /**
     * 사용이 끝난 연결을 풀로 반환합니다.
     * 
     * @param connection 반환할 연결
     */
    public void returnConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
        if (usedConnections.remove(connection)) {
            if (isValidConnection(connection)) {
                // 정상적인 연결은 재사용을 위해 풀로 반환
                availableConnections.offer(connection);
                logger.debug("연결 풀로 반환 (사용 가능: " + availableConnections.size() + 
                           ", 사용 중: " + usedConnections.size() + ")");
            } else {
                // 비정상 연결은 제거하고 새로 생성
                closeConnection(connection);
                connectionCount.decrementAndGet();
                logger.debug("비정상 연결 제거 (전체 연결: " + connectionCount.get() + ")");
            }
        }
    }
    
    /**
     * 새로운 데이터베이스 연결을 생성합니다.
     * 기존 DatabaseConnection을 활용합니다.
     * 
     * @return 새로운 Connection
     * @throws SQLException 연결 생성 실패 시
     */
    private Connection createNewConnection() throws SQLException {
        // 기존 DatabaseConnection을 그대로 활용!
        return DatabaseConnection.getConnection();
    }
    
    /**
     * 연결이 유효한지 확인합니다.
     * 
     * @param connection 확인할 연결
     * @return 유효성 여부
     */
    private boolean isValidConnection(Connection connection) {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            logger.debug("연결 유효성 검사 실패", e);
            return false;
        }
    }
    
    /**
     * 연결을 안전하게 닫습니다.
     * 
     * @param connection 닫을 연결
     */
    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("연결 종료 중 오류", e);
        }
    }
    
    /**
     * Connection Pool의 현재 상태를 출력합니다.
     */
    public void printPoolStatus() {
        logger.info("=== Connection Pool 상태 ===");
        logger.info("전체 연결 수: " + connectionCount.get());
        logger.info("사용 가능한 연결: " + availableConnections.size());
        logger.info("사용 중인 연결: " + usedConnections.size());
        logger.info("최소 연결 수: " + minPoolSize);
        logger.info("최대 연결 수: " + maxPoolSize);
    }
    
    /**
     * Connection Pool을 안전하게 종료합니다.
     */
    public void shutdown() {
        logger.info("Connection Pool 종료 시작...");
        
        // 모든 연결 닫기
        while (!availableConnections.isEmpty()) {
            Connection connection = availableConnections.poll();
            closeConnection(connection);
        }
        
        while (!usedConnections.isEmpty()) {
            Connection connection = usedConnections.poll();
            closeConnection(connection);
        }
        
        connectionCount.set(0);
        logger.info("✅ Connection Pool 종료 완료");
    }
}
package app.config;

import app.common.SimpleLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 클래스
 * SimpleLogger를 사용하여 간단한 로깅을 처리합니다.
 */
public class DatabaseConnection {
    
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static DatabaseConfigLoader configLoader;
    
    // 간단한 로거 인스턴스
    private static final SimpleLogger logger = SimpleLogger.getLogger(DatabaseConnection.class);
    
    // 환경 설정을 시스템 프로퍼티에서 읽어옴 (기본값: dev)
    static {
        String environment = System.getProperty("app.env", "dev");
        configLoader = new DatabaseConfigLoader(environment);
        
        // 디버그 모드에서만 초기화 메시지 출력
        logger.debug("데이터베이스 연결 관리자 초기화 - 환경: %s", environment);
    }
    
    /**
     * 데이터베이스 연결을 생성합니다.
     * 조용한 연결을 제공하며, 디버그 모드에서만 상세 메시지를 출력합니다.
     * 
     * @return MySQL 데이터베이스 연결
     * @throws SQLException 연결 실패 시 발생
     */
    public static Connection getConnection() throws SQLException {
        try {
            // 디버그 모드에서만 연결 시도 메시지
            logger.debug("데이터베이스 연결 시도: %s:%d/%s", 
                        configLoader.getHost(), 
                        configLoader.getPort(), 
                        configLoader.getDatabaseName());
            
            // MySQL JDBC 드라이버 로드
            Class.forName(MYSQL_DRIVER);
            logger.debug("MySQL JDBC 드라이버 로드 완료");
            
            // 설정에서 연결 정보 가져오기
            String jdbcUrl = configLoader.getJdbcUrl();
            String username = configLoader.getUsername();
            String password = configLoader.getPassword();
            
            logger.debug("연결 정보 - URL: %s, 사용자: %s", jdbcUrl, username);
            
            // 연결 타임아웃 설정
            DriverManager.setLoginTimeout(configLoader.getConnectionTimeout() / 1000);
            logger.debug("연결 타임아웃: %d초", configLoader.getConnectionTimeout() / 1000);
            
            // 데이터베이스 연결 생성
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            // 디버그 모드에서만 연결 성공 메시지
            logger.debug("데이터베이스 연결 성공! (%s 환경)", configLoader.getEnvironment());
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            String errorMsg = "MySQL JDBC 드라이버를 찾을 수 없습니다. " +
                            "mysql-connector-java JAR 파일이 클래스패스에 있는지 확인해주세요.";
            logger.error(errorMsg, e);
            throw new SQLException(errorMsg, e);
        } catch (SQLException e) {
            logger.error("데이터베이스 연결 실패", e);
            throw e;
        }
    }
    
    /**
     * 디버그 모드용 연결 메서드입니다.
     * 연결 상태 메시지를 출력하며, 디버깅이나 연결 테스트 시 사용합니다.
     * 
     * @return MySQL 데이터베이스 연결
     * @throws SQLException 연결 실패 시 발생
     */
    public static Connection getConnectionWithLog() throws SQLException {
        try {
            logger.info("데이터베이스 연결 중...");
            Connection connection = getConnection();
            logger.info("데이터베이스 연결 성공: %s 환경", configLoader.getEnvironment());
            return connection;
        } catch (SQLException e) {
            logger.error("데이터베이스 연결 실패", e);
            throw e;
        }
    }
    
    /**
     * 연결 상태를 테스트합니다.
     * 
     * @return 연결 성공 여부
     */
    public static boolean testConnection() {
        logger.info("데이터베이스 연결 테스트 시작");
        
        try (Connection connection = getConnection()) {
            boolean isValid = connection != null && !connection.isClosed();
            
            if (isValid) {
                logger.info("✅ 데이터베이스 연결 테스트 성공");
                logger.debug("   환경: %s", configLoader.getEnvironment());
                logger.debug("   호스트: %s:%d", configLoader.getHost(), configLoader.getPort());
                logger.debug("   데이터베이스: %s", configLoader.getDatabaseName());
            } else {
                logger.error("❌ 데이터베이스 연결 테스트 실패 - 연결이 유효하지 않음");
            }
            
            return isValid;
            
        } catch (SQLException e) {
            logger.error("❌ 데이터베이스 연결 테스트 실패", e);
            
            // 디버그 모드에서만 상세 오류 분석
            if (SimpleLogger.isDebugMode()) {
                if (e.getMessage().contains("Access denied")) {
                    logger.debug("   → 사용자명 또는 비밀번호가 올바르지 않습니다.");
                } else if (e.getMessage().contains("Unknown database")) {
                    logger.debug("   → 지정된 데이터베이스가 존재하지 않습니다.");
                } else if (e.getMessage().contains("Connection refused")) {
                    logger.debug("   → MySQL 서버가 실행되고 있지 않거나 호스트/포트가 올바르지 않습니다.");
                }
            }
            
            return false;
        }
    }
    
    /**
     * 연결을 안전하게 닫습니다.
     * 
     * @param connection 닫을 연결
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                logger.debug("데이터베이스 연결 종료 중...");
                connection.close();
                logger.debug("데이터베이스 연결 종료 완료");
            } catch (SQLException e) {
                logger.error("연결 종료 중 오류 발생", e);
            }
        }
    }
    
    /**
     * 현재 설정 정보를 출력합니다.
     */
    public static void printConfigInfo() {
        logger.info("=== 데이터베이스 설정 정보 ===");
        configLoader.printConfiguration();
        SimpleLogger.printDebugStatus();
    }
    
    /**
     * 현재 사용 중인 환경을 반환합니다.
     * 
     * @return 현재 환경 (dev, prod)
     */
    public static String getCurrentEnvironment() {
        return configLoader.getEnvironment();
    }
    
    /**
     * 재시도 로직을 포함한 견고한 연결 생성
     * 
     * @return MySQL 데이터베이스 연결
     * @throws SQLException 최대 재시도 후에도 연결 실패 시 발생
     */
    public static Connection getConnectionWithRetry() throws SQLException {
        int maxRetries = configLoader.getMaxRetries();
        SQLException lastException = null;
        
        logger.debug("재시도 로직을 사용한 연결 시작 (최대 %d회)", maxRetries);
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("연결 시도 %d/%d", attempt, maxRetries);
                Connection connection = getConnection();
                
                if (attempt > 1) {
                    logger.info("재시도를 통한 연결 성공! (시도 횟수: %d)", attempt);
                }
                
                return connection;
                
            } catch (SQLException e) {
                lastException = e;
                
                if (attempt < maxRetries) {
                    int delaySeconds = attempt;
                    logger.debug("연결 시도 %d/%d 실패. %d초 후 재시도...", attempt, maxRetries, delaySeconds);
                    
                    try {
                        Thread.sleep(1000L * delaySeconds); // 점진적 지연
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("연결 재시도 중 인터럽트 발생", ie);
                        throw new SQLException("연결 재시도 중 인터럽트 발생", ie);
                    }
                } else {
                    logger.error("모든 연결 시도 실패 (" + maxRetries + "/" + maxRetries + ")");
                }
            }
        }
        
        throw new SQLException("최대 재시도 횟수(" + maxRetries + ")를 초과했습니다.", lastException);
    }
}

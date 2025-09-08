package app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 클래스
 * DatabaseConfigLoader에서 설정 정보를 받아 실제 MySQL 연결을 처리합니다.
 */
public class DatabaseConnection {
    
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static DatabaseConfigLoader configLoader;
    
    // 환경 설정을 시스템 프로퍼티에서 읽어옴 (기본값: dev)
    static {
        String environment = System.getProperty("app.env", "dev");
        configLoader = new DatabaseConfigLoader(environment);
    }
    
    /**
     * 데이터베이스 연결을 생성합니다.
     * 
     * @return MySQL 데이터베이스 연결
     * @throws SQLException 연결 실패 시 발생
     */
    public static Connection getConnection() throws SQLException {
        try {
            // MySQL JDBC 드라이버 로드
            Class.forName(MYSQL_DRIVER);
            
            // 설정에서 연결 정보 가져오기
            String jdbcUrl = configLoader.getJdbcUrl();
            String username = configLoader.getUsername();
            String password = configLoader.getPassword();
            
            // 연결 타임아웃 설정
            DriverManager.setLoginTimeout(configLoader.getConnectionTimeout() / 1000);
            
            // 데이터베이스 연결 생성
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            if (configLoader.isShowSql()) {
                System.out.println("데이터베이스 연결 성공: " + configLoader.getEnvironment() + " 환경");
            }
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC 드라이버를 찾을 수 없습니다. " +
                    "mysql-connector-java JAR 파일이 클래스패스에 있는지 확인해주세요.", e);
        }
    }
    
    /**
     * 연결 상태를 테스트합니다.
     * 
     * @return 연결 성공 여부
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection != null && !connection.isClosed();
            
            if (isValid) {
                System.out.println("✅ 데이터베이스 연결 테스트 성공");
                System.out.println("   환경: " + configLoader.getEnvironment());
                System.out.println("   호스트: " + configLoader.getHost() + ":" + configLoader.getPort());
                System.out.println("   데이터베이스: " + configLoader.getDatabaseName());
            } else {
                System.out.println("❌ 데이터베이스 연결 테스트 실패");
            }
            
            return isValid;
            
        } catch (SQLException e) {
            System.err.println("❌ 데이터베이스 연결 테스트 실패: " + e.getMessage());
            
            // 일반적인 연결 오류 원인 안내
            if (e.getMessage().contains("Access denied")) {
                System.err.println("   → 사용자명 또는 비밀번호가 올바르지 않습니다.");
            } else if (e.getMessage().contains("Unknown database")) {
                System.err.println("   → 지정된 데이터베이스가 존재하지 않습니다.");
            } else if (e.getMessage().contains("Connection refused")) {
                System.err.println("   → MySQL 서버가 실행되고 있지 않거나 호스트/포트가 올바르지 않습니다.");
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
                connection.close();
                if (configLoader.isShowSql()) {
                    System.out.println("데이터베이스 연결 종료됨");
                }
            } catch (SQLException e) {
                System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
            }
        }
    }
    
    /**
     * 현재 설정 정보를 출력합니다.
     */
    public static void printConfigInfo() {
        configLoader.printConfiguration();
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
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return getConnection();
            } catch (SQLException e) {
                lastException = e;
                
                if (attempt < maxRetries) {
                    System.err.println("연결 시도 " + attempt + "/" + maxRetries + " 실패. 재시도 중...");
                    try {
                        Thread.sleep(1000 * attempt); // 점진적 지연
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("연결 재시도 중 인터럽트 발생", ie);
                    }
                } else {
                    System.err.println("모든 연결 시도 실패 (" + maxRetries + "/" + maxRetries + ")");
                }
            }
        }
        
        throw new SQLException("최대 재시도 횟수(" + maxRetries + ")를 초과했습니다.", lastException);
    }
}

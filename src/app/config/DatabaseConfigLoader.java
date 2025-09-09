package app.config;

import app.common.SimpleLogger;
import java.io.*;
import java.util.Properties;

/**
 * 데이터베이스 설정을 로드하고 관리하는 클래스
 * 개발/운영 환경별로 다른 설정 파일을 읽어옵니다.
 */
public class DatabaseConfigLoader {
    private static final String CONFIG_DIR = "config/";
    private static final String DEFAULT_CONFIG = "database.properties";
    private static final String DEV_CONFIG = "database-dev.properties";
    private static final String PROD_CONFIG = "database-prod.properties";
    
    private Properties properties;
    private String environment;
    
    // 로거 인스턴스
    private static final SimpleLogger logger = SimpleLogger.getLogger(DatabaseConfigLoader.class);
    
    public DatabaseConfigLoader() {
        this("dev"); // 기본값: 개발 환경
    }
    
    public DatabaseConfigLoader(String environment) {
        this.environment = environment;
        loadConfiguration();
    }
    
    /**
     * 환경에 따른 설정 파일을 로드합니다.
     */
    private void loadConfiguration() {
        properties = new Properties();
        String configFile = getConfigFileName();
        
        logger.debug("설정 파일 로드 시작: %s", configFile);
        
        try (InputStream input = new FileInputStream(CONFIG_DIR + configFile)) {
            properties.load(input);
            logger.info("데이터베이스 설정 로드 완료: %s", configFile);
            logger.debug("로드된 설정 항목 개수: %d", properties.size());
        } catch (FileNotFoundException e) {
            logger.error("설정 파일을 찾을 수 없습니다: " + configFile);
            logger.error("템플릿 파일을 참고하여 설정 파일을 생성해주세요.");
            throw new RuntimeException("데이터베이스 설정 파일 로드 실패", e);
        } catch (IOException e) {
            logger.error("설정 파일 읽기 중 오류 발생", e);
            throw new RuntimeException("데이터베이스 설정 파일 읽기 실패", e);
        }
    }
    
    /**
     * 환경에 따른 설정 파일명을 반환합니다.
     */
    private String getConfigFileName() {
        switch (environment.toLowerCase()) {
            case "dev":
            case "development":
                return DEV_CONFIG;
            case "prod":
            case "production":
                return PROD_CONFIG;
            default:
                return DEFAULT_CONFIG;
        }
    }
    
    // 데이터베이스 연결 정보 getter 메서드들
    public String getHost() {
        return properties.getProperty("db.host", "localhost");
    }
    
    public int getPort() {
        return Integer.parseInt(properties.getProperty("db.port", "3306"));
    }
    
    public String getDatabaseName() {
        return properties.getProperty("db.name", "manazoo");
    }
    
    public String getUsername() {
        return properties.getProperty("db.username", "root");
    }
    
    public String getPassword() {
        return properties.getProperty("db.password", "1111");
    }
    
    public String getServerTimezone() {
        return properties.getProperty("db.serverTimezone", "Asia/Seoul");
    }
    
    public int getConnectionTimeout() {
        return Integer.parseInt(properties.getProperty("db.connectionTimeout", "30000"));
    }
    
    public int getMaxRetries() {
        return Integer.parseInt(properties.getProperty("db.maxRetries", "3"));
    }
    
    public boolean isShowSql() {
        return Boolean.parseBoolean(properties.getProperty("db.showSql", "true"));
    }
    
    public boolean isAutoReconnect() {
        return Boolean.parseBoolean(properties.getProperty("db.autoReconnect", "true"));
    }
    
    /**
     * 디버그 모드 여부를 반환합니다.
     * properties 파일의 app.debug 설정을 먼저 확인하고,
     * 없으면 시스템 프로퍼티를 확인합니다.
     * 
     * @return 디버그 모드 여부
     */
    public boolean isDebugMode() {
        // properties 파일에서 먼저 확인
        String debugFromConfig = properties.getProperty("app.debug");
        if (debugFromConfig != null) {
            return Boolean.parseBoolean(debugFromConfig);
        }
        
        // properties 파일에 없으면 시스템 프로퍼티에서 확인
        return Boolean.parseBoolean(System.getProperty("app.debug", "false"));
    }
    
    /**
     * 완전한 JDBC URL을 생성합니다.
     */
    public String getJdbcUrl() {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://")
           .append(getHost())
           .append(":")
           .append(getPort())
           .append("/")
           .append(getDatabaseName())
           .append("?serverTimezone=")
           .append(getServerTimezone());
        
        if (isAutoReconnect()) {
            url.append("&autoReconnect=true");
        }
        
        return url.toString();
    }
    
    /**
     * 현재 환경을 반환합니다.
     */
    public String getEnvironment() {
        return environment;
    }
    
    /**
     * 설정 정보를 출력합니다 (비밀번호 제외).
     */
    public void printConfiguration() {
        logger.info("=== 데이터베이스 설정 정보 ===");
        logger.info("환경: %s", getEnvironment());
        logger.info("호스트: %s", getHost());
        logger.info("포트: %d", getPort());
        logger.info("데이터베이스: %s", getDatabaseName());
        logger.info("사용자명: %s", getUsername());
        logger.info("타임존: %s", getServerTimezone());
        logger.debug("JDBC URL: %s", getJdbcUrl());
        logger.debug("연결 타임아웃: %dms", getConnectionTimeout());
        logger.debug("최대 재시도: %d", getMaxRetries());
        logger.debug("SQL 표시: %s", isShowSql());
        logger.debug("자동 재연결: %s", isAutoReconnect());
        logger.debug("디버그 모드: %s", isDebugMode());
        logger.info("==============================");
    }
}

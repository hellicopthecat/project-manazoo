package app.config;

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
        
        try (InputStream input = new FileInputStream(CONFIG_DIR + configFile)) {
            properties.load(input);
            System.out.println("데이터베이스 설정 로드 완료: " + configFile);
        } catch (FileNotFoundException e) {
            System.err.println("설정 파일을 찾을 수 없습니다: " + configFile);
            System.err.println("템플릿 파일을 참고하여 설정 파일을 생성해주세요.");
            throw new RuntimeException("데이터베이스 설정 파일 로드 실패", e);
        } catch (IOException e) {
            System.err.println("설정 파일 읽기 중 오류 발생: " + e.getMessage());
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
        System.out.println("=== 데이터베이스 설정 정보 ===");
        System.out.println("환경: " + getEnvironment());
        System.out.println("호스트: " + getHost());
        System.out.println("포트: " + getPort());
        System.out.println("데이터베이스: " + getDatabaseName());
        System.out.println("사용자명: " + getUsername());
        System.out.println("타임존: " + getServerTimezone());
        System.out.println("JDBC URL: " + getJdbcUrl());
        System.out.println("연결 타임아웃: " + getConnectionTimeout() + "ms");
        System.out.println("최대 재시도: " + getMaxRetries());
        System.out.println("SQL 표시: " + isShowSql());
        System.out.println("자동 재연결: " + isAutoReconnect());
        System.out.println("==============================");
    }
}

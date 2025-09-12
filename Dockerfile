# Eclipse Temurin OpenJDK 17 JRE 사용 (경량화된 Alpine 기반)
FROM eclipse-temurin:17-jre-alpine

# 메타데이터 설정
LABEL maintainer="manazoo-team"
LABEL description="ManageZoo 동물원 관리 시스템"
LABEL version="1.0"

# 작업 디렉토리 설정
WORKDIR /app

# MySQL Connector J 다운로드
RUN apk update && \
    apk add --no-cache wget && \
    wget -O mysql-connector-j.jar \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar && \
    apk del wget && \
    rm -rf /var/cache/apk/*

# 애플리케이션 파일들 복사
COPY out/artifacts/project_manazoo_jar/project-manazoo.jar app.jar
COPY config/ config/

# 필요한 디렉토리 생성
RUN mkdir -p logs data

# 파일 권한 설정
RUN chmod 644 app.jar mysql-connector-j.jar

# Java 애플리케이션 실행
CMD ["java", "-cp", "app.jar:mysql-connector-j.jar", "app.Main"]
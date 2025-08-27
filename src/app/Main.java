package app;

import app.console.ConsoleEngine;

/**
 * 동물원 관리 시스템의 메인 진입점입니다.
 * 
 * <p>이 클래스는 애플리케이션의 시작점 역할을 하며,
 * {@link ConsoleEngine}을 통해 실제 애플리케이션 로직을 실행합니다.</p>
 * 
 * @author ManazooTeam
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        ConsoleEngine.start();
    }
}

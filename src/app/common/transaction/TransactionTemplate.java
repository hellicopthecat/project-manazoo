package app.common.transaction;

import app.common.exception.TransactionException;

/**
 * 트랜잭션 실행을 위한 템플릿 헬퍼 클래스입니다.
 * TransactionManager를 더 쉽게 사용할 수 있는 유틸리티 메서드들을 제공합니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class TransactionTemplate {
    
    private final TransactionManager transactionManager;
    
    public TransactionTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    /**
     * 트랜잭션 내에서 작업을 실행하되 결과를 반환하지 않는 경우에 사용합니다.
     * 
     * @param action 실행할 작업
     * @throws TransactionException 트랜잭션 실행 중 오류 발생 시
     */
    public void execute(TransactionAction action) throws TransactionException {
        transactionManager.executeInTransaction(() -> {
            action.execute();
            return null;
        });
    }
    
    /**
     * 읽기 전용 트랜잭션 내에서 작업을 실행하되 결과를 반환하지 않는 경우에 사용합니다.
     * 
     * @param action 실행할 작업
     * @throws TransactionException 트랜잭션 실행 중 오류 발생 시
     */
    public void executeReadOnly(TransactionAction action) throws TransactionException {
        transactionManager.executeInReadOnlyTransaction(() -> {
            action.execute();
            return null;
        });
    }
    
    /**
     * 결과를 반환하지 않는 트랜잭션 작업을 위한 함수형 인터페이스입니다.
     */
    @FunctionalInterface
    public interface TransactionAction {
        /**
         * 트랜잭션 내에서 실행될 작업을 정의합니다.
         * 
         * @throws Exception 작업 실행 중 발생한 예외
         */
        void execute() throws Exception;
    }
}
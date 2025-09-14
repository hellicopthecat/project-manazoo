package app.common.transaction;

import app.common.exception.TransactionException;

/**
 * 트랜잭션 관리를 담당하는 매니저 인터페이스입니다.
 * 비즈니스 로직의 원자성을 보장하기 위한 트랜잭션 경계를 관리합니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public interface TransactionManager {
    
    /**
     * 트랜잭션 콜백을 위한 함수형 인터페이스입니다.
     * 
     * @param <T> 반환 타입
     */
    @FunctionalInterface
    interface TransactionCallback<T> {
        /**
         * 트랜잭션 내에서 실행될 비즈니스 로직을 정의합니다.
         * 
         * @return 비즈니스 로직 실행 결과
         * @throws Exception 비즈니스 로직 실행 중 발생한 예외
         */
        T execute() throws Exception;
    }
    
    /**
     * 트랜잭션 내에서 비즈니스 로직을 실행합니다.
     * 실행 중 예외가 발생하면 자동으로 롤백됩니다.
     * 
     * @param <T> 반환 타입
     * @param callback 트랜잭션 내에서 실행할 비즈니스 로직
     * @return 비즈니스 로직 실행 결과
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    <T> T executeInTransaction(TransactionCallback<T> callback) throws TransactionException;
    
    /**
     * 읽기 전용 트랜잭션 내에서 비즈니스 로직을 실행합니다.
     * 성능 최적화를 위해 읽기 전용으로 설정됩니다.
     * 
     * @param <T> 반환 타입
     * @param callback 읽기 전용 트랜잭션 내에서 실행할 비즈니스 로직
     * @return 비즈니스 로직 실행 결과
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    <T> T executeInReadOnlyTransaction(TransactionCallback<T> callback) throws TransactionException;
}
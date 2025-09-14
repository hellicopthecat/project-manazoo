package app.common.transaction;

import app.common.database.ConnectionPool;
import app.common.exception.TransactionException;
import app.common.SimpleLogger;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC 기반 트랜잭션 매니저 구현체입니다.
 * ConnectionPool을 활용하여 트랜잭션의 원자성을 보장합니다.
 * 
 * @author MANAZOO Team
 * @since 2.0
 */
public class JdbcTransactionManager implements TransactionManager {
    
    private final ConnectionPool connectionPool;
    private final SimpleLogger logger;
    
    public JdbcTransactionManager() {
        this.connectionPool = ConnectionPool.getInstance();
        this.logger = SimpleLogger.getLogger(JdbcTransactionManager.class);
    }
    
    @Override
    public <T> T executeInTransaction(TransactionCallback<T> callback) throws TransactionException {
        return executeTransaction(callback, false);
    }
    
    @Override
    public <T> T executeInReadOnlyTransaction(TransactionCallback<T> callback) throws TransactionException {
        return executeTransaction(callback, true);
    }
    
    /**
     * 실제 트랜잭션을 실행하는 내부 메서드입니다.
     * 
     * @param <T> 반환 타입
     * @param callback 실행할 비즈니스 로직
     * @param readOnly 읽기 전용 여부
     * @return 비즈니스 로직 실행 결과
     * @throws TransactionException 트랜잭션 처리 중 오류 발생 시
     */
    private <T> T executeTransaction(TransactionCallback<T> callback, boolean readOnly) 
            throws TransactionException {
        
        Connection connection = null;
        boolean originalAutoCommit = true;
        
        try {
            // 1. Connection Pool에서 연결 획득
            connection = connectionPool.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            
            if (!readOnly && originalAutoCommit) {
                connection.setAutoCommit(false);
                logger.info("트랜잭션 시작됨");
            }
            
            if (readOnly) {
                connection.setReadOnly(true);
                logger.info("읽기 전용 트랜잭션 시작됨");
            }
            
            // 2. 비즈니스 로직 실행
            T result = callback.execute();
            
            // 3. 커밋 (읽기 전용이 아닌 경우에만)
            if (!readOnly && originalAutoCommit) {
                connection.commit();
                logger.info("트랜잭션 커밋됨");
            }
            
            return result;
            
        } catch (Exception e) {
            // 4. 롤백 처리
            if (connection != null && !readOnly) {
                try {
                    if (originalAutoCommit) {
                        connection.rollback();
                        logger.info("트랜잭션 롤백됨");
                    }
                } catch (SQLException rollbackException) {
                    logger.error("트랜잭션 롤백 중 오류 발생", rollbackException);
                    // 원본 예외와 함께 롤백 예외도 포함하여 전파
                    TransactionException transactionException = new TransactionException(
                            "트랜잭션 실행 중 오류 발생", e);
                    transactionException.addSuppressed(rollbackException);
                    throw transactionException;
                }
            }
            
            // 5. 예외 변환 및 전파
            if (e instanceof TransactionException) {
                throw (TransactionException) e;
            } else {
                throw new TransactionException("트랜잭션 실행 중 오류 발생", e);
            }
            
        } finally {
            // 6. 연결 상태 복원 및 Connection Pool로 반환
            if (connection != null) {
                try {
                    if (readOnly) {
                        connection.setReadOnly(false);
                    }
                    if (originalAutoCommit) {
                        connection.setAutoCommit(originalAutoCommit);
                    }
                    
                    // Connection Pool로 연결 반환
                    connectionPool.returnConnection(connection);
                    
                } catch (SQLException e) {
                    logger.error("커넥션 상태 복원 중 오류 발생", e);
                }
            }
        }
    }
}
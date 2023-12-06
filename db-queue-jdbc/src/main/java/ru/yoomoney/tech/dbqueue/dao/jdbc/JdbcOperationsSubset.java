package ru.yoomoney.tech.dbqueue.dao.jdbc;

import javax.annotation.Nullable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Sub set of org.springframework.jdbc.core.JdbcOperations and
 * org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations.
 *
 * Сontains only the methods used by the library. Can be implemented without using Spring.
 * For spring implementation see module db-queue-spring
 *
 * @see org.springframework.jdbc.core.JdbcOperations
 * @see org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
 */
public interface JdbcOperationsSubset {

    /**
     * Execute a query for a result object, given static SQL.
     *
     * @see org.springframework.jdbc.core.JdbcOperations#queryForObject(String, Class)
     */
    @Nullable
    public <T> T queryForObject(String sql, Class<T> requiredType);


    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result object.
     *
     * {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations#queryForObject(String, Map, Class)}
     */
    @Nullable
    <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType);

    /**
     * Issue an update via a prepared statement, binding the given arguments.
     *
     * @see org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations#update(String, Map) 
     */
    int update(String sql, Map<String, ?> paramMap);


    /**
     * Execute a JDBC data access operation, implemented as callback action
     * working on a JDBC CallableStatement. 
     * @see org.springframework.jdbc.core.JdbcOperations#execute(String, CallableStatementCallback)
     */
    @Nullable
    <T> T execute(String callString, CallableStatementCallback<T> action);


    /**
     * Execute a JDBC data access operation, implemented as callback action
     * working on a JDBC PreparedStatement.
     *
     * @see org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations#execute(String, PreparedStatementCallback)
     */
    @Nullable
    <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action);


    /**
     * copy of org.springframework.jdbc.core.CallableStatementCallback.
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface CallableStatementCallback<T> {
        @Nullable
        T doInCallableStatement(CallableStatement cs) throws SQLException;
    }


    /**
     * copy of org.springframework.jdbc.core.PreparedStatementCallback.
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface PreparedStatementCallback<T> {
        @Nullable
        T doInPreparedStatement(PreparedStatement ps) throws SQLException;
    }

}

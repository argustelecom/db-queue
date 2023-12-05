package ru.yoomoney.tech.dbqueue.dao.jdbc;

import javax.annotation.Nullable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Sub set of ru.yoomoney.tech.dbqueue.dao.jdbc.JdbcOperationsSubset. Contains only operations used by the dao.
 */
public interface JdbcOperationsSubset {

	/**
	 * Execute a query for a result object, given static SQL.
	 * <p>
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute a static query with a PreparedStatement,
	 * use the overloaded {@link #queryForObject(String, Class, Object...)} method with {@code null} as argument array.
	 * <p>
	 * This method is useful for running static SQL with a known outcome. The query is expected to be a single
	 * row/single column query; the returned result will be directly mapped to the corresponding object type.
	 *
	 * @param sql
	 *            the SQL query to execute
	 * @param requiredType
	 *            the type that the result object is expected to match
	 * @return the result object of the required type, or {@code null} in case of SQL NULL
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 *             if the query does not return exactly one row
	 * @throws org.springframework.jdbc.IncorrectResultSetColumnCountException
	 *             if the query does not return a row containing a single column
	 * @see #queryForObject(String, Class, Object...)
	 */
	@Nullable
	public <T> T queryForObject(String sql, Class<T> requiredType);


	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * list of arguments to bind to the query, expecting a result object.
	 * <p>The query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 * @param sql the SQL query to execute
	 * @param paramSource container of arguments to bind to the query
	 * @param requiredType the type that the result object is expected to match
	 * @return the result object of the required type, or {@code null} in case of SQL NULL
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 * if the query does not return exactly one row
	 * @throws org.springframework.jdbc.IncorrectResultSetColumnCountException
	 * if the query does not return a row containing a single column
	 * @throws DataAccessException if the query fails
	 * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(String, Class)
	 * @see org.springframework.jdbc.core.SingleColumnRowMapper
	 */
	@Nullable
	<T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType);

	/**
	 * Issue an update via a prepared statement, binding the given arguments.
	 *
	 * @param sql
	 *            the SQL containing named parameters
	 * @param paramMap
	 *            map of parameters to bind to the query (leaving it to the PreparedStatement to guess the corresponding
	 *            SQL type)
	 * @return the number of rows affected
	 * @throws DataAccessException
	 *             if there is any problem issuing the update
	 */
	int update(String sql, Map<String, ?> paramMap);


	/**
	 * Execute a JDBC data access operation, implemented as callback action
	 * working on a JDBC CallableStatement. This allows for implementing arbitrary
	 * data access operations on a single Statement, within Spring's managed JDBC
	 * environment: that is, participating in Spring-managed transactions and
	 * converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
	 * <p>The callback action can return a result object, for example a domain
	 * object or a collection of domain objects.
	 * @param callString the SQL call string to execute
	 * @param action a callback that specifies the action
	 * @return a result object returned by the action, or {@code null} if none
	 * @throws DataAccessException if there is any problem
	 */
	@Nullable
	<T> T execute(String callString, CallableStatementCallback<T> action);


	/**
	 * Execute a JDBC data access operation, implemented as callback action
	 * working on a JDBC PreparedStatement. This allows for implementing arbitrary
	 * data access operations on a single Statement, within Spring's managed
	 * JDBC environment: that is, participating in Spring-managed transactions
	 * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
	 * <p>The callback action can return a result object, for example a
	 * domain object or a collection of domain objects.
	 * @param sql the SQL to execute
	 * @param paramMap map of parameters to bind to the query
	 * (leaving it to the PreparedStatement to guess the corresponding SQL type)
	 * @param action callback object that specifies the action
	 * @return a result object returned by the action, or {@code null}
	 * @throws DataAccessException if there is any problem
	 */
	@Nullable
	<T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action);


	/**
	 * copy of org.springframework.jdbc.core.CallableStatementCallback
	 */
	@FunctionalInterface
	public interface CallableStatementCallback<T> {
		@Nullable
		T doInCallableStatement(CallableStatement cs) throws SQLException;
	}


	/**
	 * copy of org.springframework.jdbc.core.PreparedStatementCallback
	 */
	@FunctionalInterface
	public interface PreparedStatementCallback<T> {
		@Nullable
		T doInPreparedStatement(PreparedStatement ps) throws SQLException;
	}

}

package ru.yoomoney.tech.dbqueue.dao.spring;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yoomoney.tech.dbqueue.dao.jdbc.JdbcOperationsSubset;

import javax.annotation.Nullable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Implements JdbcOperationsSubset using spring JdbcTemplace.
 */
public class SpringJdbcOperationsSubset implements JdbcOperationsSubset {
    private final NamedParameterJdbcTemplate delegate;

    public SpringJdbcOperationsSubset(NamedParameterJdbcTemplate delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Nullable
    @Override public <T> T queryForObject(String sql, Class<T> requiredType) {
        return delegate.getJdbcTemplate().queryForObject(sql, requiredType);
    }

    @Nullable
    @Override public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) {
        return delegate.queryForObject(sql, paramMap, requiredType);
    }

    @Override
    public int update(String sql, Map<String, ?> paramMap) {
        return delegate.update(sql, paramMap);
    }

    @Nullable
    @Override
    public <T> T execute(String callString, CallableStatementCallback<T> action) {
        return delegate.getJdbcTemplate().execute(callString, action::doInCallableStatement);
    }

    @Nullable
    @Override
    public <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action) {
        return delegate.execute(sql, paramMap, action::doInPreparedStatement);
    }

    public NamedParameterJdbcTemplate getDelegate() {
        return delegate;
    }
}

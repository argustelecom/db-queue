package ru.yoomoney.tech.dbqueue.dao.spring;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionOperations;
import ru.yoomoney.tech.dbqueue.config.DatabaseAccessLayer;
import ru.yoomoney.tech.dbqueue.config.DatabaseDialect;
import ru.yoomoney.tech.dbqueue.config.QueueTableSchema;
import ru.yoomoney.tech.dbqueue.dao.H2QueueDao;
import ru.yoomoney.tech.dbqueue.dao.H2QueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.dao.MssqlQueueDao;
import ru.yoomoney.tech.dbqueue.dao.MssqlQueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.dao.Oracle11QueueDao;
import ru.yoomoney.tech.dbqueue.dao.Oracle11QueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.dao.PostgresQueueDao;
import ru.yoomoney.tech.dbqueue.dao.PostgresQueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.dao.QueueDao;
import ru.yoomoney.tech.dbqueue.dao.QueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.dao.jdbc.JdbcOperationsSubset;
import ru.yoomoney.tech.dbqueue.settings.FailureSettings;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Class for interacting with database via Spring JDBC
 *
 * @author Oleg Kandaurov
 * @since 22.04.2021
 */
public class SpringDatabaseAccessLayer implements DatabaseAccessLayer {

    @Nonnull
    private final SpringJdbcOperationsSubset jdbcOperations;
    @Nonnull
    private final TransactionOperations transactionOperations;
    @Nonnull
    private final DatabaseDialect databaseDialect;
    @Nonnull
    private final QueueTableSchema queueTableSchema;
    @Nonnull
    private final QueueDao queueDao;


    /**
     * Constructor
     *
     * @param databaseDialect       Database type (dialect)
     * @param queueTableSchema      Queue table scheme.
     * @param jdbcOperations        Reference to Spring JDBC template.
     * @param transactionOperations Reference to Spring transaction template.
     */
    public SpringDatabaseAccessLayer(@Nonnull DatabaseDialect databaseDialect,
                                     @Nonnull QueueTableSchema queueTableSchema,
                                     @Nonnull JdbcOperations jdbcOperations,
                                     @Nonnull TransactionOperations transactionOperations) {
        this.databaseDialect = requireNonNull(databaseDialect);
        this.queueTableSchema = requireNonNull(queueTableSchema);
        this.jdbcOperations = new SpringJdbcOperationsSubset(new NamedParameterJdbcTemplate(requireNonNull(jdbcOperations)));
        this.transactionOperations = requireNonNull(transactionOperations);
        this.queueDao = createQueueDao(databaseDialect, queueTableSchema, this.jdbcOperations);
    }

    @Override
    @Nonnull
    public QueueDao getQueueDao() {
        return queueDao;
    }

    private QueueDao createQueueDao(@Nonnull DatabaseDialect databaseDialect,
                                    @Nonnull QueueTableSchema queueTableSchema,
                                    @Nonnull SpringJdbcOperationsSubset jdbcOperations) {
        requireNonNull(databaseDialect);
        requireNonNull(jdbcOperations);
        requireNonNull(queueTableSchema);
        switch (databaseDialect) {
            case POSTGRESQL:
                return new PostgresQueueDao(jdbcOperations, queueTableSchema);
            case MSSQL:
                return new MssqlQueueDao(jdbcOperations, queueTableSchema);
            case ORACLE_11G:
                return new Oracle11QueueDao(jdbcOperations, queueTableSchema);
            case H2:
                return new H2QueueDao(jdbcOperations.getDelegate().getJdbcTemplate(), queueTableSchema);
            default:
                throw new IllegalArgumentException("unsupported database kind: " + databaseDialect);
        }
    }

    @Override
    @Nonnull
    public QueuePickTaskDao createQueuePickTaskDao(@Nonnull QueueLocation queueLocation,
                                                   @Nonnull FailureSettings failureSettings) {
        requireNonNull(databaseDialect);
        requireNonNull(queueTableSchema);
        requireNonNull(queueLocation);
        requireNonNull(failureSettings);
        switch (databaseDialect) {
            case POSTGRESQL:
                return new PostgresQueuePickTaskDao(jdbcOperations, queueTableSchema, queueLocation, failureSettings);
            case MSSQL:
                return new MssqlQueuePickTaskDao(jdbcOperations, queueTableSchema, queueLocation, failureSettings);
            case ORACLE_11G:
                return new Oracle11QueuePickTaskDao(jdbcOperations, queueTableSchema, queueLocation, failureSettings);
            case H2:
                return new H2QueuePickTaskDao(jdbcOperations.getDelegate().getJdbcOperations(), queueTableSchema,
                        queueLocation, failureSettings);
            default:
                throw new IllegalArgumentException("unsupported database kind: " + databaseDialect);
        }
    }

    @Nonnull
    @Override
    public DatabaseDialect getDatabaseDialect() {
        return databaseDialect;
    }

    @Nonnull
    @Override
    public QueueTableSchema getQueueTableSchema() {
        return queueTableSchema;
    }

    @Override
    public <T> T transact(@Nonnull Supplier<T> supplier) {
        requireNonNull(supplier);
        return transactionOperations.execute((status) -> supplier.get());
    }

    @Override
    public void transact(@Nonnull Runnable runnable) {
        requireNonNull(runnable);
        transact(() -> {
            runnable.run();
            return null;
        });
    }
}

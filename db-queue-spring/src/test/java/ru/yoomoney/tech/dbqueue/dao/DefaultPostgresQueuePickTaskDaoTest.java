package ru.yoomoney.tech.dbqueue.dao;

import org.junit.BeforeClass;
import ru.yoomoney.tech.dbqueue.dao.utils.PostgresDatabaseInitializer;

/**
 * @author Oleg Kandaurov
 * @since 12.10.2019
 */
public class DefaultPostgresQueuePickTaskDaoTest extends QueuePickTaskDaoTest {

    @BeforeClass
    public static void beforeClass() {
        PostgresDatabaseInitializer.initialize();
    }

    public DefaultPostgresQueuePickTaskDaoTest() {
        super(new PostgresQueueDao(
                    PostgresDatabaseInitializer.getJdbcOperationsSubset(), PostgresDatabaseInitializer.DEFAULT_SCHEMA),
                (queueLocation, failureSettings) ->
                        new PostgresQueuePickTaskDao(
                                PostgresDatabaseInitializer.getJdbcOperationsSubset(),
                                PostgresDatabaseInitializer.DEFAULT_SCHEMA, queueLocation, failureSettings),
                PostgresDatabaseInitializer.DEFAULT_TABLE_NAME, PostgresDatabaseInitializer.DEFAULT_SCHEMA,
                PostgresDatabaseInitializer.getJdbcTemplate(), PostgresDatabaseInitializer.getTransactionTemplate());
    }

    @Override
    protected String currentTimeSql() {
        return "now()";
    }
}

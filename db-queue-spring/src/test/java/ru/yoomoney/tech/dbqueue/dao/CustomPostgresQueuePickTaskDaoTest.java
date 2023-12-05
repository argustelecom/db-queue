package ru.yoomoney.tech.dbqueue.dao;

import org.junit.BeforeClass;
import ru.yoomoney.tech.dbqueue.dao.utils.PostgresDatabaseInitializer;

/**
 * @author Oleg Kandaurov
 * @since 12.10.2019
 */
public class CustomPostgresQueuePickTaskDaoTest extends QueuePickTaskDaoTest {

    @BeforeClass
    public static void beforeClass() {
        PostgresDatabaseInitializer.initialize();
    }

    public CustomPostgresQueuePickTaskDaoTest() {
        super(new PostgresQueueDao(PostgresDatabaseInitializer.getJdbcOperationsSubset(), PostgresDatabaseInitializer.CUSTOM_SCHEMA),
                (queueLocation, failureSettings) -> new PostgresQueuePickTaskDao(PostgresDatabaseInitializer.getJdbcOperationsSubset(),
                        PostgresDatabaseInitializer.CUSTOM_SCHEMA, queueLocation, failureSettings),
                PostgresDatabaseInitializer.CUSTOM_TABLE_NAME, PostgresDatabaseInitializer.CUSTOM_SCHEMA,
                PostgresDatabaseInitializer.getJdbcTemplate(), PostgresDatabaseInitializer.getTransactionTemplate());
    }

    @Override
    protected String currentTimeSql() {
        return "now()";
    }
}

package ru.yoomoney.tech.dbqueue.dao;

import org.junit.BeforeClass;
import ru.yoomoney.tech.dbqueue.dao.utils.MssqlDatabaseInitializer;

/**
 * @author Oleg Kandaurov
 * @author Behrooz Shabani
 * @since 25.01.2020
 */
public class CustomMssqlQueueDaoTest extends QueueDaoTest {

    @BeforeClass
    public static void beforeClass() {
        MssqlDatabaseInitializer.initialize();
    }

    public CustomMssqlQueueDaoTest() {
        super(new MssqlQueueDao(MssqlDatabaseInitializer.getJdbcOperationsSubset(), MssqlDatabaseInitializer.CUSTOM_SCHEMA),
                MssqlDatabaseInitializer.CUSTOM_TABLE_NAME, MssqlDatabaseInitializer.CUSTOM_SCHEMA,
                MssqlDatabaseInitializer.getJdbcTemplate(), MssqlDatabaseInitializer.getTransactionTemplate());
    }
}

package ru.yoomoney.tech.dbqueue.dao;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.yoomoney.tech.dbqueue.dao.jdbc.JdbcOperationsSubset;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.config.QueueTableSchema;
import ru.yoomoney.tech.dbqueue.dao.QueueDao;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Database access object to manage tasks in the queue for Oracle database type.
 *
 * @author Oleg Kandaurov
 * @since 15.05.2020
 */
public class Oracle11QueueDao implements QueueDao {

    private final Map<QueueLocation, String> enqueueSqlCache = new ConcurrentHashMap<>();
    private final Map<QueueLocation, String> deleteSqlCache = new ConcurrentHashMap<>();
    private final Map<QueueLocation, String> reenqueueSqlCache = new ConcurrentHashMap<>();
    private final Map<String, String> nextSequenceSqlCache = new ConcurrentHashMap<>();

    @Nonnull
    private final JdbcOperationsSubset jdbcTemplate;
    @Nonnull
    private final QueueTableSchema queueTableSchema;

    /**
     * Constructor
     *
     * @param jdbcTemplate     Reference to Spring JDBC template.
     * @param queueTableSchema Queue table scheme.
     */
    public Oracle11QueueDao(@Nonnull JdbcOperationsSubset jdbcTemplate,
                            @Nonnull QueueTableSchema queueTableSchema) {
        this.queueTableSchema = requireNonNull(queueTableSchema);
        this.jdbcTemplate = requireNonNull(jdbcTemplate);
    }

    @Override
    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "SQL_INJECTION_SPRING_JDBC"})
    public long enqueue(@Nonnull QueueLocation location, @Nonnull EnqueueParams<String> enqueueParams) {
        requireNonNull(location);
        requireNonNull(enqueueParams);

        String idSequence = location.getIdSequence()
                .orElseThrow(() -> new IllegalStateException("id sequence must be specified for oracle 11g database"));

        Long generatedId = Objects.requireNonNull(jdbcTemplate.queryForObject(
                nextSequenceSqlCache.computeIfAbsent(idSequence, this::createNextSequenceSql), Long.class));

        Map<String, Object> params = new HashMap<>();
        params.put("queueName", location.getQueueId().asString());
        params.put("payload", enqueueParams.getPayload());
        params.put("executionDelay", enqueueParams.getExecutionDelay().getSeconds());
        params.put("id", generatedId);

        queueTableSchema.getExtFields().forEach(paramName -> params.put(paramName, null));
        enqueueParams.getExtData().forEach(params::put);

        jdbcTemplate.update(enqueueSqlCache.computeIfAbsent(location, this::createEnqueueSql), params);
        return generatedId;
    }


    @Override
    public boolean deleteTask(@Nonnull QueueLocation location, long taskId) {
        requireNonNull(location);

        Map<String, Object> params = new HashMap<>();
        params.put("id", taskId);
        params.put("queueName", location.getQueueId().asString());

        int updatedRows = jdbcTemplate.update(deleteSqlCache.computeIfAbsent(location, this::createDeleteSql), params);
        return updatedRows != 0;
    }

    @Override
    public boolean reenqueue(@Nonnull QueueLocation location, long taskId, @Nonnull Duration executionDelay) {
        requireNonNull(location);
        requireNonNull(executionDelay);

        Map<String, Object> params = new HashMap<>();
        params.put("id", taskId);
        params.put("queueName", location.getQueueId().asString());
        params.put("executionDelay", executionDelay.getSeconds()) ;

        int updatedRows = jdbcTemplate.update(reenqueueSqlCache.computeIfAbsent(location, this::createReenqueueSql),
                params);
        return updatedRows != 0;
    }

    private String createDeleteSql(@Nonnull QueueLocation location) {
        return "DELETE FROM " + location.getTableName() + " WHERE " + queueTableSchema.getQueueNameField() +
                " = :queueName AND " + queueTableSchema.getIdField() + " = :id";
    }

    private String createEnqueueSql(@Nonnull QueueLocation location) {
        return "INSERT INTO " + location.getTableName() + "(" +
                queueTableSchema.getIdField() + "," +
                queueTableSchema.getQueueNameField() + "," +
                queueTableSchema.getPayloadField() + "," +
                queueTableSchema.getNextProcessAtField() + "," +
                queueTableSchema.getReenqueueAttemptField() + "," +
                queueTableSchema.getTotalAttemptField() +
                (queueTableSchema.getExtFields().isEmpty() ? "" :
                        queueTableSchema.getExtFields().stream().collect(Collectors.joining(", ", ", ", ""))) +
                ") VALUES " +
                "(:id, :queueName, :payload, CURRENT_TIMESTAMP + :executionDelay * INTERVAL '1' SECOND, 0, 0" +
                (queueTableSchema.getExtFields().isEmpty() ? "" : queueTableSchema.getExtFields().stream()
                        .map(field -> ":" + field).collect(Collectors.joining(", ", ", ", ""))) +
                ")";
    }

    private String createReenqueueSql(@Nonnull QueueLocation location) {
        return "UPDATE " + location.getTableName() + " SET " + queueTableSchema.getNextProcessAtField() +
                " = CURRENT_TIMESTAMP + :executionDelay * INTERVAL '1' SECOND, " +
                queueTableSchema.getAttemptField() + " = 0, " +
                queueTableSchema.getReenqueueAttemptField() +
                " = " + queueTableSchema.getReenqueueAttemptField() + " + 1 " +
                "WHERE " + queueTableSchema.getIdField() + " = :id AND " +
                queueTableSchema.getQueueNameField() + " = :queueName";
    }

    private String createNextSequenceSql(String idSequence) {
        return "SELECT " + idSequence + ".nextval FROM dual";
    }

}

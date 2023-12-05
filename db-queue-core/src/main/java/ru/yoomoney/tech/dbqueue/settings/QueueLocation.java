package ru.yoomoney.tech.dbqueue.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Queue location in the database.
 *
 * @author Oleg Kandaurov
 * @since 10.07.2017
 */
public final class QueueLocation {

    @Nonnull
    private final QueueTable table;

    @Nonnull
    private final QueueId queueId;

    private QueueLocation(@Nonnull QueueId queueId, @Nonnull QueueTable table) {
        this.queueId = Objects.requireNonNull(queueId, "queueId must not be null");
        this.table = Objects.requireNonNull(table, "table must not be null");
    }

    /**
     * Get queue table name.
     *
     * @return Table name.
     */
    @Nonnull
    public String getTableName() {
        return table.getTableName();
    }

    /**
     * Get queue identifier.
     *
     * @return Queue identifier.
     */
    @Nonnull
    public QueueId getQueueId() {
        return queueId;
    }

    /**
     * Get id sequence name.
     * <p>
     * Use for databases which doesn't have automatically incremented primary keys, for example Oracle 11g
     *
     * @return database sequence name for generating primary key of tasks table.
     */
    public Optional<String> getIdSequence() {
        return table.getIdSequence();
    }

    @Override
    public String toString() {
        return '{' +
                "id=" + getQueueId() +
                ",table=" + getTableName() +
                getIdSequence().map(seq-> ",idSequence=" + seq).orElse("") +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        QueueLocation that = (QueueLocation) obj;
        return Objects.equals(table, that.table) && Objects.equals(queueId, that.queueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, queueId);
    }

    /**
     * Create a new builder for queue location.
     *
     * @return A builder for queue location.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(QueueTable.Builder defaultSettings) {
        return new Builder(defaultSettings);
    }

    /**
     * A builder for class {@link QueueLocation}.
     */
    public static class Builder {
        private final QueueTable.Builder tableInfoBuilder;
        private QueueId queueId;

        private Builder() {
            tableInfoBuilder = QueueTable.builder();
        }

        private Builder(@Nonnull QueueTable.Builder defaultSettings) {
            tableInfoBuilder = Objects.requireNonNull(defaultSettings);
        }

        /**
         * Set table name for queue tasks.
         *
         * @param tableName Table name.
         * @return Reference to the same builder.
         */
        public Builder withTableName(@Nonnull String tableName) {
            tableInfoBuilder.withTableName(tableName);
            return this;
        }

        /**
         * Set queue identifier.
         *
         * @param queueId Queue identifier.
         * @return Reference to the same builder.
         */
        public Builder withQueueId(@Nonnull QueueId queueId) {
            this.queueId = queueId;
            return this;
        }

        /**
         * Set id sequence name.
         *
         * @param idSequence database sequence name for generating primary key of tasks table.
         * @return Reference to the same builder.
         */
        public Builder withIdSequence(@Nullable String idSequence) {
            tableInfoBuilder.withIdSequence(idSequence);
            return this;
        }

        /**
         * Build queue location object.
         *
         * @return Queue location  object.
         */
        public QueueLocation build() {
            return new QueueLocation(queueId, tableInfoBuilder.build());
        }
    }
}

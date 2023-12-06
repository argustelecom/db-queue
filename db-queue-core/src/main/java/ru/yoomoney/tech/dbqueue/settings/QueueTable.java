package ru.yoomoney.tech.dbqueue.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Database table that stores one or more queue
 */
public class QueueTable {
    @Nonnull
    private final String tableName;

    @Nullable
    private final String idSequence;

    /**
     * Regexp for SQL injection prevention
     */
    private static final Pattern DISALLOWED_CHARS = Pattern.compile("[^a-zA-Z0-9_\\.]*");


    public QueueTable(@Nonnull String tableName, @Nullable String idSequence) {
        this.tableName = DISALLOWED_CHARS.matcher(
                Objects.requireNonNull(tableName, "tableName must not be null")).replaceAll("");
        this.idSequence = idSequence != null ? DISALLOWED_CHARS.matcher(idSequence).replaceAll("") : null;

    }

    /**
     * Get queue table name.
     *
     * @return Table name.
     */
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    /**
     * Get id sequence name.
     * <p>
     * Use for databases which doesn't have automatically incremented primary keys, for example Oracle 11g
     *
     * @return database sequence name for generating primary key of tasks table.
     */
    public Optional<String> getIdSequence() {
        return Optional.ofNullable(idSequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueueTable)) {
            return false;
        }
        QueueTable that = (QueueTable) obj;
        return tableName.equals(that.tableName) && Objects.equals(idSequence, that.idSequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, idSequence);
    }

    /**
     * Create a new builder for queue table.
     *
     * @return A builder for queue location.
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * A builder for class {@link QueueLocation}.
     */
    public static class Builder {
        private String tableName;
        @Nullable
        private String idSequence;

        private Builder() {
        }

        /**
         * Set table name.
         *
         * @param tableName Table name.
         * @return Reference to the same builder.
         */
        public Builder withTableName(@Nonnull String tableName) {
            this.tableName = tableName;
            return this;
        }

        /**
         * Set id sequence name.
         *
         * @param idSequence database sequence name for generating primary key of tasks table.
         * @return Reference to the same builder.
         */
        public Builder withIdSequence(@Nullable String idSequence) {
            this.idSequence = idSequence;
            return this;
        }

        /**
         * Build queue location object.
         *
         * @return Queue location  object.
         */
        public QueueTable build() {
            return new QueueTable(tableName, idSequence);
        }
    }

}

package seedu.unienable.storage;

import java.util.List;

//@@author lukelouyu
/**
 * Represents the result of loading records from a dataset.
 *
 * @param <T> the type of record loaded.
 */
public class LoadResult<T> {
    private final List<T> records;
    private final List<String> warnings;

    /**
     * Creates a load result with records and warnings.
     *
     * @param records successfully loaded records.
     * @param warnings messages for skipped invalid records.
     */
    public LoadResult(List<T> records, List<String> warnings) {
        this.records = List.copyOf(records);
        this.warnings = List.copyOf(warnings);
    }

    /**
     * Returns successfully loaded records.
     *
     * @return an immutable list of successfully loaded records.
     */
    public List<T> getRecords() {
        return records;
    }

    /**
     * Returns warnings generated during loading.
     *
     * @return an immutable list of load warnings.
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Returns whether loading generated any warnings.
     *
     * @return true when one or more load warnings were recorded.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
//@@author

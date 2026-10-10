package seedu.unienable.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;

/**
 * Reads and writes user activities using the agreed six-column text format.
 *
 * <p>Malformed rows are skipped with line-numbered warnings. If a later save is
 * needed after such a load, the original file is copied to a {@code .corrupt.bak}
 * file before the repaired valid subset is written.</p>
 */
public class Storage {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String FIELD_SEPARATOR = "|";

    private final Path activityFile;
    private List<String> warnings = List.of();
    private boolean loadedCorruptData;

    /**
     * Creates storage at the default runtime location.
     */
    public Storage() {
        this(Path.of("data", "activities.txt"));
    }

    /**
     * Creates storage at a caller-supplied path, which also makes tests isolated.
     *
     * @param activityFile file containing activity records
     */
    public Storage(Path activityFile) {
        this.activityFile = Objects.requireNonNull(activityFile);
    }

    /**
     * Loads valid activity records from disk.
     *
     * @return loaded activities, or an empty list when the file does not exist
     * @throws UniEnableException when the file cannot be read
     */
    public ActivityList load() throws UniEnableException {
        warnings = new ArrayList<>();
        loadedCorruptData = false;
        if (!Files.exists(activityFile)) {
            return new ActivityList();
        }

        List<Activity> activities = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(activityFile, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UniEnableException("Unable to load activities: " + exception.getMessage());
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                activities.add(parseLine(line));
            } catch (IllegalArgumentException exception) {
                loadedCorruptData = true;
                warnings.add("Line " + (i + 1) + ": " + exception.getMessage());
            }
        }
        return new ActivityList(activities);
    }

    /**
     * Returns warnings produced by the most recent load operation.
     *
     * @return immutable line-numbered load warnings
     */
    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    /**
     * Saves all activities in insertion order using UTF-8.
     *
     * @param activities activities to persist
     * @throws UniEnableException when the file cannot be safely written
     */
    public void save(ActivityList activities) throws UniEnableException {
        Objects.requireNonNull(activities);
        try {
            Path parent = activityFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (loadedCorruptData && Files.exists(activityFile)) {
                backupCorruptFile();
            }

            List<String> lines = activities.getActivities().stream()
                    .map(Storage::formatActivity)
                    .toList();
            Path temporaryFile = activityFile.resolveSibling(activityFile.getFileName() + ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            moveIntoPlace(temporaryFile);
            loadedCorruptData = false;
        } catch (IOException exception) {
            throw new UniEnableException("Unable to save activities: " + exception.getMessage());
        }
    }

    private Activity parseLine(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 6) {
            throw new IllegalArgumentException("expected 6 fields separated by '|'.");
        }
        if (fields[0].isBlank()) {
            throw new IllegalArgumentException("activity name must not be empty.");
        }

        try {
            LocalDate date = LocalDate.parse(fields[1], DATE_FORMAT);
            LocalTime startTime = LocalTime.parse(fields[2], TIME_FORMAT);
            LocalTime endTime = LocalTime.parse(fields[3], TIME_FORMAT);
            DemandLevel demand = DemandLevel.valueOf(fields[4].toUpperCase(Locale.ROOT));
            boolean done = parseDone(fields[5]);
            return new Activity(fields[0], date, startTime, endTime, demand, done);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("invalid date, time, demand, or completion value.");
        }
    }

    private boolean parseDone(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("completion value must be true or false.");
    }

    private static String formatActivity(Activity activity) {
        return String.join(FIELD_SEPARATOR,
                activity.getName(),
                activity.getDate().toString(),
                activity.getStartTime().toString(),
                activity.getEndTime().toString(),
                activity.getDemand().name(),
                Boolean.toString(activity.isDone()));
    }

    private void backupCorruptFile() throws IOException {
        Path backup = activityFile.resolveSibling(activityFile.getFileName() + ".corrupt.bak");
        Files.copy(activityFile, backup, StandardCopyOption.REPLACE_EXISTING);
    }

    private void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, activityFile, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, activityFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

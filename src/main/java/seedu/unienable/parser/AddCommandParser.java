package seedu.unienable.parser;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.unienable.command.activity.AddCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.enums.DemandLevel;

/**
 * Parses the add command using named prefixes that may appear in any order.
 */
public class AddCommandParser {
    // Owner: Atharva
    private static final Pattern PREFIX = Pattern.compile("(?i)(?<!\\S)(dem|imp|urg|com|n|d|s|e)/");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String USAGE = "Usage: add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm "
            + "[dem/LOW|MEDIUM|HIGH] [imp/IMP] [urg/URG] [com/COM]";

    /** Supplies the current time and local zone for activity end-time validation. */
    private final Clock clock;

    /**
     * Creates a parser using the system clock and local time zone.
     */
    public AddCommandParser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser with a supplied clock so tests can fix the current date and time.
     *
     * @param clock clock used to validate whether the activity has ended.
     */
    AddCommandParser(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Validates add arguments and constructs an AddCommand without modifying activity state.
     *
     * @param arguments everything after the leading add command word.
     * @return fully validated add command.
     * @throws ParseException for absent, duplicate, unknown, or malformed arguments.
     */
    public AddCommand parse(String arguments) throws ParseException {
        Map<String, String> fields = readFields(arguments);
        for (String required : new String[] {"n", "d", "s", "e"}) {
            if (!fields.containsKey(required)) {
                throw invalid("Missing " + required + "/ parameter.");
            }
        }

        String name = fields.get("n");
        if (name.contains("|") || name.indexOf('\n') >= 0 || name.indexOf('\r') >= 0) {
            throw invalid("The activity name must not contain | or line breaks.");
        }

        LocalDate date;
        LocalTime startTime;
        LocalTime endTime;
        try {
            if (!DATE_PATTERN.matcher(fields.get("d")).matches()) {
                throw new DateTimeParseException("Wrong date format", fields.get("d"), 0);
            }
            date = LocalDate.parse(fields.get("d"), DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw invalid("Invalid date. Use YYYY-MM-DD and a real calendar date.");
        }
        try {
            startTime = parseTime(fields.get("s"));
            endTime = parseTime(fields.get("e"));
        } catch (DateTimeParseException exception) {
            throw invalid("Invalid time. Use 24-hour HH:mm (for example, 09:30).");
        }

        if (!endTime.isAfter(startTime)) {
            throw invalid("End time must be later than start time on the same day.");
        }

        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        if (!endDateTime.isAfter(LocalDateTime.now(clock))) {
            throw invalid("End time must be later than current time.");
        }

        DemandLevel demand = DemandLevel.MEDIUM;
        if (fields.containsKey("dem")) {
            try {
                demand = DemandLevel.valueOf(fields.get("dem").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw invalid("Demand must be LOW, MEDIUM, or HIGH.");
            }
        }
        Integer importance = parseRating(fields.get("imp"), "imp");
        Integer urgency = parseRating(fields.get("urg"), "urg");
        Integer commitment = parseRating(fields.get("com"), "com");
        return new AddCommand(name, date, startTime, endTime, demand, importance, urgency, commitment);
    }

    /**
     * Collects prefix/value pairs while allowing whitespace inside the activity name.
     * Only known prefixes (dem, imp, urg, com, n, d, s, e) delimit values; any other
     * slash-containing text such as "and/or" or "w/o" is kept as value content.
     * A known prefix like d/ that appears inside free text is still parsed as a
     * prefix, an unavoidable ambiguity of prefix-based grammars.
     */
    private Map<String, String> readFields(String arguments) throws ParseException {
        Map<String, String> fields = new HashMap<>();
        Matcher matcher = PREFIX.matcher(arguments);
        int previousValueStart = -1;
        String previousKey = null;

        while (matcher.find()) {
            if (previousKey == null) {
                if (!arguments.substring(0, matcher.start()).isBlank()) {
                    throw invalid("Unexpected text before the first parameter.");
                }
            } else {
                store(fields, previousKey, arguments.substring(previousValueStart, matcher.start()).trim());
            }
            previousKey = matcher.group(1).toLowerCase(Locale.ROOT);
            previousValueStart = matcher.end();
        }
        if (previousKey == null) {
            throw invalid("No add parameters supplied.");
        }
        store(fields, previousKey, arguments.substring(previousValueStart).trim());
        return fields;
    }

    private void store(Map<String, String> fields, String key, String value) throws ParseException {
        if (value.isEmpty()) {
            throw invalid("Empty " + key + "/ parameter.");
        }
        if (fields.putIfAbsent(key, value) != null) {
            throw invalid("Duplicate " + key + "/ parameter.");
        }
    }

    /**
     * Parses a supplied whole-number rating using the model's shared range rule.
     * Missing fields remain unset; empty supplied fields are rejected by the field reader.
     *
     * @param value rating text, or null when the field was omitted.
     * @param prefix field prefix used to identify invalid input.
     * @return rating from 1 to 5, or null when omitted.
     * @throws ParseException if a supplied value is not a whole number from 1 to 5.
     */
    private Integer parseRating(String value, String prefix) throws ParseException {
        if (value == null) {
            return null;
        }
        try {
            Integer rating = Integer.valueOf(value);
            Activity.validateRating(rating);
            return rating;
        } catch (IllegalArgumentException exception) {
            throw invalid("The " + prefix + "/ rating must be a whole number from 1 to 5.");
        }
    }

    private LocalTime parseTime(String value) {
        if (!TIME_PATTERN.matcher(value).matches()) {
            throw new DateTimeParseException("Wrong time format", value, 0);
        }
        return LocalTime.parse(value, TIME_FORMAT);
    }

    private ParseException invalid(String message) {
        return new ParseException("[WARNING] " + message + "\n" + USAGE);
    }
}

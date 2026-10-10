package seedu.unienable.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.unienable.command.activity.AddCommand;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.enums.DemandLevel;

/**
 * Parses the add command using named prefixes that may appear in any order.
 */
public class AddCommandParser {
    // Owner: Atharva
    private static final Pattern PREFIX = Pattern.compile("(?i)(?<!\\S)([a-z]+)/");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final String USAGE = "Usage: add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm "
            + "[dem/LOW|MEDIUM|HIGH]";

    /**
     * Validates add arguments and constructs an AddCommand without modifying activity state.
     *
     * @param arguments everything after the leading add command word
     * @return fully validated add command
     * @throws ParseException for absent, duplicate, unknown, or malformed arguments
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
        // Demo decision: activities must end on the same day after their start time.
        // Agree with the team before supporting overnight activities.
        if (!endTime.isAfter(startTime)) {
            throw invalid("End time must be later than start time on the same day.");
        }

        DemandLevel demand = DemandLevel.MEDIUM;
        if (fields.containsKey("dem")) {
            try {
                demand = DemandLevel.valueOf(fields.get("dem").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw invalid("Demand must be LOW, MEDIUM, or HIGH.");
            }
        }
        return new AddCommand(name, date, startTime, endTime, demand);
    }

    /**
     * Collects prefix/value pairs while allowing whitespace inside the activity name.
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
            if (!previousKey.equals("n") && !previousKey.equals("d")
                    && !previousKey.equals("s") && !previousKey.equals("e") && !previousKey.equals("dem")) {
                throw invalid("Unknown parameter: " + previousKey + "/.");
            }
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

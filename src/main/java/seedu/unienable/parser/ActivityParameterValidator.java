//@@author lukelouyu
package seedu.unienable.parser;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.unienable.exception.ParseException;

/**
 * Catches likely misspellings of activity fields without treating every slash in a name as a field.
 */
final class ActivityParameterValidator {
    private static final Pattern PARAMETER = Pattern.compile("(?i)(?<!\\S)([a-z]+)/");
    private static final List<String> PREFIXES = List.of("n", "d", "s", "e", "dem", "imp", "urg", "com");

    /** Prevents instantiation of this shared parser helper. */
    private ActivityParameterValidator() {
    }

    /**
     * Rejects a one-edit typo in a multi-letter prefix, including an adjacent transposition.
     * Ordinary slash text such as and/or, w/o and module codes remains valid name content.
     *
     * @param arguments unparsed activity fields.
     * @throws ParseException when a token is likely a misspelled parameter.
     */
    static void validate(String arguments) throws ParseException {
        Matcher matcher = PARAMETER.matcher(arguments);
        while (matcher.find()) {
            String token = matcher.group(1).toLowerCase(Locale.ROOT);
            if (PREFIXES.contains(token)) {
                continue;
            }
            for (String prefix : PREFIXES) {
                if (prefix.length() > 1 && token.length() > 1 && isOneEditAway(token, prefix)) {
                    throw new ParseException("[WARNING] Unknown parameter '" + token
                            + "/'. Did you mean '" + prefix + "/'?");
                }
            }
        }
    }

    /** Computes edit distance with adjacent swaps for the short field-prefix tokens. */
    private static boolean isOneEditAway(String token, String prefix) {
        if (Math.abs(token.length() - prefix.length()) > 1) {
            return false;
        }
        int[][] distance = new int[token.length() + 1][prefix.length() + 1];
        for (int i = 0; i <= token.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= prefix.length(); j++) {
            distance[0][j] = j;
        }
        for (int i = 1; i <= token.length(); i++) {
            for (int j = 1; j <= prefix.length(); j++) {
                int substitution = token.charAt(i - 1) == prefix.charAt(j - 1) ? 0 : 1;
                distance[i][j] = Math.min(distance[i - 1][j] + 1,
                        Math.min(distance[i][j - 1] + 1, distance[i - 1][j - 1] + substitution));
                if (i > 1 && j > 1 && token.charAt(i - 1) == prefix.charAt(j - 2)
                        && token.charAt(i - 2) == prefix.charAt(j - 1)) {
                    distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + 1);
                }
            }
        }
        return distance[token.length()][prefix.length()] == 1;
    }
}
//@@author

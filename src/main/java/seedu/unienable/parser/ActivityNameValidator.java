//@@author lukelouyu
package seedu.unienable.parser;

import seedu.unienable.exception.ParseException;

/**
 * Applies the ASCII-only rule to names supplied in add and edit commands.
 * Saved historical names are loaded separately without this input restriction.
 */
final class ActivityNameValidator {
    /**
     * Prevents instantiation of this shared validation helper.
     */
    private ActivityNameValidator() {
    }

    /**
     * Rejects non-ASCII characters before a command can change activity data.
     *
     * @param name non-null name supplied by the user.
     * @throws ParseException if the name contains any non-ASCII character.
     */
    static void validate(String name) throws ParseException {
        if (name.chars().anyMatch(character -> character > 0x7f)) {
            throw new ParseException("[WARNING] Please write the activity name in English (ASCII characters only).");
        }
    }
}
//@@author

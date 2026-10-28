//@@author lukelouyu
package seedu.unienable.parser;

import seedu.unienable.exception.ParseException;

/**
 * Enforces printable-ASCII names supplied through add and edit.
 * Historical names loaded from storage are not validated here.
 */
final class ActivityNameValidator {
    /**
     * Prevents instantiation of this shared validation helper.
     */
    private ActivityNameValidator() {
    }

    /**
     * Decodes an explicitly escaped literal slash after field boundaries and typo checks are resolved.
     * Other backslashes remain part of the name; stored names do not retain the slash escape.
     *
     * @param name non-null raw name field.
     * @return name with each backslash-slash sequence replaced by a literal slash.
     */
    static String unescapeSlashes(String name) {
        return name.replace("\\/", "/");
    }

    /**
     * Rejects blank names and characters outside printable ASCII before activity data changes.
     *
     * @param name non-null name supplied by the user.
     * @throws ParseException if the name is blank or contains a character outside 0x20 through 0x7E.
     */
    static void validate(String name) throws ParseException {
        if (name.codePoints().allMatch(point -> Character.isWhitespace(point) || Character.isSpaceChar(point))) {
            throw new ParseException("[WARNING] Empty n/ parameter.");
        }
        if (name.chars().anyMatch(character -> character < 0x20 || character > 0x7e)) {
            throw new ParseException("[WARNING] Please write the activity name in English (ASCII characters only).");
        }
    }
}
//@@author

//@@author lukelouyu
package seedu.unienable.parser;

import seedu.unienable.exception.ParseException;

/**
 * Allows Unicode activity names while rejecting characters that cannot be safely displayed.
 */
final class ActivityNameValidator {
    /**
     * Prevents instantiation of this shared validation helper.
     */
    private ActivityNameValidator() {
    }

    /**
     * Rejects control characters and malformed surrogate pairs before activity data changes.
     *
     * @param name non-null name supplied by the user.
     * @throws ParseException if the name contains a control character or an unpaired surrogate.
     */
    static void validate(String name) throws ParseException {
        if (name.codePoints().allMatch(point -> Character.isWhitespace(point) || Character.isSpaceChar(point))) {
            throw new ParseException("[WARNING] Empty n/ parameter.");
        }
        if (name.codePoints().anyMatch(character -> Character.isISOControl(character)
                || Character.getType(character) == Character.LINE_SEPARATOR
                || Character.getType(character) == Character.PARAGRAPH_SEPARATOR
                || character >= 0xd800 && character <= 0xdfff)) {
            throw new ParseException("[WARNING] The activity name must not contain control characters"
                    + " or unpaired surrogates.");
        }
    }
}
//@@author

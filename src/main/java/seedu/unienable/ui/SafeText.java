package seedu.unienable.ui;

import java.util.Locale;

/**
 * Escapes control and separator characters so stored names and echoed arguments cannot change terminal state.
 */
public final class SafeText {
    /**
     * Prevents construction of this utility class.
     */
    private SafeText() {
    }

    /**
     * Formats dynamic text for terminal display by escaping controls and line separators.
     *
     * @param text text to format, possibly null.
     * @return safe text for terminal display.
     */
    public static String format(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        text.codePoints().forEach(codePoint -> {
            int type = Character.getType(codePoint);
            if (Character.isISOControl(codePoint) || type == Character.LINE_SEPARATOR
                    || type == Character.PARAGRAPH_SEPARATOR) {
                result.append(String.format(Locale.ROOT, "\\u%04X", codePoint));
            } else {
                result.appendCodePoint(codePoint);
            }
        });
        return result.toString();
    }
}

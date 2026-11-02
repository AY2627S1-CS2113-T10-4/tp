package seedu.unienable.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Checks that terminal-unsafe text is escaped without changing ordinary Unicode text.
 */
class SafeTextTest {
    @Test
    void format_preservesPrintableTextAndEscapesControls() {
        assertEquals("AS1 - Block 1", SafeText.format("AS1 - Block 1"));
        assertEquals("Evil\\u001B[31mRED", SafeText.format("Evil\u001b[31mRED"));
        assertEquals("a\\u000Ab", SafeText.format("a\nb"));
        assertEquals("a\\u0009b", SafeText.format("a\tb"));
        assertEquals("a\\u2028b", SafeText.format("a\u2028b"));
        assertEquals("复习 🎉", SafeText.format("复习 🎉"));
        assertEquals("", SafeText.format(null));
    }
}

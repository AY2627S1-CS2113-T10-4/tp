package seedu.unienable.ui.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.Activity;
import seedu.unienable.model.enums.DemandLevel;

/**
 * Checks pure formatting of activity views: preserved order, 1-based numbering,
 * and the empty-list placeholder.
 */
class ActivityListFormatterTest {
    private final Activity mondayGym = new Activity("Gym session", LocalDate.parse("2026-10-12"),
            LocalTime.parse("07:00"), LocalTime.parse("09:00"), DemandLevel.HIGH, false);
    private final Activity tuesdayLab = new Activity("Lab", LocalDate.parse("2026-10-13"),
            LocalTime.parse("14:00"), LocalTime.parse("16:00"), DemandLevel.LOW, false);

    @Test
    void formatList_empty_showsHeadingAndPlaceholder() {
        String message = ActivityListFormatter.formatList(List.of());
        assertTrue(message.startsWith("Activities"), "heading missing for empty list");
        assertTrue(message.endsWith("No activities yet."), "empty-list placeholder missing");
    }

    @Test
    void formatList_preservesGivenOrderWithOneBasedIndexes() {
        // Deliberately not in canonical order: the formatter must render what it receives.
        String message = ActivityListFormatter.formatList(List.of(tuesdayLab, mondayGym));
        assertTrue(message.contains("1. Lab (2026-10-13, 14:00-16:00) [LOW]"));
        assertTrue(message.contains("2. Gym session (2026-10-12, 07:00-09:00) [HIGH]"));
        assertTrue(message.indexOf("Lab") < message.indexOf("Gym session"),
                "formatter must not reorder the given view");
    }

    @Test
    void formatDemandList_usesDemandHeadingAndSameNumbering() {
        String headingAndNotice = "Activities by demand (HIGH to LOW):\n"
                + "Note: Do not use indexes from 'list demand' for deletion.\n"
                + "Run 'list' to find the correct index for 'delete INDEX'.\n";
        assertEquals(headingAndNotice + "1. Gym session (2026-10-12, 07:00-09:00) [HIGH] [ ]",
                ActivityListFormatter.formatDemandList(List.of(mondayGym)));
        assertEquals(headingAndNotice + "No activities yet.", ActivityListFormatter.formatDemandList(List.of()));
    }
}

package seedu.unienable.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import seedu.unienable.model.enums.DemandLevel;

/**
 * Tests optional rating values, shared validation and compatibility with existing activity state.
 */
class ActivityTest {
    private static final LocalDate DATE = LocalDate.of(2020, 10, 20);
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(10, 0);

    @Test
    void constructor_legacySignature_preservesFieldsAndLeavesRatingsUnset() {
        Activity activity = new Activity("Planning and/or review w/o slides", DATE, START, END,
                DemandLevel.HIGH, true);

        assertEquals("Planning and/or review w/o slides", activity.getName());
        assertEquals(DATE, activity.getDate());
        assertEquals(START, activity.getStartTime());
        assertEquals(END, activity.getEndTime());
        assertEquals(DemandLevel.HIGH, activity.getDemand());
        assertTrue(activity.isDone());
        assertNull(activity.getImportance());
        assertNull(activity.getUrgency());
        assertNull(activity.getCommitment());
    }

    @Test
    void constructor_optionalRatings_preservesEverySetUnsetCombination() {
        Integer[][] combinations = {
            {null, null, null},
            {5, null, null},
            {null, 4, null},
            {null, null, 3},
            {5, 4, null},
            {5, null, 3},
            {null, 4, 3},
            {5, 4, 3}
        };
        for (Integer[] ratings : combinations) {
            Activity activity = new Activity("Meeting", DATE, START, END, DemandLevel.LOW, true,
                    ratings[0], ratings[1], ratings[2]);

            assertEquals(ratings[0], activity.getImportance());
            assertEquals(ratings[1], activity.getUrgency());
            assertEquals(ratings[2], activity.getCommitment());
            assertEquals(DemandLevel.LOW, activity.getDemand());
            assertTrue(activity.isDone());
        }
    }

    @Test
    void constructor_invalidRating_rejectsEachPosition() {
        for (int value : new int[] {-1, 0, 6}) {
            assertThrows(IllegalArgumentException.class, () -> new Activity("Meeting", DATE, START, END,
                    DemandLevel.MEDIUM, false, value, 3, 3), "importance: " + value);
            assertThrows(IllegalArgumentException.class, () -> new Activity("Meeting", DATE, START, END,
                    DemandLevel.MEDIUM, false, 3, value, 3), "urgency: " + value);
            assertThrows(IllegalArgumentException.class, () -> new Activity("Meeting", DATE, START, END,
                    DemandLevel.MEDIUM, false, 3, 3, value), "commitment: " + value);
        }
    }

    @Test
    void validateRating_unsetOrOneToFive_acceptsValue() {
        for (Integer value : new Integer[] {null, 1, 2, 3, 4, 5}) {
            assertDoesNotThrow(() -> Activity.validateRating(value), "rating: " + value);
        }
    }

    @Test
    void setDone_changesCompletionAndPreservesRatings() {
        Activity activity = new Activity("Meeting", DATE, START, END, DemandLevel.MEDIUM, false, 5, null, 3);
        assertFalse(activity.isDone());

        for (boolean done : new boolean[] {true, false}) {
            activity.setDone(done);

            assertEquals(done, activity.isDone());
            assertEquals(Integer.valueOf(5), activity.getImportance());
            assertNull(activity.getUrgency());
            assertEquals(Integer.valueOf(3), activity.getCommitment());
        }
    }
}

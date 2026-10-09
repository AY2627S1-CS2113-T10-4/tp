package seedu.unienable.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import seedu.unienable.exception.ParseException;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.logic.FacilityManager;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.Facility;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;
import seedu.unienable.ui.Ui;
import seedu.unienable.ui.accessibility.AccessibilityDisclaimer;

/**
 * Checks command behavior independently and in the user's complete regression session.
 */
class ConsoleRegressionTest {
    @TestFactory
    Stream<DynamicTest> commands() throws Exception {
        return TestSupport.cases("console").stream().map(row -> DynamicTest.dynamicTest(row[0], () -> {
            String output = TestSupport.console(row[1] + "\nbye\nignored\n");
            assertTrue(output.contains(row[2]), output);
            if (!row[3].isEmpty()) {
                assertFalse(output.contains(row[3]), output);
            }
            assertTrue(output.endsWith("Goodbye from UniEnable!\n" + "_".repeat(60) + "\n"), output);
        }));
    }

    @Test
    void fullSession_retainsFiveActivitiesAndRecoversAfterWarnings() throws Exception {
        String input = String.join("\n", TestSupport.cases("session").stream().map(row -> row[1]).toList());
        String output = TestSupport.console(input + "\n");
        for (String[] row : TestSupport.cases("session")) {
            assertTrue(output.contains(row[2]), row[0] + "\n" + output);
        }
        assertEquals(5, output.split("Added activity:", -1).length - 1);
        assertEquals(15, output.split("\\[WARNING\\]", -1).length - 1);
        assertTrue(output.contains("Activities in this session: 5"));
        assertTrue(output.contains(AccessibilityDisclaimer.TEXT));
    }

    @Test
    void emptyInput_preservesWelcomeFormatting() {
        String border = "_".repeat(60) + "\n";
        assertEquals(border + "Welcome to UniEnable! Type bye to exit.\n" + Ui.getLogo() + "\n" + border,
                TestSupport.console(""));
    }

    @TestFactory
    Stream<DynamicTest> unavailableData_preservesValidationOrder() {
        return Stream.of("facility AS4", "facility list", "facility find type/LIFT")
                .map(input -> DynamicTest.dynamicTest(input, () -> assertEquals(
                        "[WARNING] Facility reference data is unavailable.",
                        assertThrows(ParseException.class, () -> new Parser().parse(input)).getMessage())));
    }

    @Test
    void facilityCommands_areReadOnlyAndHandleEmptyMetadata() throws Exception {
        Facility empty = new Facility("F01", "AS1", null, List.of());
        Parser parser = new Parser(new FacilityManager(List.of(empty)));
        ActivityList activities = new ActivityList();
        Storage noWrites = new Storage() {
            @Override
            public void save(ActivityList ignored) {
                throw new AssertionError("Facility commands must not persist activities");
            }
        };
        for (String input : List.of("facility AS1", "facility list", "facility find type/LIFT")) {
            var result = parser.parse(input).execute(activities, noWrites);
            assertFalse(result.isExit());
            assertTrue(result.getMessage().endsWith(AccessibilityDisclaimer.TEXT));
            assertTrue(activities.getActivities().isEmpty());
        }
        assertTrue(parser.parse("facility AS1").execute(activities, noWrites).getMessage()
                .contains("No accessibility features recorded."));
        Parser none = new Parser(new FacilityManager(List.of()));
        assertTrue(assertThrows(UniEnableException.class,
                () -> none.parse("facility AS1").execute(activities, noWrites))
                .getMessage().contains("Supported facilities:\nNone"));
    }
}

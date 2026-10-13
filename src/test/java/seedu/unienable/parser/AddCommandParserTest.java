package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.Command;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/** Tests Branch 1's order-independent add parser and validation. */
class AddCommandParserTest {
    @Test
    void parse_outOfOrderFieldsAndMultiWordName_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        Command command = new AddCommandParser().parse("dem/high e/11:45 n/Group Meeting d/2026-10-20 s/09:30");
        command.execute(activities, new Storage());

        Activity result = activities.getActivities().get(0);
        assertEquals("Group Meeting", result.getName());
        assertEquals("2026-10-20", result.getDate().toString());
        assertEquals("09:30", result.getStartTime().toString());
        assertEquals("11:45", result.getEndTime().toString());
        assertEquals(DemandLevel.HIGH, result.getDemand());
    }

    @Test
    void parse_demandOmitted_defaultsToMedium() throws Exception {
        ActivityList activities = new ActivityList();
        new AddCommandParser().parse("n/Study d/2026-10-20 s/10:00 e/11:00")
                .execute(activities, new Storage());
        assertEquals(DemandLevel.MEDIUM, activities.getActivities().get(0).getDemand());
    }

    @Test
    void parse_invalidArguments_throwParseException() {
        String[] invalid = {
                "", "n/Meeting d/2026-10-20 s/09:00",
                "n/Meeting d/2026-02-30 s/09:00 e/10:00",
                "n/Meeting d/2026-10-20 s/9:00 e/10:00",
                "n/Meeting d/2026-10-20 s/11:00 e/10:00",
                "n/Meeting d/2026-10-20 s/09:00 e/10:00 dem/urgent",
                "n/Meeting n/Duplicate d/2026-10-20 s/09:00 e/10:00",
                "n/Meeting d/2026-10-20 s/09:00 e/10:00 foo/bar",
                "n/Meeting | note d/2026-10-20 s/09:00 e/10:00",
                "unexpected n/Meeting d/2026-10-20 s/09:00 e/10:00"
        };
        for (String input : invalid) {
            assertThrows(ParseException.class, () -> new AddCommandParser().parse(input), input);
        }
    }
}

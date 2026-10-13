package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.unienable.command.Command;
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/** Tests Branch 1's order-independent add parser and validation. */
class AddCommandParserTest {
    /** Fixed afternoon clock so fixture dates (2026-10-20) are always in the future. */
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-14T16:00:00Z"), ZoneOffset.UTC);

    @Test
    void parse_outOfOrderFieldsAndMultiWordName_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        Command command = new AddCommandParser(FIXED_CLOCK)
                .parse("dem/high e/11:45 n/Group Meeting d/2026-10-20 s/09:30");
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
        new AddCommandParser(FIXED_CLOCK).parse("n/Study d/2026-10-20 s/10:00 e/11:00")
                .execute(activities, new Storage());
        assertEquals(DemandLevel.MEDIUM, activities.getActivities().get(0).getDemand());
    }

    @Test
    void parse_andSlashInName_preservesFullName() throws Exception {
        ActivityList activities = new ActivityList();
        new AddCommandParser(FIXED_CLOCK).parse("n/Planning and/or review d/2026-10-20 s/09:00 e/10:00")
                .execute(activities, new Storage());
        assertEquals("Planning and/or review", activities.getActivities().get(0).getName());
    }

    @Test
    void parse_withoutSlashInName_preservesFullName() throws Exception {
        ActivityList activities = new ActivityList();
        new AddCommandParser(FIXED_CLOCK).parse("n/Lunch w/o friends d/2026-10-20 s/09:00 e/10:00")
                .execute(activities, new Storage());
        assertEquals("Lunch w/o friends", activities.getActivities().get(0).getName());
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
            assertThrows(ParseException.class, () -> new AddCommandParser(FIXED_CLOCK).parse(input), input);
        }
    }

    @Test
    void parse_sameDayFutureEndTime_isAccepted() throws Exception {
        ActivityList activities = new ActivityList();
        new AddCommandParser(FIXED_CLOCK).parse("n/Same Day d/2026-10-14 s/15:00 e/17:00")
                .execute(activities, new Storage());
        assertEquals(LocalTime.of(17, 0), activities.getActivities().get(0).getEndTime());
    }

    @Test
    void parse_sameDayPastEndTime_isRejected() {
        ParseException exception = assertThrows(ParseException.class, () -> new AddCommandParser(FIXED_CLOCK)
                .parse("n/Past d/2026-10-14 s/08:00 e/15:00"));
        assertTrue(exception.getMessage().startsWith("[WARNING] End time must be later than current time."));
        assertTrue(exception.getMessage().contains("Usage: add"));
    }

    @Test
    void parse_sameDayEndTimeEqualToNow_isRejected() {
        assertThrows(ParseException.class, () -> new AddCommandParser(FIXED_CLOCK)
                .parse("n/Now d/2026-10-14 s/08:00 e/16:00"));
    }

    @Test
    void parse_futureDateWithEarlierTimeOfDay_isAccepted() throws Exception {
        ActivityList activities = new ActivityList();
        new AddCommandParser(FIXED_CLOCK).parse("n/Tomorrow d/2026-10-15 s/06:00 e/08:00")
                .execute(activities, new Storage());
        assertEquals(LocalDate.of(2026, 10, 15), activities.getActivities().get(0).getDate());
    }

    @Test
    void parse_pastDateWithLaterTimeOfDay_isRejected() {
        assertThrows(ParseException.class, () -> new AddCommandParser(FIXED_CLOCK)
                .parse("n/Yesterday d/2026-10-13 s/20:00 e/23:00"));
    }

    @Test
    void parse_invalidRangeTakesPrecedenceOverCurrentTimeCheck() {
        ParseException exception = assertThrows(ParseException.class, () -> new AddCommandParser(FIXED_CLOCK)
                .parse("n/Bad Range d/2026-10-14 s/15:00 e/14:00"));
        assertTrue(exception.getMessage().contains("End time must be later than start time"));
        assertTrue(exception.getMessage().startsWith("[WARNING]"));
    }

    @Test
    void parse_boundariesRemainCorrectAcrossClockTimes() {
        for (String instant : new String[] {
            "2026-10-14T06:00:00Z", "2026-10-14T16:00:00Z", "2026-10-14T22:00:00Z"}) {
            Clock clock = Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
            LocalDateTime now = LocalDateTime.now(clock);
            LocalTime past = now.toLocalTime().minusHours(1);
            LocalTime future = now.toLocalTime().plusHours(1);

            assertDoesNotThrow(() -> new AddCommandParser(clock).parse(
                    "n/Future d/" + now.toLocalDate() + " s/" + past + " e/" + future),
                    "future end must be accepted at " + instant);
            assertThrows(ParseException.class, () -> new AddCommandParser(clock).parse(
                    "n/NotFuture d/" + now.toLocalDate() + " s/" + past + " e/" + now.toLocalTime()),
                    "end equal to now must be rejected at " + instant);
        }
    }
}

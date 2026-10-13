package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    /** Fixes now at 16:00 on 14 October 2026 in Singapore for repeatable validation. */
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-14T08:00:00Z"),
            ZoneId.of("Asia/Singapore"));
    private final AddCommandParser parser = new AddCommandParser(FIXED_CLOCK);

    @Test
    void parse_outOfOrderFieldsAndMultiWordName_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        Command command = parser.parse("dem/high e/11:45 n/Group Meeting d/2026-10-20 s/09:30");
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
        parser.parse("n/Study d/2026-10-20 s/10:00 e/11:00")
                .execute(activities, new Storage());
        assertEquals(DemandLevel.MEDIUM, activities.getActivities().get(0).getDemand());
    }

    @Test
    void parse_andSlashInName_preservesFullName() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Planning and/or review d/2026-10-20 s/09:00 e/10:00")
                .execute(activities, new Storage());
        assertEquals("Planning and/or review", activities.getActivities().get(0).getName());
    }

    @Test
    void parse_withoutSlashInName_preservesFullName() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Lunch w/o friends d/2026-10-20 s/09:00 e/10:00")
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
            assertThrows(ParseException.class, () -> parser.parse(input), input);
        }
    }

    @Test
    void parse_futureDateWithEarlierEndTime_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Early meeting d/2026-10-15 s/08:00 e/09:00")
                .execute(activities, new Storage());
        Activity result = activities.getActivities().getFirst();
        assertEquals("2026-10-15", result.getDate().toString());
        assertEquals("09:00", result.getEndTime().toString());
    }

    @Test
    void parse_nextDayJustAfterMidnight_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Midnight meeting d/2026-10-15 s/00:00 e/00:01")
                .execute(activities, new Storage());
        assertEquals("00:01", activities.getActivities().getFirst().getEndTime().toString());
    }

    @Test
    void parse_todayEndingAfterNow_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Later meeting d/2026-10-14 s/15:00 e/17:00")
                .execute(activities, new Storage());
        assertEquals("17:00", activities.getActivities().getFirst().getEndTime().toString());
    }

    @Test
    void parse_endDateTimeAtOrBeforeNow_throwsCurrentTimeWarning() {
        String[] invalid = {
            "n/Past meeting d/2026-10-13 s/22:00 e/23:00",
            "n/Earlier today d/2026-10-14 s/14:00 e/15:00",
            "n/Ending now d/2026-10-14 s/15:00 e/16:00"
        };
        for (String input : invalid) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertEquals("[WARNING] End time must be later than current time.\n"
                    + "Usage: add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH]",
                    exception.getMessage());
        }
    }

    @Test
    void parse_invalidRangeTakesPrecedenceOverCurrentTimeCheck() {
        ParseException exception = assertThrows(ParseException.class, () -> parser
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

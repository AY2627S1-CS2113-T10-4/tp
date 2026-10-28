package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.jupiter.api.Test;

//@@author lukelouyu
import seedu.unienable.command.common.Command;
//@@author
import seedu.unienable.exception.ParseException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

//@@author Atharva005
/**
 * Tests order-independent add parsing, optional ratings and existing input validation.
 */
class AddCommandParserTest {
    /**
     * Fixes now at 16:00 on 14 October 2026 in Singapore for repeatable validation.
     */
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-14T08:00:00Z"),
            ZoneId.of("Asia/Singapore"));
    private final AddCommandParser parser = new AddCommandParser(FIXED_CLOCK);

    @Test
    void parse_mistypedOptionalPrefix_warnsInsteadOfSavingItInName() {
        for (String typo : new String[]{"demm/HIGH", "dme/HIGH", "impp/5", "ur/3", "comm/4"}) {
            ParseException error = assertThrows(ParseException.class,
                    () -> parser.parse("n/Class " + typo + " d/2026-12-10 s/09:00 e/10:00"));
            assertTrue(error.getMessage().contains("Unknown parameter"));
        }
    }

    @Test
    void parse_outOfOrderFieldsAndMultiWordName_addsActivity() throws Exception {
        ActivityList activities = new ActivityList();
        Command command = parser.parse("CoM/3 dEm/hIgH E/11:45 iMp/5 N/Group Meeting "
                + "D/2026-10-20 UrG/4 S/09:30");
        command.execute(activities, new Storage());

        Activity result = activities.getActivities().get(0);
        assertEquals("Group Meeting", result.getName());
        assertEquals("2026-10-20", result.getDate().toString());
        assertEquals("09:30", result.getStartTime().toString());
        assertEquals("11:45", result.getEndTime().toString());
        assertEquals(DemandLevel.HIGH, result.getDemand());
        assertEquals(Integer.valueOf(5), result.getImportance());
        assertEquals(Integer.valueOf(4), result.getUrgency());
        assertEquals(Integer.valueOf(3), result.getCommitment());
    }

    @Test
    void parse_demandOmitted_defaultsToMedium() throws Exception {
        ActivityList activities = new ActivityList();
        parser.parse("n/Study d/2026-10-20 s/10:00 e/11:00")
                .execute(activities, new Storage());
        Activity result = activities.getActivities().getFirst();
        assertEquals(DemandLevel.MEDIUM, result.getDemand());
        assertNull(result.getImportance());
        assertNull(result.getUrgency());
        assertNull(result.getCommitment());
    }

    //@@author
    //@@author lukelouyu
    @Test
    void parse_partialRatings_keepsEachOmittedRatingUnset() throws Exception {
        String[] suffixes = {"imp/1 com/5", "urg/2"};
        Integer[][] expected = {{1, null, 5}, {null, 2, null}};
        for (int i = 0; i < suffixes.length; i++) {
            ActivityList activities = new ActivityList();
            parser.parse("n/Planning and/or review w/o slides d/2026-10-20 s/09:00 e/10:00 " + suffixes[i])
                    .execute(activities, new Storage());

            Activity result = activities.getActivities().getLast();
            assertEquals("Planning and/or review w/o slides", result.getName());
            assertEquals(expected[i][0], result.getImportance());
            assertEquals(expected[i][1], result.getUrgency());
            assertEquals(expected[i][2], result.getCommitment());
        }
    }
    //@@author
    //@@author lukelouyu
    @Test
    void parse_invalidRatings_rejectsEachPrefix() {
        for (String prefix : new String[] {"imp", "urg", "com"}) {
            for (String value : new String[] {"0", "6", "-1", "1.5", "text", "-", "2147483648"}) {
                String input = "n/Meeting d/2026-10-20 s/09:00 e/10:00 " + prefix + "/" + value;
                ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input), input);
                assertTrue(exception.getMessage().contains(prefix + "/ rating must be a whole number from 1 to 5."));
            }
        }
    }
    //@@author
    //@@author Atharva005

    @Test
    void parse_emptyOrDuplicateFields_rejectsEveryKnownPrefix() {
        String base = "n/Meeting d/2026-10-20 s/09:00 e/10:00";
        for (String prefix : new String[] {"n", "d", "s", "e", "dem", "imp", "urg", "com"}) {
            ParseException empty = assertThrows(ParseException.class, () -> parser.parse(base + " " + prefix + "/"));
            assertTrue(empty.getMessage().contains("Empty " + prefix + "/ parameter."));

            ParseException duplicate = assertThrows(ParseException.class, () -> parser.parse(
                    base + " " + prefix + "/1 " + prefix.toUpperCase(Locale.ROOT) + "/2"));
            assertTrue(duplicate.getMessage().contains("Duplicate " + prefix + "/ parameter."));
        }
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
    void parse_unicodeBlankName_throwsParseException() {
        for (String name : new String[] {"\u2003", "\u3000", " \u2003\t\u3000 "}) {
            ParseException exception = assertThrows(ParseException.class,
                    () -> parser.parse("n/" + name + " d/2026-10-20 s/09:00 e/10:00"));
            assertTrue(exception.getMessage().contains("Empty n/ parameter."));
        }
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
            "n/Meeting\nnote d/2026-10-20 s/09:00 e/10:00",
            "n/Meeting\rnote d/2026-10-20 s/09:00 e/10:00",
            "unexpected n/Meeting d/2026-10-20 s/09:00 e/10:00"
        };
        for (String input : invalid) {
            assertThrows(ParseException.class, () -> parser.parse(input), input);
            assertThrows(ParseException.class, () -> parser.parse(input + " imp/5 urg/4 com/3"), input);
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
                    + "Usage: add n/NAME d/YYYY-MM-DD s/HH:mm e/HH:mm [dem/LOW|MEDIUM|HIGH] "
                    + "[imp/IMP] [urg/URG] [com/COM]",
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
//@@author

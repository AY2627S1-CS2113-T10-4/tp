package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.unienable.exception.ParseException;

/**
 * Checks likely field typos without rejecting ordinary slash text in activity names.
 */
class ActivityParameterValidatorTest {
    @Test
    void validate_digitInMisspelledPrefix_reportsSuggestedParameter() {
        String[][] cases = {{"dem1", "dem"}, {"1dem", "dem"}, {"de1", "dem"},
            {"imp2", "imp"}, {"ur3", "urg"}, {"co4", "com"}, {"overlap1", "overlap"}};
        for (String[] scenario : cases) {
            ParseException exception = assertThrows(ParseException.class,
                    () -> ActivityParameterValidator.validate("n/Class " + scenario[0] + "/HIGH"));
            assertEquals("[WARNING] Unknown parameter '" + scenario[0] + "/'. Did you mean '"
                    + scenario[1] + "/'?", exception.getMessage());
        }
    }

    @Test
    void validate_ordinarySlashTextAndModuleCodes_remainValid() {
        for (String name : new String[] {"Planning and/or review", "Lunch w/o friends", "CS2113/notes",
            "Discuss dim\\/sum dem1\\/HIGH d\\/notes"}) {
            assertDoesNotThrow(() -> ActivityParameterValidator.validate("n/" + name));
        }
    }
}

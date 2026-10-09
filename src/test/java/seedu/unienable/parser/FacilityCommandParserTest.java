package seedu.unienable.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import seedu.unienable.exception.ParseException;

/**
 * Checks facility-data availability without changing validation warnings.
 */
class FacilityCommandParserTest {
    @TestFactory
    Stream<DynamicTest> unavailableData_preservesValidationOrder() {
        return Stream.of("facility AS4", "facility list", "facility find type/LIFT")
                .map(input -> DynamicTest.dynamicTest(input, () -> assertEquals(
                        "[WARNING] Facility reference data is unavailable.",
                        assertThrows(ParseException.class,
                                () -> new Parser().parse(input))
                                .getMessage())));
    }
}

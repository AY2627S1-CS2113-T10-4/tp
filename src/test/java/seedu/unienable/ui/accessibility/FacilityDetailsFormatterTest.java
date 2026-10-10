package seedu.unienable.ui.accessibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Checks omission of null, empty, and blank optional text from facility output.
 */
class FacilityDetailsFormatterTest {
    @Test
    void optionalText_nullEmptyAndBlank_isOmittedFromFacilityOutput() {
        for (String text : new String[]{null, "", " "}) {
            var feature = new FacilityFeature(FacilityFeature.Type.LIFT, AccessibilityStatus.YES, text);
            var facility = new Facility("F01", "AS1", text, List.of(feature));
            assertEquals("[F01] AS1\n\nAccessibility Features:\nLIFT | YES",
                    FacilityDetailsFormatter.format(facility));
        }
    }
}

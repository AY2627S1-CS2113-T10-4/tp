package seedu.unienable.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Facility;
import seedu.unienable.model.FacilityFeature;
import seedu.unienable.model.enums.AccessibilityStatus;

/**
 * Loads bundled facility reference data, preserving valid records and reporting invalid lines.
 */
public class FacilityStorage {
    /**
     * Loads facilities.txt from the classpath using UTF-8, including inside a packaged JAR.
     *
     * @return facilities in source order with warnings for skipped lines
     * @throws UniEnableException if the resource is missing or cannot be read
     */
    public LoadResult<Facility> load() throws UniEnableException {
        return loadResource("/facilities.txt");
    }

    /**
     * Parses facility data from a caller-owned reader, which is not closed by this method.
     * Features may precede their facility declaration; association occurs after reading all lines.
     * Record tags and enums are exact matches. Names are preserved but checked for duplicates ignoring case.
     *
     * @param source reader containing facility and feature records
     * @return valid facilities and warnings carrying physical source line numbers
     * @throws UniEnableException if reading fails; partial data is not returned as a successful load
     */
    public LoadResult<Facility> load(Reader source) throws UniEnableException {
        Map<String, FacilityBuilder> facilities = new LinkedHashMap<>();
        Set<String> ids = new HashSet<>();
        Set<String> names = new HashSet<>();
        List<FeatureLine> featureLines = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        BufferedReader reader = new BufferedReader(source);
        try {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) {
                    continue;
                }
                String[] fields = line.split("\\|", -1);
                try {
                    switch (fields[0]) {
                    case "FACILITY":
                        addFacility(fields, facilities, ids, names);
                        break;
                    case "FEATURE":
                        featureLines.add(new FeatureLine(lineNumber, fields));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown record type: " + fields[0]);
                    }
                } catch (IllegalArgumentException exception) {
                    warnings.add("Line " + lineNumber + ": " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            throw readFailure(exception);
        }
        for (FeatureLine featureLine : featureLines) {
            try {
                addFeature(featureLine.fields(), facilities);
            } catch (IllegalArgumentException exception) {
                warnings.add("Line " + featureLine.number() + ": " + exception.getMessage());
            }
        }
        List<Facility> records = new ArrayList<>();
        for (FacilityBuilder facility : facilities.values()) {
            records.add(new Facility(facility.id, facility.name, facility.description, facility.features));
        }
        return new LoadResult<>(records, warnings);
    }

    /**
     * Opens a classpath resource and owns its reader. Package access allows missing-resource tests.
     */
    LoadResult<Facility> loadResource(String resourcePath) throws UniEnableException {
        InputStream stream = FacilityStorage.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new UniEnableException("Missing facility dataset resource: " + resourcePath);
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return load(reader);
        } catch (IOException exception) {
            throw readFailure(exception);
        }
    }

    /**
     * Registers a valid facility only after all uniqueness checks pass.
     */
    private void addFacility(String[] fields, Map<String, FacilityBuilder> facilities,
                             Set<String> ids, Set<String> names) {
        if (fields.length != 4) {
            throw new IllegalArgumentException("FACILITY requires exactly 4 fields.");
        }
        if (fields[1].isBlank() || fields[2].isBlank()) {
            throw new IllegalArgumentException("Facility ID and name must not be empty.");
        }
        String normalizedId = fields[1].toUpperCase(Locale.ROOT);
        if (ids.contains(normalizedId)) {
            throw new IllegalArgumentException("Duplicate facility ID: " + fields[1]);
        }
        String normalizedName = fields[2].toUpperCase(Locale.ROOT);
        if (names.contains(normalizedName)) {
            throw new IllegalArgumentException("Duplicate facility name: " + fields[2]);
        }
        facilities.put(fields[1], new FacilityBuilder(fields[1], fields[2], optionalText(fields[3])));
        ids.add(normalizedId);
        names.add(normalizedName);
    }

    /**
     * Attaches a valid feature in source order to its stable facility ID.
     */
    private void addFeature(String[] fields, Map<String, FacilityBuilder> facilities) {
        if (fields.length < 4 || fields.length > 5) {
            throw new IllegalArgumentException("FEATURE requires 4 or 5 fields; notes are optional.");
        }
        FacilityBuilder facility = facilities.get(fields[1]);
        if (facility == null) {
            throw new IllegalArgumentException("Unknown facility ID: " + fields[1]);
        }
        FacilityFeature.Type type;
        AccessibilityStatus status;
        try {
            type = FacilityFeature.Type.valueOf(fields[2]);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid feature type: " + fields[2]);
        }
        try {
            status = AccessibilityStatus.valueOf(fields[3]);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid accessibility status: " + fields[3]);
        }
        String notes = fields.length == 5 ? optionalText(fields[4]) : null;
        facility.features.add(new FacilityFeature(type, status, notes));
    }

    private String optionalText(String value) {
        return value.isEmpty() ? null : value;
    }

    /**
     * Gives callers a concise error while retaining the underlying I/O cause for diagnostics.
     */
    private UniEnableException readFailure(IOException cause) {
        UniEnableException exception = new UniEnableException("Unable to read facility dataset: " + cause.getMessage());
        exception.initCause(cause);
        return exception;
    }

    /**
     * Retains a feature's physical line number until its facility can be resolved.
     */
    private record FeatureLine(int number, String[] fields) {
    }

    /**
     * Collects features before constructing the immutable Facility model.
     */
    private static class FacilityBuilder {
        private final String id;
        private final String name;
        private final String description;
        private final List<FacilityFeature> features = new ArrayList<>();

        private FacilityBuilder(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }
}

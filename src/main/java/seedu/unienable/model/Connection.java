package seedu.unienable.model;

import seedu.unienable.model.enums.AccessibilityStatus;
import seedu.unienable.model.enums.ShelterStatus;
import seedu.unienable.model.enums.TraversalType;

/**
 * Represents a bidirectional connection between two facilities.
 * The endpoints use facility names, such as AS1 and CLB,
 * rather than stable facility IDs such as F01.
 * The from and to labels identify endpoints, not a permitted travel direction.
 */
public final class Connection {
    private final int id;
    /**
     * One endpoint's facility name, preserved as recorded in the dataset.
     */
    private final String from;
    /**
     * The other endpoint's facility name; its position does not imply a travel direction.
     */
    private final String to;
    private final int distanceInMetres;
    private final AccessibilityStatus accessibility;
    private final TraversalType type;
    private final ShelterStatus shelter;
    private final String knownBarrier;
    private final String notes;

    /**
     * Creates a connection without parsing or normalizing the supplied values.
     *
     * @param id connection ID
     * @param from one endpoint facility name, such as AS1
     * @param to the other endpoint facility name, such as CLB
     * @param distanceInMetres recorded distance in metres
     * @param accessibility recorded accessibility; UNKNOWN means unconfirmed
     * @param type physical traversal category
     * @param shelter recorded shelter; UNKNOWN means unconfirmed
     * @param knownBarrier optional known barrier, or null
     * @param notes optional notes, or null
     */
    public Connection(int id, String from, String to, int distanceInMetres,
            AccessibilityStatus accessibility, TraversalType type, ShelterStatus shelter,
            String knownBarrier, String notes) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.distanceInMetres = distanceInMetres;
        this.accessibility = accessibility;
        this.type = type;
        this.shelter = shelter;
        this.knownBarrier = knownBarrier;
        this.notes = notes;
    }

    /**
     * Returns the connection ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns one endpoint facility name, without implying a travel direction.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the other endpoint facility name.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the recorded distance in metres.
     */
    public int getDistanceInMetres() {
        return distanceInMetres;
    }

    /**
     * Returns the recorded accessibility status.
     */
    public AccessibilityStatus getAccessibility() {
        return accessibility;
    }

    /**
     * Returns the physical traversal category.
     */
    public TraversalType getType() {
        return type;
    }

    /**
     * Returns the recorded shelter status.
     */
    public ShelterStatus getShelter() {
        return shelter;
    }

    /**
     * Returns the known barrier, or null if none was recorded.
     */
    public String getKnownBarrier() {
        return knownBarrier;
    }

    /**
     * Returns the notes, or null if none were recorded.
     */
    public String getNotes() {
        return notes;
    }
}

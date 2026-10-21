package seedu.unienable.model;

import java.time.LocalDate;
import java.time.LocalTime;

import seedu.unienable.model.enums.DemandLevel;

/**
 * Stores activity details, completion state and independently optional ratings.
 * A null rating means unset; a recorded rating is an integer from 1 to 5.
 * Input formatting and current-time validation belong to the command parser.
 */
public class Activity {
    private final String name;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final DemandLevel demand;

    /** Recorded importance from 1 to 5, or null when unset. */
    private final Integer importance;

    /** Recorded urgency from 1 to 5, or null when unset. */
    private final Integer urgency;

    /** Recorded commitment from 1 to 5, or null when unset. */
    private final Integer commitment;
    private boolean isDone;

    /**
     * Creates an activity with all ratings unset, preserving the V1.0 constructor contract.
     *
     * @param name activity name.
     * @param date activity date.
     * @param startTime start time.
     * @param endTime end time.
     * @param demand estimated demand level.
     * @param isDone whether the activity is completed.
     */
    public Activity(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
            DemandLevel demand, boolean isDone) {
        this(name, date, startTime, endTime, demand, isDone, null, null, null);
    }

    /**
     * Creates an activity with independently optional ratings.
     * No current-time check is made so saved historical activities can be restored.
     *
     * @param name activity name.
     * @param date activity date.
     * @param startTime start time.
     * @param endTime end time.
     * @param demand estimated demand level, separate from the ratings.
     * @param isDone whether the activity is completed.
     * @param importance importance from 1 to 5, or null when unset.
     * @param urgency urgency from 1 to 5, or null when unset.
     * @param commitment commitment from 1 to 5, or null when unset.
     * @throws IllegalArgumentException if a supplied rating is outside 1 to 5.
     */
    public Activity(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
            DemandLevel demand, boolean isDone, Integer importance, Integer urgency, Integer commitment) {
        validateRating(importance);
        validateRating(urgency);
        validateRating(commitment);
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.demand = demand;
        this.isDone = isDone;
        this.importance = importance;
        this.urgency = urgency;
        this.commitment = commitment;
    }

    /**
     * Checks the shared numeric rating rule for activity constructors and other feature owners.
     * Text parsing remains the caller's responsibility.
     *
     * @param rating rating to check, or null when unset.
     * @throws IllegalArgumentException if the rating is outside 1 to 5.
     */
    public static void validateRating(Integer rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5, or null when unset.");
        }
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public DemandLevel getDemand() {
        return demand;
    }

    /**
     * Returns the recorded importance; callers must check for null before numeric use.
     *
     * @return importance from 1 to 5, or null when unset.
     */
    public Integer getImportance() {
        return importance;
    }

    /**
     * Returns the recorded urgency; callers must check for null before numeric use.
     *
     * @return urgency from 1 to 5, or null when unset.
     */
    public Integer getUrgency() {
        return urgency;
    }

    /**
     * Returns the recorded commitment; callers must check for null before numeric use.
     *
     * @return commitment from 1 to 5, or null when unset.
     */
    public Integer getCommitment() {
        return commitment;
    }

    /**
     * Calculates the combined priority score used by {@code list priority}.
     * The score is {@code urgency * urgency * (importance + commitment)}.
     * Completion state does not affect the score, but all three ratings must
     * be present to calculate it.
     *
     * @return priority score from 2 to 250, or null when any rating is unset.
     */
    public Integer getPriorityScore() {
        if (importance == null || urgency == null || commitment == null) {
            return null;
        }
        return urgency * urgency * (importance + commitment);
    }

    /**
     * Sets whether this activity is completed.
     *
     * @param isDone true when the activity has been completed.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the completion state used by list display and persistence.
     *
     * @return true when this activity is completed.
     */
    public boolean isDone() {
        return isDone;
    }

}

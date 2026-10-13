package seedu.unienable.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import seedu.unienable.exception.InvalidIndexException;

/**
 * Owns activity collection state without exposing its mutable backing list.
 * The backing list stays in insertion (add) order; the display orderings used by
 * {@code list} and {@code list demand} are applied on demand by the view methods,
 * so Branch 1's append contract is untouched.
 */
public class ActivityList {
    /**
     * Canonical {@code list} order: date, then start time; stable ties keep add order.
     */
    private static final Comparator<Activity> CANONICAL_ORDER =
            Comparator.comparing(Activity::getDate).thenComparing(Activity::getStartTime);

    /**
     * HIGH to LOW, reversed because the enum declares LOW, MEDIUM, HIGH; ties keep canonical order.
     */
    private static final Comparator<Activity> DEMAND_ORDER =
            Comparator.comparing(Activity::getDemand).reversed().thenComparing(CANONICAL_ORDER);

    private final List<Activity> activities;

    /**
     * Creates an empty activity collection.
     */
    public ActivityList() {
        this(List.of());
    }

    /**
     * Creates an activity collection from a copy of the supplied insertion order.
     *
     * @param activities initial activities in insertion order.
     */
    public ActivityList(List<Activity> activities) {
        this.activities = new ArrayList<>(activities);
    }

    /**
     * Appends without ordering; the views apply display order, so Branch 1's
     * append contract is untouched.
     *
     * @param activity non-null activity to append.
     * @throws NullPointerException if the activity is null
     */
    public void addActivity(Activity activity) {
        activities.add(Objects.requireNonNull(activity));
    }

    /**
     * Returns an immutable snapshot sorted by date, then start time, the order {@code list}
     * displays and {@code delete INDEX} resolves its index against.
     */
    public List<Activity> getCanonicalView() {
        return activities.stream().sorted(CANONICAL_ORDER).toList();
    }

    /**
     * Returns an immutable snapshot grouped by demand (HIGH to LOW); ties keep canonical order.
     */
    public List<Activity> getDemandView() {
        return activities.stream().sorted(DEMAND_ORDER).toList();
    }

    /**
     * Resolves a 1-based displayed index against the canonical view, removes that
     * activity, and returns it. Removal is by object rather than by raw backing-list
     * position, so the displayed index always refers to the order shown by
     * {@code list}, never to insertion order.
     *
     * @param displayedIndex 1-based index shown by the chronological {@code list} command.
     * @return the removed activity
     * @throws InvalidIndexException when there are no activities, or when the index
     *         is outside the range 1 to (number of activities)
     */
    public Activity deleteByDisplayedIndex(int displayedIndex) throws InvalidIndexException {
        List<Activity> canonical = getCanonicalView();
        if (canonical.isEmpty()) {
            throw new InvalidIndexException("[WARNING] There are no activities to delete.");
        }
        if (displayedIndex < 1 || displayedIndex > canonical.size()) {
            throw new InvalidIndexException("[WARNING] No activity at index " + displayedIndex
                    + ". Valid range: 1 to " + canonical.size() + ".");
        }
        Activity removed = canonical.get(displayedIndex - 1);
        activities.remove(removed);
        return removed;
    }

    /**
     * Resolves a user-visible one-based index against the canonical chronological view.
     *
     * @param displayedIndex index shown by the chronological list command
     * @return the activity at that index
     * @throws InvalidIndexException when the index is outside the current canonical view
     */
    public Activity getByDisplayedIndex(int displayedIndex) throws InvalidIndexException {
        List<Activity> canonical = getCanonicalView();
        if (canonical.isEmpty()) {
            throw new InvalidIndexException("[WARNING] There are no activities to update.");
        }
        if (displayedIndex < 1 || displayedIndex > canonical.size()) {
            throw new InvalidIndexException("[WARNING] No activity at index " + displayedIndex
                    + ". Valid range: 1 to " + canonical.size() + ".");
        }
        return canonical.get(displayedIndex - 1);
    }

    /**
     * Updates completion state using the same canonical displayed index as list and delete.
     *
     * @param displayedIndex index shown by the chronological list command
     * @param done desired completion state
     * @return true when the activity state changed, false when it already had that state
     * @throws InvalidIndexException when the index is outside the current canonical view
     */
    public boolean setDoneAtDisplayedIndex(int displayedIndex, boolean done)
            throws InvalidIndexException {
        Activity activity = getByDisplayedIndex(displayedIndex);
        if (activity.isDone() == done) {
            return false;
        }
        activity.setDone(done);
        return true;
    }

    /**
     * Returns an immutable snapshot in insertion (add) order, the storage order rather than display order;
     * use {@link #getCanonicalView()} when the ordering that the user sees is needed.
     */
    public List<Activity> getActivities() {
        return List.copyOf(activities);
    }
}

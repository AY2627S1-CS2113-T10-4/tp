package seedu.unienable.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns activity collection state without exposing its mutable backing list.
 */
public class ActivityList {
    private final List<Activity> activities;

    public ActivityList() {
        this(List.of());
    }

    public ActivityList(List<Activity> activities) {
        this.activities = new ArrayList<>(activities);
    }

    /**
     * Appends a newly created activity to the current in-memory collection.
     * Branch 2 can build a sorted presentation without changing this contract.
     *
     * @param activity new activity to store
     */
    public void addActivity(Activity activity) {
        activities.add(java.util.Objects.requireNonNull(activity));
    }
    // TODO [Branch 2]: Add agreed ordering, displayed-index lookup, and deletion APIs here.
    // TODO [Branch 3]: Add completion update APIs here after agreeing on indexes with Branch 2.
    // Coordinate edits to this shared class; command and parser files have separate owners.

    /**
     * Returns an immutable snapshot in the collection's current order.
     */
    public List<Activity> getActivities() {
        return List.copyOf(activities);
    }
}

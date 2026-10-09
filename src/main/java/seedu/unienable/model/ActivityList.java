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

    // TODO [Branch 1]: Add the insertion API here after coordinating with Branch 2.
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

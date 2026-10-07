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
     * Returns an immutable snapshot in the collection's current order.
     */
    public List<Activity> getActivities() {
        return List.copyOf(activities);
    }
}

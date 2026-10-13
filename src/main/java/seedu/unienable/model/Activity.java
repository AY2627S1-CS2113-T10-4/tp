package seedu.unienable.model;

import java.time.LocalDate;
import java.time.LocalTime;

import seedu.unienable.model.enums.DemandLevel;

/**
 * Holds the agreed activity state; validation and feature behavior belong to later PRs.
 */
public class Activity {
    private final String name;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final DemandLevel demand;
    private boolean isDone;

    // TODO [Branch 1]: Coordinate any model validation here with Branch 3.
    // Prefer input validation in your own command/parser files when no model change is needed.
    public Activity(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                    DemandLevel demand, boolean isDone) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.demand = demand;
        this.isDone = isDone;
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
     * Sets whether this activity is completed.
     *
     * @param done true when the activity has been completed
     */
    public void setDone(boolean done) {
        isDone = done;
    }

    /**
     * Returns the completion state used by list display and persistence.
     *
     * @return true when this activity is completed
     */
    public boolean isDone() {
        return isDone;
    }
}

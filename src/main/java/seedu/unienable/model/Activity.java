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
    private final boolean isDone;

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

    public boolean isDone() {
        return isDone;
    }
}

package seedu.unienable.command.activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

/**
 * Edits the supplied fields of one activity selected by its chronological index.
 */
public class EditCommand extends Command {
    private final int displayedIndex;
    private final String name;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final DemandLevel demand;
    private final boolean nameSupplied;
    private final boolean dateSupplied;
    private final boolean startTimeSupplied;
    private final boolean endTimeSupplied;
    private final boolean demandSupplied;

    /**
     * Creates an edit command. Null values are ignored when their corresponding
     * supplied flag is false.
     */
    public EditCommand(int displayedIndex, String name, LocalDate date, LocalTime startTime,
                       LocalTime endTime, DemandLevel demand, boolean nameSupplied,
                       boolean dateSupplied, boolean startTimeSupplied, boolean endTimeSupplied,
                       boolean demandSupplied) {
        this.displayedIndex = displayedIndex;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.demand = demand;
        this.nameSupplied = nameSupplied;
        this.dateSupplied = dateSupplied;
        this.startTimeSupplied = startTimeSupplied;
        this.endTimeSupplied = endTimeSupplied;
        this.demandSupplied = demandSupplied;
    }

    /**
     * Validates the resulting activity before applying any field, keeping invalid
     * edits atomic.
     */
    @Override
    public CommandResult execute(ActivityList activities, Storage storage) throws UniEnableException {
        Activity activity = activities.getByDisplayedIndex(displayedIndex);
        String updatedName = nameSupplied ? name : activity.getName();
        LocalDate updatedDate = dateSupplied ? date : activity.getDate();
        LocalTime updatedStart = startTimeSupplied ? startTime : activity.getStartTime();
        LocalTime updatedEnd = endTimeSupplied ? endTime : activity.getEndTime();
        DemandLevel updatedDemand = demandSupplied ? demand : activity.getDemand();

        if (dateSupplied || startTimeSupplied || endTimeSupplied) {
            if (!updatedEnd.isAfter(updatedStart)) {
                throw warning("End time must be later than start time on the same day.");
            }
            LocalDateTime endDateTime = LocalDateTime.of(updatedDate, updatedEnd);
            if (!endDateTime.isAfter(LocalDateTime.now())) {
                throw warning("Edited activity must end after the current local time.");
            }
        }

        activity.updateDetails(updatedName, updatedDate, updatedStart, updatedEnd, updatedDemand);
        String message = "Edited activity: " + activity.getName() + "\n"
                + "Date: " + activity.getDate() + "\n"
                + "Time: " + activity.getStartTime() + " - " + activity.getEndTime() + "\n"
                + "Demand: " + activity.getDemand();
        return new CommandResult(message, false, true);
    }

    private UniEnableException warning(String message) {
        return new UniEnableException("[WARNING] " + message);
    }
}

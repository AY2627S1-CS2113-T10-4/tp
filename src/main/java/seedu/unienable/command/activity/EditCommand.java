package seedu.unienable.command.activity;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import seedu.unienable.command.Command;
import seedu.unienable.command.CommandResult;
import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.Activity;
import seedu.unienable.model.ActivityList;
import seedu.unienable.model.enums.DemandLevel;
import seedu.unienable.storage.Storage;

//@@author Oliver-koh
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
    private final Integer importance;
    private final Integer urgency;
    private final Integer commitment;
    private final boolean nameSupplied;
    private final boolean dateSupplied;
    private final boolean startTimeSupplied;
    private final boolean endTimeSupplied;
    private final boolean demandSupplied;
    private final boolean importanceSupplied;
    private final boolean urgencySupplied;
    private final boolean commitmentSupplied;
    private final Clock clock;

    /**
     * Creates an edit command using the system clock.
     */
    public EditCommand(int displayedIndex, String name, LocalDate date, LocalTime startTime,
            LocalTime endTime, DemandLevel demand, boolean nameSupplied,
            boolean dateSupplied, boolean startTimeSupplied, boolean endTimeSupplied,
            boolean demandSupplied, Integer importance, Integer urgency, Integer commitment,
            boolean importanceSupplied, boolean urgencySupplied, boolean commitmentSupplied) {
        this(displayedIndex, name, date, startTime, endTime, demand, nameSupplied, dateSupplied,
                startTimeSupplied, endTimeSupplied, demandSupplied, importance, urgency, commitment,
                importanceSupplied, urgencySupplied, commitmentSupplied, Clock.systemDefaultZone());
    }

    /**
     * Creates an edit command with an injectable clock for deterministic validation.
     */
    public EditCommand(int displayedIndex, String name, LocalDate date, LocalTime startTime,
            LocalTime endTime, DemandLevel demand, boolean nameSupplied,
            boolean dateSupplied, boolean startTimeSupplied, boolean endTimeSupplied,
            boolean demandSupplied, Integer importance, Integer urgency, Integer commitment,
            boolean importanceSupplied, boolean urgencySupplied, boolean commitmentSupplied,
            Clock clock) {
        this.displayedIndex = displayedIndex;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.demand = demand;
        this.importance = importance;
        this.urgency = urgency;
        this.commitment = commitment;
        this.nameSupplied = nameSupplied;
        this.dateSupplied = dateSupplied;
        this.startTimeSupplied = startTimeSupplied;
        this.endTimeSupplied = endTimeSupplied;
        this.demandSupplied = demandSupplied;
        this.importanceSupplied = importanceSupplied;
        this.urgencySupplied = urgencySupplied;
        this.commitmentSupplied = commitmentSupplied;
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Validates the resulting activity before replacing any field, keeping invalid
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
        Integer updatedImportance = importanceSupplied ? importance : activity.getImportance();
        Integer updatedUrgency = urgencySupplied ? urgency : activity.getUrgency();
        Integer updatedCommitment = commitmentSupplied ? commitment : activity.getCommitment();

        if (dateSupplied || startTimeSupplied || endTimeSupplied) {
            if (!updatedEnd.isAfter(updatedStart)) {
                throw warning("End time must be later than start time on the same day.");
            }
            LocalDateTime endDateTime = LocalDateTime.of(updatedDate, updatedEnd);
            if (!endDateTime.isAfter(LocalDateTime.now(clock))) {
                throw warning("Edited activity must end after the current local time.");
            }
        }

        Activity replacement = new Activity(updatedName, updatedDate, updatedStart, updatedEnd,
                updatedDemand, activity.isDone(), updatedImportance, updatedUrgency, updatedCommitment);
        activities.replaceByDisplayedIndex(displayedIndex, replacement);
        String message = "Edited activity: " + replacement.getName() + "\n"
                + "Date: " + replacement.getDate() + "\n"
                + "Time: " + replacement.getStartTime() + " - " + replacement.getEndTime() + "\n"
                + "Demand: " + replacement.getDemand() + "\n"
                + "Importance: " + formatRating(replacement.getImportance()) + "\n"
                + "Urgency: " + formatRating(replacement.getUrgency()) + "\n"
                + "Commitment: " + formatRating(replacement.getCommitment());
        return new CommandResult(message, false, true);
    }

    private String formatRating(Integer rating) {
        return rating == null ? "-" : rating.toString();
    }

    private UniEnableException warning(String message) {
        return new UniEnableException("[WARNING] " + message);
    }
}
//@@author

package seedu.unienable.storage;

import seedu.unienable.exception.UniEnableException;
import seedu.unienable.model.ActivityList;

/**
 * Reserves the persistence API. No files are read or written in this baseline.
 */
public class Storage {
    /**
     * Future implementation will load data/activities.txt.
     */
    public ActivityList load() throws UniEnableException {
        // TODO [Branch 3]: Replace this exception with loading and corrupted-data handling.
        // Agree on the data/activities.txt format and recovery policy before implementation.
        throw new UniEnableException("Activity loading is not implemented in the baseline.");
    }

    /**
     * Future implementation will save activities to data/activities.txt.
     */
    public void save(ActivityList activities) throws UniEnableException {
        // TODO [Branch 3]: Replace this exception with saving and I/O error handling.
        throw new UniEnableException("Activity saving is not implemented in the baseline.");
    }
}

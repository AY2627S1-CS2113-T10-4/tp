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
        throw new UniEnableException("Activity loading is not implemented in the baseline.");
    }

    /**
     * Future implementation will save activities to data/activities.txt.
     */
    public void save(ActivityList activities) throws UniEnableException {
        throw new UniEnableException("Activity saving is not implemented in the baseline.");
    }
}

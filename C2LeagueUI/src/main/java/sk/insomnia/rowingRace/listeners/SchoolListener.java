package sk.insomnia.rowingRace.listeners;

import sk.insomnia.rowingRace.exceptions.RowingRaceException;
import sk.insomnia.rowingRace.so.School;

/**
 * Created by bobek on 6/29/2014.
 */
public interface SchoolListener {
    public void onSchoolSelected(School school) throws RowingRaceException;
}

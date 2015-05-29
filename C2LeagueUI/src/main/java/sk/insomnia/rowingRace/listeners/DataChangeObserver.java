package sk.insomnia.rowingRace.listeners;

import sk.insomnia.rowingRace.exceptions.RowingRaceException;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.rowingRace.so.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobek on 6/28/2014.
 */
public class DataChangeObserver {

    private static final List<TeamsListener> teamsListeners = new ArrayList<TeamsListener>();
    private static final List<SchoolListener> schoolListeners = new ArrayList<SchoolListener>();
    private static final List<RaceListener> raceListeners = new ArrayList<RaceListener>();

    public static void registerTeamsListener(TeamsListener teamsListener) {
        teamsListeners.add(teamsListener);
    }

    public static void registerSchoolListener(SchoolListener schoolListener) {
        schoolListeners.add(schoolListener);
    }
    public static void registerRaceListener(RaceListener raceListener){
        raceListeners.add(raceListener);
    }


    public static void notifySchoolSelected(School school) throws RowingRaceException {
        for (SchoolListener schoolListener : schoolListeners) {
            schoolListener.onSchoolSelected(school);
        }
    }

    public static void notifyTeamChange(List<Team> teamDtos) {
        for (TeamsListener teamsListener : teamsListeners) {
            teamsListener.onTeamsChange(teamDtos);
        }
    }

    public static void notifyRaceSelected(RowingRace race){
        for (RaceListener raceListener : raceListeners){
            raceListener.onRaceSelected(race);
        }
    }
}

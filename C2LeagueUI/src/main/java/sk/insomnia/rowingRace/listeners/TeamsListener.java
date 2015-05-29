package sk.insomnia.rowingRace.listeners;

import sk.insomnia.rowingRace.so.Team;

import java.util.List;

/**
 * Created by bobek on 6/28/2014.
 */
public interface TeamsListener {

    public void onTeamsChange(List<Team> teamDtoList);

}

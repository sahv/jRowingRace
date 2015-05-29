package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;
import java.util.List;

import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.RaceRound;

public interface RaceRoundDao {

	public void saveOrUpdate(RaceRound raceRound) throws SQLException,ConnectivityException;
	public RaceRound getById(Long id) throws SQLException,ConnectivityException;
    public void delete(RaceRound round) throws SQLException;
}

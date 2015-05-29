package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;
import java.util.List;

import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.RowingRace;

public interface RowingRaceDao {
	public void deleteRaceYear(RaceYear year) throws SQLException,ConnectivityException;
    public RowingRace getRowingRace() throws SQLException,ConnectivityException;
	public RowingRace getRowingRace(List<EnumEntity> raceCategories) throws SQLException,ConnectivityException;
    public void saveOrUpdate(RaceYear raceYear) throws SQLException,ConnectivityException;
}

package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;

import sk.insomnia.rowingRace.so.Racer;

public interface RacerDao {

	public void saveOrUpdate(Racer racer,Long teamId) throws SQLException;
	public void deleteRacer(Racer racer) throws SQLException;
	public Racer findById(Long id) throws SQLException;
	
	
}

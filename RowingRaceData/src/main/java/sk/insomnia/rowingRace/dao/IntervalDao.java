package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;

import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Interval;

public interface IntervalDao {

	public void saveOrUpdate(Interval interval, Long disciplineId) throws SQLException,ConnectivityException;
	public Interval getById(Long id) throws SQLException,ConnectivityException;
	
}

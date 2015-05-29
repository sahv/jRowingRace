package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;

import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Discipline;

public interface DisciplineDao {
	
	public void saveOrUpdate(Discipline discipline,Long discisplineCategoryId) throws SQLException,ConnectivityException;
	public Discipline getById(Long id) throws SQLException,ConnectivityException;

}

package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;
import java.util.List;

import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.DisciplineCategory;

public interface DisciplineCategoryDao {

	public DisciplineCategory findById(Long id) throws SQLException,ConnectivityException;
	public List<DisciplineCategory> getAll() throws SQLException,ConnectivityException;
	public void saveOrUpdate(DisciplineCategory disciplineCategory) throws SQLException,ConnectivityException;
	
}

package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;
import java.util.List;

import sk.insomnia.rowingRace.so.School;

public interface SchoolDao {

	public School findById(Long id) throws SQLException;
	public School findByCode(String code) throws SQLException;
	public void saveSchool(School school) throws SQLException;
	public void delete(School school) throws SQLException;
	public Integer getSchoolCode() throws SQLException;
    public List<School> getSchools() throws SQLException;
	
}

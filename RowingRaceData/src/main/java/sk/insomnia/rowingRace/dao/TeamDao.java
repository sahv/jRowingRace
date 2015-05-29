package sk.insomnia.rowingRace.dao;

import sk.insomnia.rowingRace.so.Team;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TeamDao {

    public void saveOrUpdate(Team team, Long schoolId) throws SQLException;

    public void delete(Team team) throws SQLException;

    public void deleteTeam(Long teamId) throws SQLException;

    public Team findById(Long id) throws SQLException;

    public Team findById(Long id, Connection connection) throws SQLException;

    public List<Team> findBySchoolId(Long schoolId) throws SQLException;

    public List<Team> findBySchoolId(Long schoolId, Connection connection) throws SQLException;
}

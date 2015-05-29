package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.dao.RacerDao;
import sk.insomnia.rowingRace.dao.TeamDao;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.Racer;
import sk.insomnia.rowingRace.so.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeamDaoImpl {


    private static TeamDao instance;

    private TeamDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static TeamDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements TeamDao {

        private static final CodeTableDao codeTableDao = CodeTableDaoImpl.getInstance();
        private static final RacerDao racerDao = RacerDaoImpl.getInstance();

        public void delete(Team team) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (team.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_TEAM SET DELETED=1 WHERE TEAM_ID=?");
                ps.setLong(1, team.getId());
                ps.executeUpdate();
                ps.close();
            }
        }

        @Override
        public void deleteTeam(Long teamId) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (teamId != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_TEAM SET DELETED=1 WHERE TEAM_ID=?");
                ps.setLong(1, teamId);
                ps.executeUpdate();
                ps.close();
            }
        }

        public void saveOrUpdate(Team team, Long schoolID) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (team.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_TEAM SET NAME=?,CATEGORY_FK=?,MAX_RACERS=?,NUMBER_OF_ALTERNATES=? WHERE TEAM_ID=?");
                ps.setString(1, team.getName());
                ps.setLong(2, team.getTeamCategory().getId());
                ps.setInt(3, team.getMaxRacers());
                ps.setInt(4, team.getNumberOfAlternates());
                ps.setLong(5, team.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_TEAM (NAME,CATEGORY_FK,MAX_RACERS,NUMBER_OF_ALTERNATES) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, team.getName());
                ps.setLong(2, team.getTeamCategory().getId());
                ps.setInt(3, team.getMaxRacers());
                ps.setInt(4, team.getNumberOfAlternates());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    team.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();

                ps = connection.prepareStatement("INSERT INTO RR_SCHOOL_TEAMS (SCHOOL_FK,TEAM_FK) VALUES (?,?)");
                ps.setLong(1, schoolID);
                ps.setLong(2, team.getId());
                ps.executeUpdate();
                ps.close();

            }
            DbConnection.releaseConnection(connection);
        }


        public Team findById(Long id) throws SQLException {
            Connection connection = DbConnection.getConnection();
            Team team = findById(id, connection);
            DbConnection.releaseConnection(connection);
            return team;
        }

        @Override
        public Team findById(Long id, Connection connection) throws SQLException {
            Team team = null;
            PreparedStatement ps = connection.prepareStatement("SELECT NAME,CATEGORY_FK,MAX_RACERS,NUMBER_OF_ALTERNATES FROM RR_TEAM WHERE TEAM_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                team = new Team();
                team.setId(id);
                team.setName(rs.getString("NAME"));
                team.setMaxRacers(rs.getInt("MAX_RACERS"));
                team.setNumberOfAlternates(rs.getInt("NUMBER_OF_ALTERNATES"));
                team.setTeamCategory(codeTableDao.findById(rs.getLong("CATEGORY_FK"), RowingRaceCodeTables.CT_TEAM_CATEGORIES));
            }
            ps.close();
            rs.close();

            ps = connection.prepareStatement("SELECT RACER_FK FROM RR_TEAM_RACERS TR, RR_RACER R WHERE TR.TEAM_FK=? AND TR.RACER_FK=R.RACER_ID AND R.DELETED=0");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (team.getRacers() == null) {
                    team.setRacers(new ArrayList<Racer>());
                }
                team.getRacers().add(racerDao.findById(rs.getLong("RACER_FK")));
            }
            ps.close();
            rs.close();
            return team;
        }

        @Override
        public List<Team> findBySchoolId(Long schoolId) throws SQLException {
            Connection connection = DbConnection.getConnection();
            List<Team> teams = findBySchoolId(schoolId, connection);
            DbConnection.releaseConnection(connection);
            return teams;
        }

        @Override
        public List<Team> findBySchoolId(Long schoolId, Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement("SELECT TEAM_ID, NAME, CATEGORY_FK, MAX_RACERS, NUMBER_OF_ALTERNATES FROM RR_SCHOOL_TEAMS ST, RR_TEAM T WHERE SCHOOL_FK=? AND ST.TEAM_FK=T.TEAM_ID AND T.DELETED=0");
            ps.setLong(1, schoolId);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            List<Team> teams = new ArrayList<>();
            while (rs.next()) {
                Team team = new Team();
                team.setId(rs.getLong("TEAM_ID"));
                team.setMaxRacers(rs.getInt("MAX_RACERS"));
                team.setName(rs.getString("NAME"));
                EnumEntity category = new EnumEntitySO();
                category.setId(rs.getLong("CATEGORY_FK"));
                team.setTeamCategory(category);
                team.setNumberOfAlternates(rs.getInt("NUMBER_OF_ALTERNATES"));
                teams.add(team);
            }
            rs.close();
            rs.close();
            return teams;
        }
    }
}

package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.DisciplineDao;
import sk.insomnia.rowingRace.dao.RaceRoundDao;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RaceRoundDaoImpl {

    private static RaceRoundDao instance;

    private RaceRoundDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static RaceRoundDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements RaceRoundDao {

        private static final DisciplineDao disciplineDao = DisciplineDaoImpl.getInstance();

        public void saveOrUpdate(RaceRound raceRound) throws SQLException, ConnectivityException {
            Connection connection = DbConnection.getConnection();
            if (raceRound.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_RACE_ROUND SET ROUND_NUMBER=?,BEGIN=?,END=?,DISCIPLINE_FK=?,ROUND_DESCRIPTION=?,RACE_YEAR_FK=? WHERE RACE_ROUND_ID=?");
                ps.setInt(1, raceRound.getRoundNumber());
                ps.setDate(2, new java.sql.Date(raceRound.getBegin().getTime()));
                ps.setDate(3, new java.sql.Date(raceRound.getEnd().getTime()));
                ps.setLong(4, raceRound.getDiscipline().getId());
                ps.setString(5, raceRound.getDescription());
                ps.setLong(6, raceRound.getRaceYear().getId());
                ps.setLong(7, raceRound.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_RACE_ROUND (ROUND_NUMBER,BEGIN,END,DISCIPLINE_FK,ROUND_DESCRIPTION,RACE_YEAR_FK) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, raceRound.getRoundNumber());
                ps.setDate(2, new java.sql.Date(raceRound.getBegin().getTime()));
                ps.setDate(3, new java.sql.Date(raceRound.getEnd().getTime()));
                ps.setLong(4, raceRound.getDiscipline().getId());
                ps.setString(5, raceRound.getDescription());
                ps.setLong(6, raceRound.getRaceYear().getId());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    raceRound.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }
            DbConnection.releaseConnection(connection);
        }


        public RaceRound getById(Long id) throws SQLException, ConnectivityException {
            RaceRound raceRound = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT ROUND_NUMBER,BEGIN,END,DISCIPLINE_FK,ROUND_DESCRIPTION,RACE_YEAR_FK FROM RR_RACE_ROUND WHERE RACE_ROUND_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                raceRound = new RaceRound();
                raceRound.setDescription(rs.getString("ROUND_DESCRIPTION"));
                raceRound.setId(id);
                raceRound.setBegin(rs.getDate("BEGIN"));
                raceRound.setEnd(rs.getDate("END"));
                raceRound.setRaceYear(new RaceYear(rs.getLong("RACE_YEAR_FK")));
                raceRound.setDiscipline(disciplineDao.getById(rs.getLong("DISCIPLINE_FK")));
                raceRound.setRoundNumber(rs.getInt("ROUND_NUMBER"));
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return raceRound;
        }

        public void delete(RaceRound round) throws SQLException {
            Connection connection = DbConnection.getConnection();

            PreparedStatement ps = connection.prepareStatement("DELETE FROM RR_RACE_ROUND WHERE RACE_ROUND_ID=?");
            ps.setLong(1, round.getId());
            ps.executeUpdate();
            ps.close();

            DbConnection.releaseConnection(connection);
        }


    }
}

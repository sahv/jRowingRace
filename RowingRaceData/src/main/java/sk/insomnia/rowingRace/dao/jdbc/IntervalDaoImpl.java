package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.dao.IntervalDao;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Interval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IntervalDaoImpl {

    private static IntervalDao instance;

    private IntervalDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static IntervalDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements IntervalDao {

        private static final CodeTableDao codeTableDao = CodeTableDaoImpl.getInstance();

        public void saveOrUpdate(Interval interval, Long disciplineId) throws SQLException,
                ConnectivityException {
            Connection connection = DbConnection.getConnection();
            if (interval.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_INTERVAL SET DIMENSION_FK=?,WORKOUT_VALUE=?,REST_VALUE=?,INTERVAL_NUMBER=?,RELAY_SPLIT_VALUE=? WHERE INTERVAL_ID=?");
                ps.setLong(1, interval.getDimension().getId());
                ps.setInt(2, interval.getWorkoutValue());
                ps.setInt(3, interval.getRestValue());
                ps.setString(4, interval.getIntervalNumber());
                ps.setInt(5, interval.getRelaySplitValue());
                ps.setLong(6, interval.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_INTERVAL (DIMENSION_FK,WORKOUT_VALUE,REST_VALUE,INTERVAL_NUMBER,RELAY_SPLIT_VALUE) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, interval.getDimension().getId());
                ps.setInt(2, interval.getWorkoutValue());
                ps.setInt(3, interval.getRestValue());
                ps.setString(4, interval.getIntervalNumber());
                ps.setInt(5, interval.getRelaySplitValue());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    interval.setId(rs.getLong(1));
                }
                rs.close();
                ps.close();

                ps = connection.prepareStatement("INSERT INTO RR_DISCIPLINE_INTERVALS (DISCIPLINE_FK,INTERVAL_FK) VALUES (?,?)");
                ps.setLong(1, disciplineId);
                ps.setLong(2, interval.getId());
                ps.executeUpdate();
                ps.close();


            }
            DbConnection.releaseConnection(connection);
        }


        public Interval getById(Long id) throws SQLException, ConnectivityException {
            Interval interval = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT DIMENSION_FK,WORKOUT_VALUE,REST_VALUE,INTERVAL_NUMBER,RELAY_SPLIT_VALUE FROM RR_INTERVAL WHERE INTERVAL_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                interval = new Interval();
                interval.setId(id);
                interval.setDimension(codeTableDao.findById(rs.getLong("DIMENSION_FK"), RowingRaceCodeTables.CT_INTERVAL_DIMENSION));
                interval.setWorkoutValue(rs.getInt("WORKOUT_VALUE"));
                interval.setRestValue(rs.getInt("REST_VALUE"));
                interval.setIntervalNumber(rs.getString("INTERVAL_NUMBER"));
                interval.setRelaySplitValue(rs.getInt("RELAY_SPLIT_VALUE"));
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return interval;
        }
    }
}

package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.RacerDao;
import sk.insomnia.rowingRace.so.Racer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RacerDaoImpl {

    private static RacerDao instance;

    private RacerDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static RacerDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements RacerDao {


        public void deleteRacer(Racer racer) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (racer.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_RACER SET DELETED=1 WHERE RACER_ID=?");
                ps.setLong(1, racer.getId());
                ps.executeUpdate();
                ps.close();
            }
        }

        public void saveOrUpdate(Racer racer, Long teamId) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (racer.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_RACER SET NAME=?,SURNAME=?,YOB=? WHERE RACER_ID=?");
                ps.setString(1, racer.getName());
                ps.setString(2, racer.getSurname());
                ps.setInt(3, racer.getYearOfBirth());
                ps.setLong(4, racer.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_RACER (NAME,SURNAME,YOB) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, racer.getName());
                ps.setString(2, racer.getSurname());
                ps.setInt(3, racer.getYearOfBirth());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    racer.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();

                ps = connection.prepareStatement("INSERT INTO RR_TEAM_RACERS (TEAM_FK,RACER_FK) VALUES (?,?)");
                ps.setLong(1, teamId);
                ps.setLong(2, racer.getId());
                ps.executeUpdate();
                ps.close();
            }
            DbConnection.releaseConnection(connection);
        }


        public Racer findById(Long id) throws SQLException {
            Racer racer = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT NAME,SURNAME,YOB FROM RR_RACER WHERE RACER_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                racer = new Racer();
                racer.setId(id);
                racer.setName(rs.getString("NAME"));
                racer.setSurname(rs.getString("SURNAME"));
                racer.setYearOfBirth(rs.getInt("YOB"));
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return racer;
        }

    }
}
package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.DisciplineDao;
import sk.insomnia.rowingRace.dao.IntervalDao;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.Interval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DisciplineDaoImpl {

    private static DisciplineDao instance;

    private DisciplineDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static DisciplineDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements DisciplineDao {

        private static final IntervalDao intervalDao = IntervalDaoImpl.getInstance();

        public void saveOrUpdate(Discipline discipline, Long discisplineCategoryId) throws SQLException,
                ConnectivityException {
            Connection connection = DbConnection.getConnection();
            if (discipline.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_DISCIPLINE SET NAME=? WHERE DISCIPLINE_ID=?");
                ps.setString(1, discipline.getName());
                ps.setLong(2, discipline.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_DISCIPLINE (NAME) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, discipline.getName());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    discipline.setId(rs.getLong(1));
                }
                rs.close();

                ps = connection.prepareStatement("INSERT INTO RR_CATEGORY_DISCIPLINES (D_CATEGORY_FK,DISCIPLINE_FK) VALUES (?,?)");
                ps.setLong(1, discisplineCategoryId);
                ps.setLong(2, discipline.getId());
                ps.executeUpdate();
                rs.close();


            }
            DbConnection.releaseConnection(connection);
        }


        public Discipline getById(Long id) throws SQLException,
                ConnectivityException {
            Discipline discipline = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT NAME FROM RR_DISCIPLINE WHERE DISCIPLINE_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                discipline = new Discipline();
                discipline.setId(id);
                discipline.setName(rs.getString("NAME"));

                ps = connection.prepareStatement("SELECT INTERVAL_FK FROM RR_DISCIPLINE_INTERVALS WHERE DISCIPLINE_FK=?");
                ps.setLong(1, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (discipline.getIntervals() == null) {
                        discipline.setIntervals(new ArrayList<Interval>());
                    }
                    discipline.getIntervals().add(intervalDao.getById(rs.getLong("INTERVAL_FK")));
                }
                ps.close();
                rs.close();
            }
            DbConnection.releaseConnection(connection);
            return discipline;
        }
    }
}

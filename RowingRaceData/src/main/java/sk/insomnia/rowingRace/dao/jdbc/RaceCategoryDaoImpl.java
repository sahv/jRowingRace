package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.LanguageMutationsDao;
import sk.insomnia.rowingRace.dao.RaceCategoryDao;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.LanguageMutation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RaceCategoryDaoImpl {
    private static RaceCategoryDao instance;

    private RaceCategoryDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static RaceCategoryDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements RaceCategoryDao {

        private static final LanguageMutationsDao LANGUAGE_MUTATIONS_DAO = LanguageMutationsDaoImpl.getInstance();

        public EnumEntitySO findById(Long id) throws SQLException {
            Connection connection = DbConnection.getConnection();
            EnumEntitySO raceCategory = findById(id, connection);
            DbConnection.releaseConnection(connection);
            return raceCategory;

        }

        @Override
        public EnumEntitySO findById(Long id, Connection connection) throws SQLException {
            EnumEntitySO raceCategory = new EnumEntitySO();
            PreparedStatement ps = connection.prepareStatement("SELECT ACRONYM FROM RR_RACE_CATEGORY WHERE ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                raceCategory.setId(id);
                raceCategory.setAcronym(rs.getString("ACRONYM"));
                raceCategory.setLanguageMutations(LANGUAGE_MUTATIONS_DAO.getMutationsForKey(raceCategory.getId(), RowingRaceCodeTables.CT_RACE_CATEGORY));
            }
            ps.close();
            rs.close();
            return raceCategory;
        }


        public List<EnumEntitySO> getAll() throws SQLException {
            List<EnumEntitySO> RaceCategories = new ArrayList<EnumEntitySO>();
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT ACRONYM,ID FROM RR_RACE_CATEGORY");
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                EnumEntitySO d = new EnumEntitySO();
                d.setId(rs.getLong("ID"));
                d.setAcronym(rs.getString("ACRONYM"));
                d.setLanguageMutations(LANGUAGE_MUTATIONS_DAO.getMutationsForKey(d.getId(), RowingRaceCodeTables.CT_RACE_CATEGORY));
                RaceCategories.add(d);
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return RaceCategories;
        }

        @Override
        public List<EnumEntity> findBySchoolId(Long schoolId, Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT ID, ACRONYM  FROM RR_RACE_CATEGORY RC" +
                    " JOIN RR_SCHOOL_RACE_CATEGORIES SRC ON SRC.CATEGORY_FK = RC.ID" +
                    " JOIN RR_SCHOOL S ON S.SCHOOL_ID = SRC.SCHOOL_FK" +
                    " WHERE SCHOOL_FK=?");
            ps.setLong(1, schoolId);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            List<EnumEntity> raceCategories = new ArrayList<>();
            while (rs.next()) {
                EnumEntity raceCategory = new EnumEntitySO();
                raceCategory.setId(rs.getLong("ID"));
                raceCategory.setAcronym(rs.getString("ACRONYM"));
                raceCategories.add(raceCategory);
            }
            return raceCategories;
        }


        public void saveOrUpdate(EnumEntitySO raceCategory)
                throws SQLException {
            Connection connection = DbConnection.getConnection();

            if (raceCategory.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_RACE_CATEGORY SET ACRONYM=? WHERE ID=?");
                ps.setString(1, raceCategory.getAcronym());
                ps.setLong(2, raceCategory.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_RACE_CATEGORY (ACRONYM) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, raceCategory.getAcronym());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.beforeFirst();
                if (rs.next()) {
                    raceCategory.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }

            LANGUAGE_MUTATIONS_DAO.removeAllMutationsFromKey(raceCategory, RowingRaceCodeTables.CT_RACE_CATEGORY);

            for (LanguageMutation languageMutation : raceCategory.getLanguageMutations()) {
                LANGUAGE_MUTATIONS_DAO.addMutationToKey(raceCategory.getId(), languageMutation);
            }

            DbConnection.releaseConnection(connection);
        }

    }
}

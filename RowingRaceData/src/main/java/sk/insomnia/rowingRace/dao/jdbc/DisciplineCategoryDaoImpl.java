package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.DisciplineCategoryDao;
import sk.insomnia.rowingRace.dao.DisciplineDao;
import sk.insomnia.rowingRace.dao.LanguageMutationsDao;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.DisciplineCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DisciplineCategoryDaoImpl {

    private static DisciplineCategoryDao instance;

    private DisciplineCategoryDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static DisciplineCategoryDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements DisciplineCategoryDao {

        private static final DisciplineDao disciplineDao = DisciplineDaoImpl.getInstance();


        private static final LanguageMutationsDao LANGUAGE_MUTATIONS_DAO = LanguageMutationsDaoImpl.getInstance();
        private static final DisciplineDao DISCIPLINE_DAO = DisciplineDaoImpl.getInstance();

        public DisciplineCategory findById(Long id) throws SQLException, ConnectivityException {
            DisciplineCategory disciplineCategory = new DisciplineCategory();
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT ACRONYM FROM RR_DISCIPLINE_CATEGORY WHERE ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (rs.next()) {
                disciplineCategory.setId(id);
                disciplineCategory.setAcronym(rs.getString("ACRONYM"));

                ps = connection.prepareStatement("SELECT DISCIPLINE_FK FROM RR_CATEGORY_DISCIPLINES WHERE D_CATEGORY_FK=?");
                ps.setLong(1, disciplineCategory.getId());
                ResultSet subRs = ps.executeQuery();
                ps.close();
                while (subRs.next()) {
                    if (disciplineCategory.getDisciplines() == null) {
                        disciplineCategory.setDisciplines(new ArrayList<Discipline>());
                    }
                    disciplineCategory.getDisciplines().add(disciplineDao.getById(subRs.getLong("DISCIPLINE_FK")));
                }
                subRs.close();
                disciplineCategory.setLanguageMutations(LANGUAGE_MUTATIONS_DAO.getMutationsForKey(id, RowingRaceCodeTables.CT_DISCIPLINE_CATEGORY));
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return disciplineCategory;

        }


        public List<DisciplineCategory> getAll() throws SQLException, ConnectivityException {
            List<DisciplineCategory> disciplineCategories = new ArrayList<DisciplineCategory>();
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT ACRONYM,ID FROM RR_DISCIPLINE_CATEGORY");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DisciplineCategory d = new DisciplineCategory();
                d.setId(rs.getLong("ID"));
                d.setAcronym(rs.getString("ACRONYM"));

                ps = connection.prepareStatement("SELECT DISCIPLINE_FK FROM RR_CATEGORY_DISCIPLINES WHERE D_CATEGORY_FK=?");
                ps.setLong(1, d.getId());
                ResultSet subRs = ps.executeQuery();
                while (subRs.next()) {
                    if (d.getDisciplines() == null) {
                        d.setDisciplines(new ArrayList<Discipline>());
                    }
                    d.getDisciplines().add(DISCIPLINE_DAO.getById(subRs.getLong("DISCIPLINE_FK")));
                }
                d.setLanguageMutations(LANGUAGE_MUTATIONS_DAO.getMutationsForKey(d.getId(), RowingRaceCodeTables.CT_DISCIPLINE_CATEGORY));
                disciplineCategories.add(d);
                ps.close();
                subRs.close();
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return disciplineCategories;
        }


        public void saveOrUpdate(DisciplineCategory disciplineCategory)
                throws SQLException, ConnectivityException {
            Connection connection = DbConnection.getConnection();
            if (disciplineCategory.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_DISCIPLINE_CATEGORY SET ACRONYM=? WHERE ID=?");
                ps.setString(1, disciplineCategory.getAcronym());
                ps.setLong(2, disciplineCategory.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_DISCIPLINE_CATEGORY (ACRONYM) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, disciplineCategory.getAcronym());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    disciplineCategory.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }
            DbConnection.releaseConnection(connection);
        }

    }
}
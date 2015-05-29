package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.dao.LanguageMutationsDao;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.LanguageMutation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobek on 6/26/2014.
 */
public class LanguageMutationsDaoImpl {
    private static LanguageMutationsDao instance;

    private LanguageMutationsDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static LanguageMutationsDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements LanguageMutationsDao {
        private static final String TABLE_SUFFIX = "_MUTATIONS";
        private static final String TABLE_NAME = "RR_LANGUAGE_MUTATIONS";
        private static final CodeTableDao codeTableDao = CodeTableDaoImpl.getInstance();

        @Override
        public List<LanguageMutation> getMutationsForKey(Long id, RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();

            PreparedStatement ps = connection.prepareStatement("SELECT ID,VALUE,LANGUAGE FROM " + TABLE_NAME + " WHERE ID IN (SELECT MUTATION_FK FROM " + codeTable.getTableName() + TABLE_SUFFIX + " WHERE CODE_TABLE_FK=?)");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            List<LanguageMutation> mutations = new ArrayList<LanguageMutation>();
            while (rs.next()) {
                LanguageMutation languageMutation = new LanguageMutation();
                languageMutation.setId(rs.getLong("ID"));
                languageMutation.setValue(rs.getString("VALUE"));

                languageMutation.setLanguage(codeTableDao.findByAcronym(rs.getString("LANGUAGE"), RowingRaceCodeTables.CT_LANGUAGES, false));
                languageMutation.setCodeTable(codeTable);
                mutations.add(languageMutation);
            }
            rs.close();
            ps.close();

            DbConnection.releaseConnection(connection);
            return mutations;
        }

        @Override
        public void addMutationToKey(Long keyId, LanguageMutation mutation) throws SQLException {
            Connection connection = DbConnection.getConnection();

            PreparedStatement ps = connection.prepareStatement("INSERT INTO  " + TABLE_NAME + " (LANGUAGE,VALUE) VALUES (?,?)");
            ps.setString(1, mutation.getLanguage().getAcronym());
            ps.setString(2, mutation.getValue());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                mutation.setId(rs.getLong(1));
            }
            ps.close();
            rs.close();

            ps = connection.prepareStatement("INSERT INTO " + mutation.getCodeTable().getTableName() + TABLE_SUFFIX + " (CODE_TABLE_FK,MUTATION_FK) VALUES (?,?)");
            ps.setLong(1, keyId);
            ps.setLong(2, mutation.getId());
            ps.executeUpdate();
            ps.close();

            DbConnection.releaseConnection(connection);
        }

        @Override
        public void removeMutationFromKey(LanguageMutation mutation) throws SQLException {
            Connection connection = DbConnection.getConnection();

            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + mutation.getCodeTable().getTableName() + TABLE_SUFFIX + " WHERE MUTATION_FK=?");
            ps.setLong(1, mutation.getId());
            ps.executeUpdate();
            ps.close();

            ps = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE ID=?");
            ps.setLong(1, mutation.getId());
            ps.executeUpdate();
            ps.close();

            DbConnection.releaseConnection(connection);
        }

        @Override
        public void removeAllMutationsFromKey(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();

            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + codeTable.getTableName() + TABLE_SUFFIX + " WHERE CODE_TABLE_FK=?");
            ps.setLong(1, enumEntity.getId());
            ps.executeUpdate();
            ps.close();

            if (enumEntity.getLanguageMutations() != null) {
                String mutationKeys = "";
                int i = 0;
                for (LanguageMutation languageMutation : enumEntity.getLanguageMutations()) {
                    if (i == 0) {
                        mutationKeys += languageMutation.getId().intValue();
                    } else {
                        mutationKeys += "," + languageMutation.getId().intValue();
                    }
                }
                ps = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE ID IN (?)");
                ps.setString(1, mutationKeys);
                ps.executeUpdate();
                ps.close();

            }
            DbConnection.releaseConnection(connection);
        }

        @Override
        public void updateMutation(LanguageMutation mutation) throws SQLException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET LANGUAGE=?,VALUE=? WHERE ID=?");
            ps.setString(1, mutation.getLanguage().getAcronym());
            ps.setString(2, mutation.getValue());
            ps.setLong(3, mutation.getId());
            ps.executeUpdate();
            DbConnection.releaseConnection(connection);
        }
    }
}
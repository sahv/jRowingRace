package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.dao.LanguageMutationsDao;
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

public class CodeTableDaoImpl {
    private static CodeTableDao instance;

    private CodeTableDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static CodeTableDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements CodeTableDao {

        private static final String KEY_ATTRIBUTE = "ID";
        private static final String ACRONYM = "ACRONYM";
        private static final LanguageMutationsDao languageMutationsDao = LanguageMutationsDaoImpl.getInstance();


        public void saveOrUpdate(EnumEntity data, RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (data.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE " + codeTable.getTableName() + " SET ACRONYM=? WHERE " + KEY_ATTRIBUTE + "=?");
                ps.setString(1, data.getAcronym());
                ps.setLong(2, data.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + codeTable.getTableName() + " (ACRONYM) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, data.getAcronym());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    data.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }
            if (data.getLanguageMutations() != null && data.getLanguageMutations().size() > 0) {
                for (LanguageMutation languageMutation : data.getLanguageMutations()) {
                    languageMutationsDao.addMutationToKey(data.getId(), languageMutation);
                }
            }
            DbConnection.releaseConnection(connection);
        }


        public List<EnumEntity> getAll(RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            List<EnumEntity> data = null;
            PreparedStatement ps = connection.prepareStatement("SELECT " + KEY_ATTRIBUTE + ",ACRONYM FROM " + codeTable.getTableName());
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                if (data == null) data = new ArrayList<EnumEntity>();
                EnumEntity tc = new EnumEntitySO();
                tc.setAcronym(rs.getString("ACRONYM"));
                tc.setId(rs.getLong(KEY_ATTRIBUTE));
                tc.setLanguageMutations(languageMutationsDao.getMutationsForKey(tc.getId(), codeTable));
                data.add(tc);

            }
            ps.close();
            rs.close();

            DbConnection.releaseConnection(connection);
            return data;
        }


        public EnumEntity findById(Long id, RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            EnumEntity data = null;
            PreparedStatement ps = connection.prepareStatement("SELECT ACRONYM FROM " + codeTable.getTableName() + " WHERE " + KEY_ATTRIBUTE + "=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.first();
            if (rs.getString("ACRONYM") != null) {
                data = new EnumEntitySO();
                data.setId(id);
                data.setAcronym(rs.getString("ACRONYM"));
                data.setLanguageMutations(languageMutationsDao.getMutationsForKey(data.getId(), codeTable));
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return data;
        }

        @Override
        public EnumEntity findByAcronym(String acronym, RowingRaceCodeTables codeTable, boolean withMutations) throws SQLException {
            Connection connection = DbConnection.getConnection();
            EnumEntity data = null;
            PreparedStatement ps = connection.prepareStatement("SELECT ID FROM " + codeTable.getTableName() + " WHERE " + ACRONYM + "=?");
            ps.setString(1, acronym);
            ResultSet rs = ps.executeQuery();
            rs.first();
            if (rs.getString("ID") != null) {
                data = new EnumEntitySO();
                data.setId(rs.getLong("ID"));
                data.setAcronym(acronym);
                if (withMutations) {
                    data.setLanguageMutations(languageMutationsDao.getMutationsForKey(data.getId(), codeTable));
                }
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return data;
        }

        @Override
        public void deleteCodeTableValue(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            languageMutationsDao.removeAllMutationsFromKey(enumEntity, codeTable);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + codeTable.getTableName() + " WHERE ID = ?");
            ps.setLong(1, enumEntity.getId());
            ps.executeUpdate();
            ps.close();
            DbConnection.releaseConnection(connection);
        }

        @Override
        public List<EnumEntity> getSlaveValues(EnumEntity masterValue, RowingRaceCodeTables masterCodeTable, RowingRaceCodeTables slaveCodeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT SLAVE_CT_FK FROM RR_CT_SLAVE_VALUES WHERE MASTER_CT=? AND MASTER_CT_FK=? AND SLAVE_CT=?");
            ps.setString(1, masterCodeTable.getTableName());
            ps.setLong(2, masterValue.getId());
            ps.setString(3, slaveCodeTable.getTableName());
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            List<EnumEntity> returnValues = new ArrayList<>();
            while (rs.next()) {
                returnValues.add(findById(rs.getLong("SLAVE_CT_FK"), slaveCodeTable));
            }
            rs.close();
            ps.close();
            DbConnection.releaseConnection(connection);
            return returnValues;
        }

        private void saveSlaveValue(Long masterValueId, RowingRaceCodeTables masterCodeTable, EnumEntity slaveValue, RowingRaceCodeTables slaveCodeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_CT_SLAVE_VALUES (MASTER_CT,MASTER_CT_FK,SLAVE_CT,SLAVE_CT_FK) VALUES (?,?,?,?)");
            ps.setString(1, masterCodeTable.getTableName());
            ps.setLong(2, masterValueId);
            ps.setString(3, slaveCodeTable.getTableName());
            ps.setLong(4, slaveValue.getId());
            ps.executeUpdate();
            ps.close();
            DbConnection.releaseConnection(connection);
        }

        private void deleteSlaveValues(Long masterValueId, RowingRaceCodeTables masterCodeTable, RowingRaceCodeTables slaveCodeTable) throws SQLException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM RR_CT_SLAVE_VALUES WHERE MASTER_CT=? AND MASTER_CT_FK=? AND SLAVE_CT=?");
            ps.setString(1, masterCodeTable.getTableName());
            ps.setLong(2, masterValueId);
            ps.setString(3, slaveCodeTable.getTableName());
            ps.executeUpdate();
            ps.close();
            DbConnection.releaseConnection(connection);
        }

        @Override
        public void saveSlaveValues(Long masterValueId, RowingRaceCodeTables masterCodeTable, List<EnumEntity> slaveValues, RowingRaceCodeTables slaveCodeTable) throws SQLException {
            deleteSlaveValues(masterValueId, masterCodeTable, slaveCodeTable);
            for (EnumEntity slaveValue : slaveValues) {
                saveSlaveValue(masterValueId, masterCodeTable, slaveValue, slaveCodeTable);
            }
        }
    }
}

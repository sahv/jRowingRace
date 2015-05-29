package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.AddressDao;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.so.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddressDaoImpl {

    private static AddressDao instance;

    private AddressDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static AddressDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements AddressDao {
        private static final CodeTableDao codeTableDao = CodeTableDaoImpl.getInstance();
        public void saveOrUpdate(Address address) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (address.getId() != null) {

                PreparedStatement ps = connection.prepareStatement("UPDATE RR_ADDRESS SET STREET=?,CITY=?,ZIP=?,STATE=?,COUNTRY_FK=? WHERE ADDRESS_ID=?");
                ps.setString(1, address.getStreet());
                ps.setString(2, address.getCity());
                ps.setString(3, address.getZip());
                ps.setString(4, address.getState());
                ps.setLong(5, address.getCountry().getId());
                ps.setLong(6, address.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_ADDRESS (STREET,CITY,ZIP,STATE,COUNTRY_FK) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, address.getStreet());
                ps.setString(2, address.getCity());
                ps.setString(3, address.getZip());
                ps.setString(4, address.getState());
                ps.setLong(5, address.getCountry().getId());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    address.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }
            DbConnection.releaseConnection(connection);
        }

        public Address getById(Long id) throws SQLException {
            Connection connection = DbConnection.getConnection();
            Address address = getById(id, connection);
            DbConnection.releaseConnection(connection);
            return address;
        }

        @Override
        public Address getById(Long id, Connection connection) throws SQLException {
            Address address = null;
            PreparedStatement ps = connection.prepareStatement("SELECT STREET,CITY,ZIP,STATE,COUNTRY_FK FROM RR_ADDRESS WHERE ADDRESS_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                address = new Address();
                address.setId(id);
                address.setStreet(rs.getString("STREET"));
                address.setCity(rs.getString("CITY"));
                address.setZip(rs.getString("ZIP"));
                address.setState(rs.getString("STATE"));
                if (rs.getLong("COUNTRY_FK") > 0) {
                    address.setCountry(codeTableDao.findById(rs.getLong("COUNTRY_FK"), RowingRaceCodeTables.CT_COUNTRIES));
                }
            }
            return address;
        }
    }
}

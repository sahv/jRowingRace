package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.AddressDao;
import sk.insomnia.rowingRace.dao.RaceCategoryDao;
import sk.insomnia.rowingRace.dao.SchoolDao;
import sk.insomnia.rowingRace.dao.TeamDao;
import sk.insomnia.rowingRace.so.Address;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.rowingRace.so.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SchoolDaoImpl {
    private static SchoolDao instance;

    private SchoolDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static SchoolDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements SchoolDao {

        public static final String TB_CATEGORIES = "RR_SCHOOL_RACE_CATEGORIES";
        private static final AddressDao aDao = AddressDaoImpl.getInstance();
        private static final TeamDao teamDao = TeamDaoImpl.getInstance();
        private static final RaceCategoryDao raceCategoryDao = RaceCategoryDaoImpl.getInstance();

        public School findById(Long id) throws SQLException {
            School school = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT NAME,SCHOOL_KEY,ADDRESS_FK FROM RR_SCHOOL WHERE SCHOOL_ID=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.getString("NAME") != null) {
                school = new School();
                school.setId(id);
                school.setName(rs.getString("NAME"));
                school.setKey(rs.getString("SCHOOL_KEY"));
                if (rs.getLong("ADDRESS_FK") > 0) {
                    school.setAddress(aDao.getById(rs.getLong("ADDRESS_FK")));
                }
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return school;
        }

        public void delete(School school) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (school.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_SCHOOL SET DELETED=1 WHERE SCHOOL_ID=?");
                ps.setLong(1, school.getId());
                ps.executeUpdate();
                ps.close();
            }
            DbConnection.releaseConnection(connection);
        }

        public void saveSchool(School school) throws SQLException {
            Connection connection = DbConnection.getConnection();
            if (school.getAddress() != null) {
                aDao.saveOrUpdate(school.getAddress());
            }
            if (school.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_SCHOOL SET NAME=?,SCHOOL_KEY=?,ADDRESS_FK=? WHERE SCHOOL_ID=?");
                ps.setString(1, school.getName());
                ps.setString(2, school.getKey());
                if (school.getAddress() != null) {
                    ps.setLong(3, school.getAddress().getId());
                }
                ps.setLong(4, school.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_SCHOOL (NAME,SCHOOL_KEY,ADDRESS_FK) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, school.getName());
                ps.setString(2, school.getKey());
                if (school.getAddress() != null) {
                    ps.setLong(3, school.getAddress().getId());
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    school.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }

            saveSchoolCategories(school, connection);
            DbConnection.releaseConnection(connection);
        }

        private void saveSchoolCategories(School school, Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM " + TB_CATEGORIES + " WHERE SCHOOL_FK=?");
            ps.setLong(1, school.getId());
            ps.executeUpdate();
            ps = connection.prepareStatement("INSERT INTO " + TB_CATEGORIES + " (SCHOOL_FK, CATEGORY_FK) VALUES (?,?)");
            for (EnumEntity raceCategory : school.getRaceCategories()) {
                ps.setLong(1, school.getId());
                ps.setLong(2, raceCategory.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        }

        public Integer getSchoolCode() throws SQLException {
            Connection connection = DbConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(SCHOOL_KEY) FROM RR_SCHOOL_KEY");
            Integer schoolCode = null;
            if (resultSet.next()) {
                schoolCode = resultSet.getInt(1);
                schoolCode++;
            }
            statement.close();
            resultSet.close();

            PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_SCHOOL_KEY (SCHOOL_KEY) VALUES (?)");
            ps.setInt(1, schoolCode);
            ps.executeUpdate();
            ps.close();
            DbConnection.releaseConnection(connection);
            return schoolCode;
        }

        @Override
        public List<School> getSchools() throws SQLException {
            List<School> schools = new ArrayList<>();
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT SCHOOL_KEY, NAME,SCHOOL_ID,A.ADDRESS_ID,A.STREET,A.CITY,A.ZIP,A.STATE,C.ID AS C_ID, C.ACRONYM C_ACRONYM FROM RR_SCHOOL S" +
                    " JOIN RR_ADDRESS A ON S.ADDRESS_FK = A.ADDRESS_ID" +
                    " JOIN RR_COUNTRY C ON C.id = A.COUNTRY_FK");
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                School school = new School();
                school.setId(rs.getLong("SCHOOL_ID"));
                school.setName(rs.getString("NAME"));
                school.setKey(rs.getString("SCHOOL_KEY"));
                Address address = new Address();
                EnumEntity country = new EnumEntitySO();
                country.setAcronym(rs.getString("C_ACRONYM"));
                country.setId(rs.getLong("C_ID"));
                address.setCountry(country);

                address.setState(rs.getString("STREET"));
                address.setZip(rs.getString("ZIP"));
                address.setId(rs.getLong("ADDRESS_ID"));
                address.setCity(rs.getString("CITY"));
                address.setStreet(rs.getString("STREET"));
                school.setAddress(address);
                school.setRaceCategories(raceCategoryDao.findBySchoolId(school.getId(), connection));
//                school.setTeams(teamDao.findBySchoolId(school.getId(), connection));
                schools.add(school);
            }
            ps.close();
            rs.close();

            DbConnection.releaseConnection(connection);
            return schools;
        }


        public School findByCode(String code) throws SQLException {
            School school = null;
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT NAME,SCHOOL_ID,ADDRESS_FK,SCHOOL_KEY FROM RR_SCHOOL");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                school = new School();
                school.setId(rs.getLong("SCHOOL_ID"));
                school.setName(rs.getString("NAME"));
                school.setKey(code);
                if (rs.getLong("ADDRESS_FK") > 0) {
                    school.setAddress(aDao.getById(rs.getLong("ADDRESS_FK"), connection));
                }
            }
            ps.close();
            rs.close();

            ps = connection.prepareStatement("SELECT TEAM_FK FROM RR_SCHOOL_TEAMS ST, RR_TEAM T WHERE SCHOOL_FK=? AND ST.TEAM_FK=T.TEAM_ID AND T.DELETED=0");
            ps.setLong(1, school.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                if (school.getTeams() == null) {
                    school.setTeams(new ArrayList<Team>());
                }
                school.getTeams().add(teamDao.findById(rs.getLong("TEAM_FK")));
            }

            ps = connection.prepareStatement("SELECT RACE_CATEGORY_FK FROM RR_SCHOOL_RACE_CATEGORIES WHERE SCHOOL_FK=?");
            ps.setLong(1, school.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                school.getRaceCategories().add(raceCategoryDao.findById(rs.getLong("RACE_CATEGORY_FK")));
            }
            DbConnection.releaseConnection(connection);
            return school;
        }

    }
}

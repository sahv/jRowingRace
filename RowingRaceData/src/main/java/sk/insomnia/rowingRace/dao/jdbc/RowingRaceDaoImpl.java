package sk.insomnia.rowingRace.dao.jdbc;

import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.RaceCategoryDao;
import sk.insomnia.rowingRace.dao.RaceRoundDao;
import sk.insomnia.rowingRace.dao.RowingRaceDao;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.RowingRace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowingRaceDaoImpl {

    private static RowingRaceDao instance;

    private RowingRaceDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance method.");
    }

    public static RowingRaceDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements RowingRaceDao {
        private static final RaceCategoryDao raceCategoryDao = RaceCategoryDaoImpl.getInstance();
        private static final RaceRoundDao raceRoundDao = RaceRoundDaoImpl.getInstance();

        public RowingRace getRowingRace(List<EnumEntity> raceCategories) throws SQLException,
                ConnectivityException {
            if (raceCategories == null) {
                throw new IllegalStateException("Race categories must not be empty.");
            }
            RowingRace rowingRace = new RowingRace();
            Connection connection = DbConnection.getConnection();
            String categories = "";

            for (int i = 0; i < raceCategories.size(); i++) {
                EnumEntity enumEntitySO = raceCategories.get(i);
                if (i == 0) {
                    categories += enumEntitySO.getId().longValue();
                } else {
                    categories += "," + enumEntitySO.getId().longValue();
                }

            }
            PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT ID,YEAR,RACE_CATEGORY_FK,NUMBER_OF_ALTERNATES,MAX_RACERS FROM RR_RACE_YEAR WHERE RACE_CATEGORY_FK IN (?)");
            ps.setString(1, categories);

            ResultSet rs = ps.executeQuery();

            rs.beforeFirst();
            while (rs.next()) {
                if (rowingRace.getRaceYears() == null) {
                    rowingRace.setRaceYears(new ArrayList<RaceYear>());
                }
                RaceYear ry = new RaceYear();
                ry.setYear(rs.getString("YEAR"));
                ry.setId(rs.getLong("ID"));
                ry.setRaceCategory(raceCategoryDao.findById(rs.getLong("RACE_CATEGORY_FK")));
                ry.setNumberOfAlternates(rs.getInt("NUMBER_OF_ALTERNATES"));
                ry.setMaxRacers(rs.getInt("MAX_RACERS"));
                PreparedStatement ps2 = connection.prepareStatement("SELECT RACE_ROUND_ID FROM RR_RACE_ROUND WHERE RACE_YEAR_FK=? ORDER BY ROUND_NUMBER");
                ps2.setLong(1, ry.getId());
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    if (ry.getRounds() == null) {
                        ry.setRounds(new ArrayList<RaceRound>());
                    }
                    ry.getRounds().add(raceRoundDao.getById(rs2.getLong("RACE_ROUND_ID")));
                }
                rowingRace.getRaceYears().add(ry);
                ps2.close();
                rs2.close();
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return rowingRace;
        }

        @Override
        public void saveOrUpdate(RaceYear raceYear) throws SQLException, ConnectivityException {
            Connection connection = DbConnection.getConnection();
            if (raceYear.getId() != null) {
                PreparedStatement ps = connection.prepareStatement("UPDATE RR_RACE_YEAR SET YEAR = ?, RACE_CATEGORY_FK = ?, NUMBER_OF_ALTERNATES=?, MAX_RACERS=? WHERE ID = ?");
                ps.setString(1, raceYear.getYear());
                ps.setLong(2, raceYear.getRaceCategory().getId());
                ps.setInt(3, raceYear.getNumberOfAlternates());
                ps.setInt(4, raceYear.getMaxRacers());
                ps.setLong(5, raceYear.getId());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO RR_RACE_YEAR (YEAR,RACE_CATEGORY_FK,NUMBER_OF_ALTERNATES,MAX_RACERS) VALUES (?,?,?,?)");
                ps.setString(1, raceYear.getYear());
                ps.setLong(2, raceYear.getRaceCategory().getId());
                ps.setInt(3, raceYear.getNumberOfAlternates());
                ps.setInt(4, raceYear.getMaxRacers());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.beforeFirst();
                if (rs.next()) {
                    raceYear.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
            }
            DbConnection.releaseConnection(connection);
        }


        public void deleteRaceYear(RaceYear year) throws SQLException,
                ConnectivityException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM RR_RACE_ROUND WHERE RACE_YEAR_FK = ?");
            ps.setLong(1, year.getId());
            ps.executeUpdate();
            ps.close();

            ps = connection.prepareStatement("DELETE FROM RR_RACE_YEAR WHERE ID = ?");
            ps.setLong(1, year.getId());
            ps.executeUpdate();
            ps.close();

            DbConnection.releaseConnection(connection);
        }

        @Override
        public RowingRace getRowingRace() throws SQLException, ConnectivityException {
            Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT DISTINCT ID,YEAR,RACE_CATEGORY_FK,NUMBER_OF_ALTERNATES,MAX_RACERS FROM RR_RACE_YEAR");
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            RowingRace rowingRace = new RowingRace();
            while (rs.next()) {
                if (rowingRace.getRaceYears() == null) {
                    rowingRace.setRaceYears(new ArrayList<RaceYear>());
                }
                RaceYear ry = new RaceYear();
                ry.setYear(rs.getString("YEAR"));
                ry.setId(rs.getLong("ID"));
                ry.setRaceCategory(raceCategoryDao.findById(rs.getLong("RACE_CATEGORY_FK")));
                ry.setMaxRacers(rs.getInt("MAX_RACERS"));
                ry.setNumberOfAlternates(rs.getInt("NUMBER_OF_ALTERNATES"));
                PreparedStatement ps2 = connection.prepareStatement("SELECT RACE_ROUND_ID FROM RR_RACE_ROUND WHERE RACE_YEAR_FK=? ORDER BY ROUND_NUMBER");
                ps2.setLong(1, ry.getId());
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    if (ry.getRounds() == null) {
                        ry.setRounds(new ArrayList<RaceRound>());
                    }
                    ry.getRounds().add(raceRoundDao.getById(rs2.getLong("RACE_ROUND_ID")));
                }
                rowingRace.getRaceYears().add(ry);
                ps2.close();
                rs2.close();
            }
            ps.close();
            rs.close();
            DbConnection.releaseConnection(connection);
            return rowingRace;
        }
    }
}

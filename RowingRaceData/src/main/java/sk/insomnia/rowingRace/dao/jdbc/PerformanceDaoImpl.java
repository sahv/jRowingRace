package sk.insomnia.rowingRace.dao.jdbc;

import org.apache.log4j.Logger;
import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.dao.PerformanceDao;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.PerformanceParameter;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDaoImpl {
    private static PerformanceDao instance;

    private PerformanceDaoImpl() {
        throw new AssertionError("This class should be instantiated using getInstance() method.");
    }

    public static PerformanceDao getInstance() {
        if (instance == null) {
            instance = new DaoImpl();
        }
        return instance;
    }

    private static class DaoImpl implements PerformanceDao {

        private static final Logger logger = Logger.getLogger(PerformanceDaoImpl.class.toString());
        @Override
        public void saveOrUpdate(Performance performance) throws SQLException, ConnectivityException {
            saveOrUpdate(performance, false);
        }
        @Override
        public void saveOrUpdate(Performance performance, boolean force) throws SQLException, ConnectivityException {
            Connection connection = DbConnection.getConnection();
            // check whether if team has raced this round and if it has better time
            String dimension = "DIM_DISTANCE";
            String value = "FINAL_TIME";
            try {
                dimension = performance.getRaceRound().getDiscipline().getIntervals().get(0).getDimension().getAcronym();
            } catch (Exception e) {
                logger.debug(ExceptionUtils.exceptionAsString(e));
            }
            if (dimension.equals("DIM_DISTANCE")) {
                value = "FINAL_DISTANCE";
            }
            PreparedStatement ps = connection.prepareStatement("SELECT PERFORMANCE_ID,FINAL_TIME,FINAL_DISTANCE FROM RR_PERFORMANCE WHERE RACE_ROUND_FK=? AND TEAM_FK=?");
            ps.setLong(1, performance.getRaceRound().getId());
            ps.setLong(2, performance.getTeam().getId());
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            if (rs.next()) {
                if (value.equals("FINAL_TIME")) {
                    //when interval dimension is time, final distance in designed time is important
                    double dbValue = rs.getDouble("FINAL_DISTANCE");
                    if (force || (dbValue < performance.getFinalDistance())) {
                        performance.setId(rs.getLong("PERFORMANCE_ID"));
                    } else {
                        //do nothing
                        return;
                    }
                } else {
                    //when interval dimension is distance, final time in designed distance is important
                    double dbValue = rs.getDouble("FINAL_TIME");
                    if (force || (dbValue <=0 || (dbValue > performance.getFinalTime()))) {
                        performance.setId(rs.getLong("PERFORMANCE_ID"));
                    } else {
                        return;
                    }
                }
            }
            ps.close();
            rs.close();

            if (performance.getId() != null) {
                logger.debug("going to save performance");
                ps = connection.prepareStatement("UPDATE RR_PERFORMANCE SET TEAM_FK=?,FINAL_DISTANCE=?, CREATED_ON=?, FINAL_TIME=? WHERE PERFORMANCE_ID=?");
                ps.setLong(1, performance.getTeam().getId());
                ps.setLong(2, performance.getFinalDistance());
                ps.setDate(3, new java.sql.Date(performance.getDate().getTime()));
                ps.setDouble(4, performance.getFinalTime());
                ps.setLong(5, performance.getId());
                ps.executeUpdate();
                ps.close();
                logger.debug("performance saved");
            } else {
                logger.debug("going to save update performance");
                ps = connection.prepareStatement("INSERT INTO RR_PERFORMANCE (TEAM_FK,FINAL_DISTANCE, CREATED_ON, FINAL_TIME,RACE_ROUND_FK) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, performance.getTeam().getId());
                if (performance.getFinalDistance() != null) {
                    ps.setLong(2, performance.getFinalDistance());
                } else {
                    ps.setLong(2, 0);
                }
                ps.setDate(3, new java.sql.Date(performance.getDate().getTime()));
                ps.setDouble(4, performance.getFinalTime());
                ps.setLong(5, performance.getRaceRound().getId());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    performance.setId(rs.getLong(1));
                }
                ps.close();
                rs.close();
                logger.debug("performance updated");
            }

            logger.debug("going to save performance paramters (count:" + performance.getParameters().size() + ")");
            if (performance.getParameters().size() > 0) {
                logger.debug("going to delete performance parameters for performance_id=" + performance.getId());
                ps = connection.prepareStatement("DELETE FROM RR_PERFORMANCE_PARAMS WHERE PERFORMANCE_FK=?");
                ps.setLong(1, performance.getId());
                ps.executeUpdate();
                ps.close();
                logger.debug("performance parameters deleted");
            }

            for (int i = 0; i < performance.getParameters().size(); i++) {
                PerformanceParameter pp = performance.getParameters().get(i);
                ps = connection.prepareStatement("INSERT INTO RR_PERFORMANCE_PARAMS (PERFORMANCE_FK,IMMEDIATE_TIME,IMMEDIATE_SPEED,IMMEDIATE_CADENCE, ORDER_NO,RACER_FK) VALUES (?,?,?,?,?,?)");
                ps.setLong(1, performance.getId());
                ps.setDouble(2, pp.getTime());
                ps.setDouble(3, pp.getSpeed());
                ps.setInt(4, pp.getCadence());
                ps.setInt(5, i);
                ps.setLong(6, pp.getRacedBy().getId());
                ps.executeUpdate();
                ps.close();
                logger.debug("save performance parameter #" + i);
            }
            logger.debug("performance paramters saved");
            DbConnection.releaseConnection(connection);
        }

        @Override
        public List<PerformanceDto> getAllPerformancesForRaceYearAndRound(Long raceYearId, Long raceRoundId) throws SQLException {
            String sql = "SELECT DISTINCT T.TEAM_ID,  T.NAME AS TEAM_NAME, S.NAME AS ORGANIZATION_NAME FROM RR_TEAM T" +
                    " JOIN RR_SCHOOL_TEAMS ST ON ST.TEAM_FK = T.TEAM_ID" +
                    " JOIN RR_SCHOOL S ON S.SCHOOL_ID = ST.SCHOOL_FK" +
                    " JOIN RR_SCHOOL_RACE_CATEGORIES SRC ON SRC.SCHOOL_FK = ST.SCHOOL_FK" +
                    " JOIN RR_RACE_CATEGORY RC ON RC.ID = SRC.CATEGORY_FK" +
                    " JOIN RR_RACE_YEAR RY ON RY.RACE_CATEGORY_FK = SRC.CATEGORY_FK" +
                    " WHERE RY.ID = ? AND T.DELETED = 0" +
                    " ORDER BY T.NAME, S.NAME";

            String sql2 = "SELECT P.PERFORMANCE_ID, P.FINAL_DISTANCE, P.FINAL_TIME, P.CREATED_ON FROM RR_PERFORMANCE P WHERE P.RACE_ROUND_FK = ? AND P.TEAM_FK = ? ORDER BY FINAL_TIME ASC";

            List<PerformanceDto> performances = new ArrayList<>();
            final Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, raceYearId);
            ResultSet rs = ps.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                PerformanceDto performanceDto = new PerformanceDto();
                performanceDto.setTeamId(rs.getLong("TEAM_ID"));
                performanceDto.setTeamName(rs.getString("TEAM_NAME"));
                performanceDto.setOrganizationName(rs.getString("ORGANIZATION_NAME"));

                PreparedStatement ps2 = connection.prepareCall(sql2);
                ps2.setLong(1, raceRoundId);
                ps2.setLong(2, performanceDto.getTeamId());
                ResultSet resultSet = ps2.executeQuery();
                resultSet.beforeFirst();
                if (resultSet.next()) {
                    performanceDto.setFinalDistance(resultSet.getDouble("FINAL_DISTANCE"));
                    performanceDto.setFinalTime(resultSet.getDouble("FINAL_TIME"));
                    performanceDto.setPerformanceId(resultSet.getLong("PERFORMANCE_ID"));
                    performanceDto.setCreatedOn(resultSet.getDate("CREATED_ON"));
                }
                performanceDto.setRaceRoundId(raceRoundId);
                performances.add(performanceDto);
            }
            rs.close();
            ps.close();
            DbConnection.releaseConnection(connection);
            return performances;
        }

        @Override
        public void deletePerformance(Long performanceId) throws SQLException, ConnectivityException {
            final Connection connection = DbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM RR_PERFORMANCE WHERE PERFORMANCE_ID=?");
            ps.setLong(1, performanceId);
            ps.executeUpdate();
            ps.close();
            DbConnection.releaseConnection(connection);
        }
    }
}

package sk.insomnia.rowingRace.dao;

import java.sql.SQLException;
import java.util.List;

import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Performance;

public interface PerformanceDao {

    public void saveOrUpdate(Performance performance)  throws SQLException,ConnectivityException;
	public void saveOrUpdate(Performance performance, boolean force)  throws SQLException,ConnectivityException;
    public List<PerformanceDto> getAllPerformancesForRaceYearAndRound(Long raceYearId, Long raceRoundId) throws SQLException;
    public void deletePerformance(Long performanceId)  throws SQLException,ConnectivityException;
}

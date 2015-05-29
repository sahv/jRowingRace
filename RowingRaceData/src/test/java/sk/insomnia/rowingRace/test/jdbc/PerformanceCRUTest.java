package sk.insomnia.rowingRace.test.jdbc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sk.insomnia.rowingRace.dao.PerformanceDao;
import sk.insomnia.rowingRace.dao.jdbc.PerformanceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.RowingRaceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.SchoolDaoImpl;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataFileService;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.PerformanceParameter;
import sk.insomnia.rowingRace.so.Performances;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;

public class PerformanceCRUTest {

    RowingRaceFileFacade fileService;
	PerformanceDao dao;
	
	@Before
	public void setFileService(){
		fileService = new RowingRaceDataFileService();
		dao = PerformanceDaoImpl.getInstance();
	}
	public int fibonachi(int i){
		if (i <3 ){
			return 1;
		} else {
			return fibonachi(i - 1) + fibonachi(i - 2);
		}
	}
	
	@Test 
	public void testFibonachi(){
		System.out.println(fibonachi(11));
	}
	@Test
	@Ignore
	public void saveOrUpdatePerformance() throws SQLException, ConnectivityException{
		/*
		SchoolDaoImpl sDao = new SchoolDaoImpl();
		School school = sDao.findByCode("0000000066");
		assertNotNull(school);
		assertNotNull(school.getTeams());
		
		RowingRaceDaoImpl rDao = new RowingRaceDaoImpl();
		RowingRace rRace = rDao.getRowingRace();
		assertNotNull(rRace);
		assertNotNull(rRace.getRaceYears());
		assertNotNull(rRace.getRaceYears().get(0));
		assertNotNull(rRace.getRaceYears().get(0).getRounds());
		assertNotNull(rRace.getRaceYears().get(0).getRounds().get(0));
		
		Performance performance = new Performance();
		performance.setTeam(school.getTeams().get(0));
		performance.setRaceRound(rRace.getRaceYears().get(0).getRounds().get(0));
		performance.setFinalDistance(new Integer(1000));
		performance.setFinalTime(new Double(240));
		performance.setDate(new Date());
		
		performance.setParameters(new ArrayList<PerformanceParameter>());
		for (int i=0;i<100;i++){
			PerformanceParameter pp = new PerformanceParameter();
			pp.setCadence(new Integer(29));
			pp.setSpeed(new Double(100));
			pp.setTime(new Double(100+i));			
			performance.getParameters().add(pp);
		}
		*/
		Performances performance;
	
		try {
			performance = fileService.loadPerformance();
			
			assertNotNull(performance);
			assertNotNull(performance.getPerformances());
			assertNotNull(performance.getPerformances().get(0));
			
			dao.saveOrUpdate(performance.getPerformances().get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

    @Test
    public void testReadPerformancesByYearAndRound() throws SQLException {
        final Long raceYearId = new Long(6);
        final Long roundId = new Long(64);
        List<PerformanceDto> performanceDtoList = dao.getAllPerformancesForRaceYearAndRound(raceYearId, roundId);
        assertNotNull(performanceDtoList);
        assertTrue(performanceDtoList.size() > 0);
    }
}

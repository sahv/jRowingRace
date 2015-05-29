package sk.insomnia.rowingRace.test.file;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sk.insomnia.rowingRace.dao.jdbc.PerformanceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.RowingRaceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.SchoolDaoImpl;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataFileService;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.PerformanceParameter;
import sk.insomnia.rowingRace.so.Performances;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;

public class PerformanceFileCRUTest {


    RowingRaceFileFacade fileService;
	@Before
	public void setFileService(){
		fileService = new RowingRaceDataFileService();
	}
	@Test
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

		RowingRaceDataFileService fileService = new RowingRaceDataFileService();
		boolean noException = true;
		try {
			fileService.saveOrUpdate(performance);
		} catch (IOException e) {
			noException  = false;
		}
		
		assertTrue(noException);
		*/
		Performances performance = null;
		boolean noException  = true;

		try {
			performance = fileService.loadPerformance();
		} catch (IOException e) {
			noException  = false;
		}

		assertTrue(noException);

		assertNotNull(performance);
		
	}
}

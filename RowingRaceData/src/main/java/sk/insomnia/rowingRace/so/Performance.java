package sk.insomnia.rowingRace.so;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Performance implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(Performance.class.toString());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double finalTime;
	private Integer finalDistance;
	private Date date;
	private Team team;
	private Long id;
	private RaceRound raceRound;
	private List<PerformanceParameter> parameters;
	
	public Double getFinalTime() {
		return finalTime;
	}
	public void setFinalTime(Double finalTime) {
		this.finalTime = finalTime;
		logger.debug("final time set to :"+finalTime);
	}
	public Integer getFinalDistance() {
		return finalDistance;
	}
	public void setFinalDistance(Integer finalDistance) {
		logger.debug("final distance set to :"+finalDistance);
		this.finalDistance = finalDistance;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		logger.debug("team set to :"+team.getName());
		this.team = team;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RaceRound getRaceRound() {
		logger.debug("race round set to :"+raceRound.getDescription());
		return raceRound;
	}
	public void setRaceRound(RaceRound raceRound) {
		this.raceRound = raceRound;
	}
	public List<PerformanceParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<PerformanceParameter> parameters) {
		this.parameters = parameters;
	}
	
	
}

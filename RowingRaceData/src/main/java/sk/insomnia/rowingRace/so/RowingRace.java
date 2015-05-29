package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;

public class RowingRace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5141734367853545734L;
	private List<RaceYear> raceYears;
	
	public List<RaceYear> getRaceYears() {
		return raceYears;
	}
	public void setRaceYears(List<RaceYear> raceYears) {
		this.raceYears = raceYears;
	}
	
	
	
}

package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;

public class Performances implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3163489015529716619L;
	private List<Performance> performances;

	public List<Performance> getPerformances() {
		return performances;
	}

	public void setPerformances(List<Performance> performances) {
		this.performances = performances;
	}
	
	
}

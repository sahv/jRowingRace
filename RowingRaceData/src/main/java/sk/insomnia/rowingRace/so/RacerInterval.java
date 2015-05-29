package sk.insomnia.rowingRace.so;

import java.io.Serializable;

public class RacerInterval implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3323702883329984607L;
	private Racer racer;
	private Integer intervalNumber;
	private String time;
	private Long distance;
	private Long speed;

	
	public Racer getRacer() {
		return racer;
	}


	public void setRacer(Racer racer) {
		this.racer = racer;
	}


	public Integer getIntervalNumber() {
		return intervalNumber;
	}


	public void setIntervalNumber(Integer intervalNumber) {
		this.intervalNumber = intervalNumber;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public Long getDistance() {
		return distance;
	}


	public void setDistance(Long distance) {
		this.distance = distance;
	}


	public Long getSpeed() {
		return speed;
	}


	public void setSpeed(Long speed) {
		this.speed = speed;
	}


	public String getFullName(){
		return this.racer.getFullName();
	}
	

}

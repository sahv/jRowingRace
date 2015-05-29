package sk.insomnia.rowingRace.so;

import java.io.Serializable;

public class PerformanceParameter implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2006680027188791572L;
	private Long id;
	private Double time;
	private Double speed;
	private Integer  cadence;
	private Racer racedBy;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Double getTime() {
		return time;
	}
	public void setTime(Double time) {
		this.time = time;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	public Integer getCadence() {
		return cadence;
	}
	public void setCadence(Integer cadence) {
		this.cadence = cadence;
	}
	public Racer getRacedBy() {
		return racedBy;
	}
	public void setRacedBy(Racer racedBy) {
		this.racedBy = racedBy;
	}
	
	
	

}

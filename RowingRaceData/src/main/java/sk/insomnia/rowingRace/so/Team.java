package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;


public class Team implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5766677668454847572L;

	private String name;

	private Long id;
	
	private List<Racer> racers;
	private List<Performance> performances;
	private EnumEntity teamCategory;
    private Integer maxRacers;
    private Integer numberOfAlternates;

    public Integer getMaxRacers() {
        return maxRacers;
    }

    public void setMaxRacers(Integer maxRacers) {
        this.maxRacers = maxRacers;
    }

    public Integer getNumberOfAlternates() {
        return numberOfAlternates;
    }

    public void setNumberOfAlternates(Integer numberOfAlternates) {
        this.numberOfAlternates = numberOfAlternates;
    }

    public Team(){}
	
	public Team(String name){
		this.name = name;
	}

	public Team(Long id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String toString(){
		return this.name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Racer> getRacers() {
		return racers;
	}

	public void setRacers(List<Racer> racers) {
		this.racers = racers;
	}

	public List<Performance> getPerformances() {
		return performances;
	}

	public void setPerformances(List<Performance> performances) {
		this.performances = performances;
	}

	public EnumEntity getTeamCategory() {
		return teamCategory;
	}

	public void setTeamCategory(EnumEntity teamCategory) {
		this.teamCategory = teamCategory;
	}
	

}

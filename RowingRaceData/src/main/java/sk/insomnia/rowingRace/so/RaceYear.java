package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;

public class RaceYear implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private List<RaceRound> rounds;
    private String year;
    private EnumEntity raceCategory;
    private Integer maxRacers;
    private Integer numberOfAlternates;

    public RaceYear() {

    }

    public RaceYear(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RaceRound> getRounds() {
        return rounds;
    }

    public void setRounds(List<RaceRound> rounds) {
        this.rounds = rounds;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String toString() {
        return this.year.toString() + " " + raceCategory.getAcronym();
    }

    public EnumEntity getRaceCategory() {
        return raceCategory;
    }

    public void setRaceCategory(EnumEntity raceCategory) {
        this.raceCategory = raceCategory;
    }

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
}

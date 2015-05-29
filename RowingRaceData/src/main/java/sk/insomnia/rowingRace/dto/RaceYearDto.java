package sk.insomnia.rowingRace.dto;

import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobek on 7/2/2014.
 */
public class RaceYearDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private List<RaceRound> rounds;
    private String year;
    private EnumEntityDto raceCategory;
    private Integer maxRacers;
    private Integer numberOfAlternates;

    public RaceYearDto() {

    }

    public RaceYearDto(RaceYear that) {
        this.setId(that.getId());
        this.setRaceCategory(new SimpleEnumEntityDto(that.getRaceCategory()));
        this.setYear(that.getYear());
        this.setRounds(that.getRounds());
        this.setMaxRacers(that.getMaxRacers());
        this.setNumberOfAlternates(that.getNumberOfAlternates());
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

    public EnumEntityDto getRaceCategory() {
        return raceCategory;
    }

    public void setRaceCategory(EnumEntityDto raceCategory) {
        this.raceCategory = raceCategory;
    }

    public String toString() {
        return this.getYear() + " " + this.getRaceCategory().getValue();
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

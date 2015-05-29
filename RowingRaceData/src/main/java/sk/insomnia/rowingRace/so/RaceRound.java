package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.Date;

public class RaceRound implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5905241557924904220L;
    private Date begin = new Date();
    private Date end = new Date();
    private String description;
    private Integer roundNumber;
    private Discipline discipline;
    private RaceYear raceYear;
    private Long id;

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public String toString() {
        return this.roundNumber.toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RaceYear getRaceYear() {
        return raceYear;
    }

    public void setRaceYear(RaceYear raceYear) {
        this.raceYear = raceYear;
    }

    public String getDisciplineName() {
        return this.discipline.getName();
    }


}

package sk.insomnia.rowingRace.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by martin on 10/22/2014.
 */
public class PerformanceDto implements Serializable {

    private Long teamId;
    private Long performanceId;
    private Double finalDistance;
    private Double finalTime;
    private String teamName;
    private String organizationName;
    private Date createdOn;
    private Long raceRoundId;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public Double getFinalDistance() {
        return finalDistance;
    }

    public void setFinalDistance(Double finalDistance) {
        this.finalDistance = finalDistance;
    }

    public Double getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(Double finalTime) {
        this.finalTime = finalTime;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Long getRaceRoundId() {
        return raceRoundId;
    }

    public void setRaceRoundId(Long raceRoundId) {
        this.raceRoundId = raceRoundId;
    }
}

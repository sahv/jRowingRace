package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class School implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -612704562499783471L;
    private Long id;
    private String name;
    private String key;
    private List<Team> teams = new ArrayList<Team>();
    private Address address;
    private List<EnumEntity> raceCategories = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EnumEntity> getRaceCategories() {
        return raceCategories;
    }

    public void setRaceCategories(List<EnumEntity> raceCategories) {
        this.raceCategories = raceCategories;
    }
}

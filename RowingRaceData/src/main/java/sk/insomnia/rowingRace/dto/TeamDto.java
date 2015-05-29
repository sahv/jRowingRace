package sk.insomnia.rowingRace.dto;


public class TeamDto {

    private Long id;
    private String name;
    private EnumEntityDto category;
    private Integer maxRacers;
    private Integer numberOfAlternates;


    public TeamDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumEntityDto getCategory() {
        return category;
    }

    public void setCategory(EnumEntityDto category) {
        this.category = category;
    }

    public Integer getNumberOfAlternates() {
        return numberOfAlternates;
    }

    public void setNumberOfAlternates(Integer numberOfAlternates) {
        this.numberOfAlternates = numberOfAlternates;
    }

    public Integer getMaxRacers() {
        return maxRacers;
    }

    public void setMaxRacers(Integer maxRacers) {
        this.maxRacers = maxRacers;
    }
}

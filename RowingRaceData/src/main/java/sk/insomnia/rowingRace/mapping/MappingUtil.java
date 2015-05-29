package sk.insomnia.rowingRace.mapping;

import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.dto.TeamDto;
import sk.insomnia.rowingRace.so.DisciplineCategory;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.PerformanceParameter;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 8/24/2014.
 */
public final class MappingUtil {

    private MappingUtil() {
        throw new AssertionError("MappingUtil was not meant to be instantiated");
    }

    public static TeamDto toDto(Team team){
        TeamDto teamDto = new TeamDto();
        teamDto.setId(team.getId());
        teamDto.setName(team.getName());
        teamDto.setCategory(new SimpleEnumEntityDto(team.getTeamCategory()));
        teamDto.setNumberOfAlternates(team.getNumberOfAlternates());
        teamDto.setMaxRacers(team.getMaxRacers());
        return teamDto;
    }
    public static Team toSO(TeamDto teamDto){
        Team team = new Team();
        team.setId(teamDto.getId());
        team.setName(teamDto.getName());
        team.setTeamCategory(teamDto.getCategory());
        team.setNumberOfAlternates(teamDto.getNumberOfAlternates());
        team.setMaxRacers(teamDto.getMaxRacers());
        return team;
    }

    public static List<TeamDto> toDtoList(List<Team> teamList){
        List<TeamDto> teamDtos = new ArrayList<>();
        for(Team team:teamList){
            teamDtos.add(toDto(team));
        }
        return teamDtos;
    }
    public static Performance toSO(PerformanceDto performanceDto) {
        Performance performance = new Performance();
        performance.setFinalTime(performanceDto.getFinalTime());
        performance.setDate(performanceDto.getCreatedOn());
        performance.setId(performanceDto.getPerformanceId());
        if (performanceDto.getFinalDistance() != null) {
            performance.setFinalDistance(performanceDto.getFinalDistance().intValue());
        }
        performance.setParameters(new ArrayList<PerformanceParameter>());
        RaceRound rr = new RaceRound();
        rr.setId(performanceDto.getRaceRoundId());
        performance.setRaceRound(rr);
        Team team = new Team();
        team.setId(performanceDto.getTeamId());
        performance.setTeam(team);
        return performance;
    }

    public static DisciplineCategory toSO(DisciplineCategoryDto disciplineCategoryDto) {
        DisciplineCategory disciplineCategory = new DisciplineCategory();
        if (disciplineCategoryDto.getAcronym() != null) {
            disciplineCategory.setAcronym(disciplineCategoryDto.getAcronym());
        }

        if (disciplineCategoryDto.getLanguageMutations() != null) {
            disciplineCategory.setLanguageMutations(disciplineCategoryDto.getLanguageMutations());
        }


        return disciplineCategory;
    }

    public static RaceYear toSO(RaceYearDto that) {
        RaceYear _this = new RaceYear();
        _this.setId(that.getId());
        _this.setRaceCategory(that.getRaceCategory());
        _this.setYear(that.getYear());
        _this.setRounds(that.getRounds());
        _this.setMaxRacers(that.getMaxRacers());
        _this.setNumberOfAlternates(that.getNumberOfAlternates());
        return _this;
    }

    public static EnumEntity toSO(EnumEntityDto that) {
        EnumEntity _this = new EnumEntitySO();
        if (that.getLanguageMutations() != null) {
            _this.setLanguageMutations(that.getLanguageMutations());
        }
        if (that.getId() != null) {
            _this.setId(that.getId());
        }
        if (that.getAcronym() != null) {
            _this.setAcronym(that.getAcronym());
        }
        return _this;
    }

    public static DisciplineCategoryDto toDto(DisciplineCategory so) {
        DisciplineCategoryDto disciplineCategoryDto = new DisciplineCategoryDto();
        disciplineCategoryDto.setId(so.getId());
        disciplineCategoryDto.setDisciplines(so.getDisciplines());
        disciplineCategoryDto.setAcronym(so.getAcronym());
        disciplineCategoryDto.setLanguageMutations(so.getLanguageMutations());
        return disciplineCategoryDto;
    }

    public static List<DisciplineCategoryDto> disciplineCategorieAsDtoList(List<DisciplineCategory> categories) {
        List<DisciplineCategoryDto> disciplineCategoryDtos = new ArrayList<>();
        for (DisciplineCategory disciplineCategory : categories) {
            disciplineCategoryDtos.add(MappingUtil.toDto(disciplineCategory));
        }
        return disciplineCategoryDtos;
    }

    public static List<RaceYearDto> asDtoList(List<RaceYear> raceYears) {
        if (raceYears == null) {
            return null;
        }
        List<RaceYearDto> raceYearDtos = new ArrayList<>();
        for (RaceYear raceYear : raceYears) {
            raceYearDtos.add(new RaceYearDto(raceYear));
        }
        return raceYearDtos;
    }

}

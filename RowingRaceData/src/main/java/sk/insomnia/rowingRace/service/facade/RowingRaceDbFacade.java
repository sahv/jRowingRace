package sk.insomnia.rowingRace.service.facade;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.dto.TeamDto;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.Interval;
import sk.insomnia.rowingRace.so.LanguageMutation;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.Racer;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.rowingRace.so.Team;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public interface RowingRaceDbFacade {

    public void saveSchool(School school) throws SQLException, ConnectivityException;

    public School findByKey(String key) throws SQLException, ConnectivityException;

    public School findById(Long id) throws SQLException, ConnectivityException;

    public String getSchoolCode() throws SQLException, ConnectivityException;

    public List<School> getSchools() throws SQLException, ConnectivityException;

    public List<TeamDto> getTeamsBySchoolId(Long schoolId) throws SQLException, ConnectivityException;

    public void addTeamToSchool(Team team, Long schoolId) throws SQLException, ConnectivityException;

    public void saveTeam(Team team) throws SQLException, ConnectivityException;
    public void saveTeam(TeamDto team, Long schoolId) throws SQLException, ConnectivityException;

    public void addRacerToTeam(Racer racer, Long teamId) throws SQLException, ConnectivityException;

    public void saveRacer(Racer racer) throws SQLException, ConnectivityException;


    public void addRowingRaceRound(RaceRound raceRound) throws SQLException, ConnectivityException;

    public void deleteRowingRaceRound(RaceRound raceRound) throws SQLException, ConnectivityException;

    public void deleteRacer(Racer racer) throws SQLException, ConnectivityException;

    public void deleteTeam(Team team) throws SQLException, ConnectivityException;
    public void deleteTeam(Long teamId) throws SQLException, ConnectivityException;

    public void deleteSchool(School school) throws SQLException, ConnectivityException;


    public RowingRace loadRowingRace(List<EnumEntity> raceCategories) throws SQLException, ConnectivityException;

    public RowingRace loadRowingRace() throws SQLException, ConnectivityException;

    public void deleteRaceYear(RaceYear year) throws SQLException, ConnectivityException;

    public void saveOrUpdateRaceYear(RaceYear raceYear) throws SQLException, ConnectivityException;

    public List<DisciplineCategoryDto> loadDisciplineCategories() throws SQLException, ConnectivityException;

    public void saveDisciplineCategory(DisciplineCategoryDto disciplineCategory) throws SQLException, ConnectivityException;

    public void addIntervalToDiscipline(Interval interval, Long disciplineId) throws SQLException, ConnectivityException;

    public void addDisciplineToCategory(Discipline discipline, Long disciplineCategoryId) throws SQLException, ConnectivityException;

    public void saveInterval(Interval interval) throws SQLException, ConnectivityException;

    public void saveDiscipline(Discipline discipline) throws SQLException, ConnectivityException;


    public boolean isConnectivity();

    public void saveOrUpdate(Performance performance) throws SQLException, ConnectivityException;
    public void saveOrUpdate(Performance performance, boolean force) throws SQLException, ConnectivityException;

    public List<PerformanceDto> getAllPerformancesForRaceYearAndRound(Long raceYearId, Long raceRoundId) throws SQLException;

    public List<EnumEntityDto> getCodeTable(RowingRaceCodeTables codeTable, Locale locale) throws SQLException, ConnectivityException, DtoUtils.DtoUtilException;

//    @Deprecated
//    public List<EnumEntity> getCodeTableValues(RowingRaceCodeTables codeTable) throws SQLException, ConnectivityException;

    public void deleteCodeTableValue(EnumEntity EnumEntity, RowingRaceCodeTables codeTable) throws SQLException, ConnectivityException;

    public void saveCodeTableValue(EnumEntity EnumEntity, RowingRaceCodeTables codeTable) throws SQLException, ConnectivityException;

    public void addMutationToKey(Long id, LanguageMutation mutation) throws SQLException, ConnectivityException;

    public void deleteMutation(LanguageMutation mutation) throws SQLException, ConnectivityException;

    public void updateMutation(LanguageMutation mutation) throws SQLException, ConnectivityException;

    public List<EnumEntity> loadValuesForCodeTable(EnumEntityDto value, RowingRaceCodeTables masterCodeTable, RowingRaceCodeTables slaveCodeTable) throws SQLException;

    public void saveSlaveValues(Long masterValueId, RowingRaceCodeTables masterCodeTable, List<EnumEntityDto> slaveValues, RowingRaceCodeTables slaveCodeTable) throws SQLException;

    public void deletePerformance(PerformanceDto selectedItem) throws SQLException, ConnectivityException;

}

package sk.insomnia.rowingRace.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dao.CodeTableDao;
import sk.insomnia.rowingRace.dao.DisciplineCategoryDao;
import sk.insomnia.rowingRace.dao.DisciplineDao;
import sk.insomnia.rowingRace.dao.IntervalDao;
import sk.insomnia.rowingRace.dao.LanguageMutationsDao;
import sk.insomnia.rowingRace.dao.PerformanceDao;
import sk.insomnia.rowingRace.dao.RaceRoundDao;
import sk.insomnia.rowingRace.dao.RacerDao;
import sk.insomnia.rowingRace.dao.RowingRaceDao;
import sk.insomnia.rowingRace.dao.SchoolDao;
import sk.insomnia.rowingRace.dao.TeamDao;
import sk.insomnia.rowingRace.dao.jdbc.CodeTableDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.DisciplineCategoryDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.DisciplineDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.IntervalDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.LanguageMutationsDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.PerformanceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.RaceRoundDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.RacerDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.RowingRaceDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.SchoolDaoImpl;
import sk.insomnia.rowingRace.dao.jdbc.TeamDaoImpl;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dataStore.NoDataForKeyException;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.dto.TeamDto;
import sk.insomnia.rowingRace.mapping.MappingUtil;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.DisciplineCategory;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RowingRaceDataDbService implements RowingRaceDbFacade {

    private final ResourceBundle bundle;
    private static final LanguageMutationsDao languageMutationsDao = LanguageMutationsDaoImpl.getInstance();
    private static final RowingRaceDao rowingRaceDao = RowingRaceDaoImpl.getInstance();
    private static final SchoolDao schoolDao = SchoolDaoImpl.getInstance();
    private static final PerformanceDao performanceDao = PerformanceDaoImpl.getInstance();
    private static final CodeTableDao codeTableDao = CodeTableDaoImpl.getInstance();
    private static final DisciplineCategoryDao disciplineCategoryDao = DisciplineCategoryDaoImpl.getInstance();
    private static final RaceRoundDao raceRoundDao = RaceRoundDaoImpl.getInstance();
    private static final RacerDao racerDao = RacerDaoImpl.getInstance();
    private static final TeamDao teamDao = TeamDaoImpl.getInstance();
    private static final IntervalDao intervalDao = IntervalDaoImpl.getInstance();
    private static final DisciplineDao disciplineDao = DisciplineDaoImpl.getInstance();


    private static final Logger LOG = LoggerFactory.getLogger(RowingRaceDataDbService.class);


    public RowingRaceDataDbService(Locale locale) {
        this.bundle = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.data", locale);
    }

    public void saveSchool(School school) throws SQLException, ConnectivityException {
        schoolDao.saveSchool(school);
    }

    public String getSchoolCode() throws SQLException, ConnectivityException {
        String retVal = null;
        DecimalFormat phoneDecimalFmt = new DecimalFormat("0000000000");
        retVal = phoneDecimalFmt.format(schoolDao.getSchoolCode());
        return retVal;
    }

    @Override
    public List<School> getSchools() throws SQLException, ConnectivityException {
        return this.schoolDao.getSchools();
    }

    @Override
    public List<TeamDto> getTeamsBySchoolId(Long schoolId) throws SQLException, ConnectivityException {


        return MappingUtil.toDtoList(teamDao.findBySchoolId(schoolId));
    }


    public RowingRace loadRowingRace(List<EnumEntity> raceCategories) throws SQLException, ConnectivityException {
        RowingRace rowingRace = null;
        try {
            rowingRace = (RowingRace) CommonDataStore.getInstanceOfClass(RowingRace.class);
        } catch (NoDataForKeyException e) {
            LOG.info("Race not found in data store, will read from DB.");
            rowingRace = rowingRaceDao.getRowingRace(raceCategories);
            CommonDataStore.registerInstance(RowingRace.class, rowingRace);
        }
        return rowingRace;
    }

    public RowingRace loadRowingRace() throws SQLException, ConnectivityException {
        RowingRace rowingRace = null;
        try {
            rowingRace = (RowingRace) CommonDataStore.getInstanceOfClass(RowingRace.class);
        } catch (NoDataForKeyException e) {
            LOG.info("Race not found in data store, will read from DB.");
            rowingRace = rowingRaceDao.getRowingRace();
            CommonDataStore.registerInstance(RowingRace.class, rowingRace);
        }
        return rowingRace;
    }


    public School findByKey(String key) throws SQLException,
            ConnectivityException {
        return schoolDao.findByCode(key);
    }


    public School findById(Long id) throws SQLException, ConnectivityException {
        return schoolDao.findById(id);
    }


    public boolean isConnectivity() {
        return DbConnection.isConnectivity();
    }


    public void saveOrUpdate(Performance performance, boolean force) throws SQLException,
            ConnectivityException {
        performanceDao.saveOrUpdate(performance);
    }

    public void saveOrUpdate(Performance performance) throws SQLException,
            ConnectivityException {
        saveOrUpdate(performance, false);
    }

    @Override
    public List<PerformanceDto> getAllPerformancesForRaceYearAndRound(Long raceYearId, Long raceRoundId) throws SQLException {
        return this.performanceDao.getAllPerformancesForRaceYearAndRound(raceYearId, raceRoundId);
    }


    public List<EnumEntityDto> getCodeTable(RowingRaceCodeTables codeTable, Locale locale) throws SQLException,
            ConnectivityException, DtoUtils.DtoUtilException {
        List<EnumEntityDto> codeTables;
        try {
            codeTables = CommonDataStore.getValuesForCodeTable(codeTable);
        } catch (NoDataForKeyException e) {
            LOG.info(String.format("No values for code table found for %s in CommonDatastore, going to read from DB.", codeTable));
            List<EnumEntity> tcs = codeTableDao.getAll(codeTable);
            codeTables = DtoUtils.listOfLanguageSpecificValues(tcs, locale.getLanguage());
            CommonDataStore.registerValuesForCodeTable(codeTable, codeTables);
        }
        return codeTables;
    }

/*
    @Deprecated
    public List<EnumEntity> getCodeTableValues(RowingRaceCodeTables codeTable) throws SQLException,
            ConnectivityException {
        return codeTableDao.getAll(codeTable);
    }
*/

    @Override
    public void deleteCodeTableValue(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException, ConnectivityException {
        codeTableDao.deleteCodeTableValue(enumEntity, codeTable);
    }

    @Override
    public void saveCodeTableValue(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException, ConnectivityException {
        codeTableDao.saveOrUpdate(enumEntity, codeTable);
    }

    @Override
    public void addMutationToKey(Long id, LanguageMutation mutation) throws SQLException, ConnectivityException {
        languageMutationsDao.addMutationToKey(id, mutation);
    }

    @Override
    public void deleteMutation(LanguageMutation mutation) throws SQLException, ConnectivityException {
        languageMutationsDao.removeMutationFromKey(mutation);
    }

    @Override
    public void updateMutation(LanguageMutation mutation) throws SQLException, ConnectivityException {
        languageMutationsDao.updateMutation(mutation);
    }

    @Override
    public List<EnumEntity> loadValuesForCodeTable(EnumEntityDto value, RowingRaceCodeTables masterCodeTable, RowingRaceCodeTables slaveCodeTable) throws SQLException {
        return codeTableDao.getSlaveValues(value, masterCodeTable, slaveCodeTable);
    }

    @Override
    public void saveSlaveValues(Long masterValueId, RowingRaceCodeTables masterCodeTable, List<EnumEntityDto> slaveValues, RowingRaceCodeTables slaveCodeTable) throws SQLException {
        codeTableDao.saveSlaveValues(masterValueId, masterCodeTable, SimpleEnumEntityDto.toSoList(slaveValues), slaveCodeTable);
    }

    @Override
    public void deletePerformance(PerformanceDto selectedItem) throws SQLException, ConnectivityException {
        performanceDao.deletePerformance(selectedItem.getPerformanceId());
    }

    public List<DisciplineCategoryDto> loadDisciplineCategories()
            throws SQLException, ConnectivityException {
        return MappingUtil.disciplineCategorieAsDtoList(disciplineCategoryDao.getAll());
    }

    public void saveDisciplineCategory(DisciplineCategoryDto disciplineCategory) throws SQLException, ConnectivityException {
        DisciplineCategory disciplineCategorySO = new DisciplineCategory();
        disciplineCategoryDao.saveOrUpdate(disciplineCategorySO);
    }

    public void saveOrUpdateRaceYear(RaceYear raceYear) throws SQLException,
            ConnectivityException {
        rowingRaceDao.saveOrUpdate(raceYear);
    }

    public void deleteRaceYear(RaceYear year) throws SQLException,
            ConnectivityException {
        rowingRaceDao.deleteRaceYear(year);
    }


    public void deleteRowingRaceRound(RaceRound raceRound) throws SQLException,
            ConnectivityException {
        raceRoundDao.delete(raceRound);
    }

    public void deleteRacer(Racer racer) throws SQLException,
            ConnectivityException {
        racerDao.deleteRacer(racer);
    }

    public void deleteTeam(Team team) throws SQLException,
            ConnectivityException {
        teamDao.delete(team);
    }

    @Override
    public void deleteTeam(Long teamId) throws SQLException, ConnectivityException {
        teamDao.deleteTeam(teamId);
    }

    public void deleteSchool(School school) throws SQLException,
            ConnectivityException {
        schoolDao.delete(school);
    }

    public void addRowingRaceRound(RaceRound raceRound) throws SQLException,
            ConnectivityException {
        raceRoundDao.saveOrUpdate(raceRound);
    }

    public void addTeamToSchool(Team team, Long schoolId) throws SQLException, ConnectivityException {
        teamDao.saveOrUpdate(team, schoolId);
    }

    public void addRacerToTeam(Racer racer, Long teamId) throws SQLException,
            ConnectivityException {
        racerDao.saveOrUpdate(racer, teamId);
    }

    public void saveTeam(Team team) throws SQLException, ConnectivityException {
        teamDao.saveOrUpdate(team, null);
    }

    @Override
    public void saveTeam(TeamDto team, Long schoolId) throws SQLException, ConnectivityException {
        teamDao.saveOrUpdate(MappingUtil.toSO(team), schoolId);
    }

    public void addIntervalToDiscipline(Interval interval, Long disciplineId)
            throws SQLException, ConnectivityException {
        intervalDao.saveOrUpdate(interval, disciplineId);

    }

    public void addDisciplineToCategory(Discipline discipline,
                                        Long disciplineCategoryId) throws SQLException,
            ConnectivityException {
        disciplineDao.saveOrUpdate(discipline, disciplineCategoryId);
    }

    public void saveInterval(Interval interval) throws SQLException,
            ConnectivityException {
        intervalDao.saveOrUpdate(interval, null);
    }

    public void saveDiscipline(Discipline discipline) throws SQLException,
            ConnectivityException {
        disciplineDao.saveOrUpdate(discipline, null);
    }

    public void saveRacer(Racer racer) throws SQLException,
            ConnectivityException {
        racerDao.saveOrUpdate(racer, null);
    }
}

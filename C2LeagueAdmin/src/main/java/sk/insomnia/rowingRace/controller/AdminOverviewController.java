package sk.insomnia.rowingRace.controller;

import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.application.AdminGui;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.markers.DisciplineCategory;
import sk.insomnia.rowingRace.markers.RaceCategory;
import sk.insomnia.rowingRace.markers.TeamCategory;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminOverviewController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminOverviewController.class);

    @FXML
    private PerformancesAdministrationController performancesAdministrationController;
    @FXML
    private CodeTablesController codeTablesController;
    @FXML
    private DisciplinesController disciplinesController;
    @FXML
    private RaceCalendarController raceCalendarController;
    @FXML
    private MasterSlaveCodeTableController masterSlaveCodeTableController;
    @FXML
    private OrganizationAdministrationController organizationAdministrationController;

    private static final List<AbstractController> registeredControllers = new ArrayList<>();

    private AdminGui rowingRaceGui;


    public AdminOverviewController() {

    }


    private RowingRaceDbFacade dbService;


    @FXML
    private void initialize() {
        this.registeredControllers.add(performancesAdministrationController);
        this.registeredControllers.add(codeTablesController);
        this.registeredControllers.add(disciplinesController);
        this.registeredControllers.add(raceCalendarController);
        this.registeredControllers.add(masterSlaveCodeTableController);
        this.registeredControllers.add(organizationAdministrationController);
    }


    @Override
    public void initLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void initResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void initializeFormData() {
        //TODO : initialize CommonDataStore here
        initializeDataStore();
        for (AbstractController controller:registeredControllers){
            controller.initLocale(locale);
            controller.initResourceBundle(resourceBundle);
            controller.setDbService(dbService);
        }
        disciplinesController.initializeFormData();
    }

    @FXML
    private void tabPerformanceSelected(){
        performancesAdministrationController.initializeFormData();
    }
    @FXML
    private void tabRaceCalendarSelected(){
        raceCalendarController.initializeFormData();
    }
    @FXML
    private void managedCodeTablesSelected(){
        masterSlaveCodeTableController.initializeFormData();
    }
    @FXML
    private void tabCodeTablesSelected(){
        codeTablesController.initializeFormData();
    }

    @FXML
    private void tabOrganizationsSelected(){
        organizationAdministrationController.initializeFormData();
    }
    private void initializeDataStore() {
        try {
            dbService.getCodeTable(RowingRaceCodeTables.CT_RACE_CATEGORY, this.locale);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            LOG.error(String.format("Can't register data for %s because of ", RowingRaceCodeTables.CT_RACE_CATEGORY, e));
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }

        try {
            dbService.getCodeTable(RowingRaceCodeTables.CT_TEAM_CATEGORIES,this.locale);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            LOG.error(String.format("Can't register data for %s because of ", RowingRaceCodeTables.CT_TEAM_CATEGORIES, e));
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }

        try {
            List<DisciplineCategoryDto> disciplineCategoryDtos = dbService.loadDisciplineCategories();
            disciplineCategoryDtos = DtoUtils.<DisciplineCategoryDto>listOfLanguageSpecificValuesForDtoClass(
                    disciplineCategoryDtos,
                    this.locale.getLanguage(),
                    DisciplineCategoryDto.class);
            CommonDataStore.registerValuesForClass(DisciplineCategory.class, disciplineCategoryDtos);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            LOG.error(String.format("Can't register data for %s because of ", RowingRaceCodeTables.CT_DISCIPLINE_CATEGORY, e));
            errorMessageBase(RowingRaceCodeTables.CT_DISCIPLINE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }
    }


    @Override
    public void setDbService(RowingRaceDbFacade dbService) {
        this.dbService = dbService;
    }

    public void setRowingRaceGui(AdminGui rowingRaceGui) {
        this.rowingRaceGui = rowingRaceGui;
    }


    public boolean isOkClicked() {
        boolean okClicked = false;
        return okClicked;
    }
}

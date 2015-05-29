package sk.insomnia.rowingRace.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.dto.TeamDto;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Address;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 10/29/2014.
 */
public class OrganizationAdministrationController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationAdministrationController.class);

    @FXML
    private TableView<TeamDto> tvTeams;
    @FXML
    private TableView<School> tvSchools;
    @FXML
    private TableColumn<TeamDto, String> tcTeamName;
    @FXML
    private TableColumn<TeamDto, String> tcTeamCategory;
    @FXML
    private TableColumn<School, String> tcRaceCategory;
    @FXML
    private TableColumn<School, String> tcOrganizationAddress;
    @FXML
    private TableColumn<School, String> tcOrganizationName;
    @FXML
    private TableColumn<School, String> tcOrganizationKey;
    @FXML
    private TextField tfOrganizationName;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfStreet;
    @FXML
    private TextField tfZipCode;
    @FXML
    private ComboBox<EnumEntityDto> cbCountry;
    @FXML
    private ComboBox<EnumEntityDto> cbRaceCategory;
    @FXML
    private ComboBox<RaceYearDto> cbRaceYear;
    @FXML
    private TextField tfTeamName;
    @FXML
    private ComboBox<EnumEntityDto> cbDisciplineCategory;

    private School selectedItem;
    private TeamDto selectedTeam;

    private static ObservableList<School> organizations = FXCollections.observableArrayList();
    private static ObservableList<TeamDto> teams = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tvSchools.setItems(organizations);
        tvTeams.setItems(teams);
        tcOrganizationKey.setCellValueFactory(new PropertyValueFactory<School, String>("key"));
        tcOrganizationName.setCellValueFactory(new PropertyValueFactory<School, String>("name"));
        tvSchools.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<School>() {
            @Override
            public void changed(ObservableValue<? extends School> observable,
                                School oldValue, School newValue) {
                showSchoolDetail(newValue);
            }
        });
        tvTeams.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TeamDto>() {
            @Override
            public void changed(ObservableValue<? extends TeamDto> observable,
                                TeamDto oldValue, TeamDto newValue) {
                showTeamDetail(newValue);
            }
        });

        tcOrganizationAddress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<School, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<School, String> schoolStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                retval.setValue(String.format("%s %s", schoolStringCellDataFeatures.getValue().getAddress().getCity(),
                        schoolStringCellDataFeatures.getValue().getAddress().getStreet()));
                return retval;
            }
        });
        tcRaceCategory.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<School, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<School, String> schoolStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                if (schoolStringCellDataFeatures.getValue().getRaceCategories() != null &&
                        schoolStringCellDataFeatures.getValue().getRaceCategories().size() > 0) {
                    retval.set(schoolStringCellDataFeatures.getValue().getRaceCategories().get(0).getAcronym());
                }
                return retval;
            }
        });

        tcTeamName.setCellValueFactory(new PropertyValueFactory<TeamDto, String>("name"));
        tcTeamCategory.setCellValueFactory(new PropertyValueFactory<TeamDto, String>("category"));
    }

    private void showTeamDetail(TeamDto team) {
        if (team == null) {
            return;
        }
        this.selectedTeam = team;
        tfTeamName.setText(selectedTeam.getName());
        try {
            cbDisciplineCategory.setValue(DtoUtils.transformWithLanguageDependentValue(selectedTeam.getCategory(), locale.getLanguage(), SimpleEnumEntityDto.class));
        } catch (DtoUtils.DtoUtilException e) {
            LOG.error("Can't transform team categories.", e);
        }
    }

    private void showSchoolDetail(School newValue) {
        if (newValue == null) {
            return;
        }
        tfOrganizationName.setText(newValue.getName());
        tfCity.setText(newValue.getAddress().getCity());
        tfStreet.setText(newValue.getAddress().getStreet());
        tfZipCode.setText(newValue.getAddress().getZip());
        cbCountry.getSelectionModel().select(DtoUtils.findBySo(cbCountry.getItems(), newValue.getAddress().getCountry()));
        if (newValue.getRaceCategories() != null && newValue.getRaceCategories().size() > 0) {
            cbRaceCategory.getSelectionModel().select(DtoUtils.findBySo(cbRaceCategory.getItems(), newValue.getRaceCategories().get(0)));
        }
        this.selectedItem = newValue;
    }

    @Override
    public void initializeFormData() {
        readOrganizationData();
        initializeCodeTableValues();
    }

    private void initializeCodeTableValues() {
        cbCountry.getItems().clear();
        try {
            cbCountry.getItems().addAll(dbService.getCodeTable(RowingRaceCodeTables.CT_COUNTRIES, this.locale));
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            errorMessageBase(RowingRaceCodeTables.CT_COUNTRIES, ERR_CODE_TABLE_DATA_LOAD);
            LOG.debug("Error loading countries data from database.", e);
        }
        cbRaceCategory.getItems().clear();
        try {
            cbRaceCategory.getItems().addAll(dbService.getCodeTable(RowingRaceCodeTables.CT_RACE_CATEGORY, this.locale));
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
            LOG.debug("Error loading race category data from database.", e);
        }

        cbRaceYear.getItems().clear();
        RowingRace rowingRace = null;
        try {
            rowingRace = dbService.loadRowingRace();
            if (rowingRace != null) {
                List<RaceYearDto> raceYearDtos = DtoUtils.raceYearsToDtos(rowingRace.getRaceYears(), this.locale.getLanguage());
                this.cbRaceYear.getItems().addAll(raceYearDtos);
            }
        } catch (ConnectivityException | SQLException e) {
            LOG.error("Error loading rowing race data.", e);
        }
    }

    private void initializeTeamCategories() {
        cbDisciplineCategory.getItems().clear();
        try {
            List<EnumEntityDto> teamCategories = DtoUtils.listOfLanguageSpecificValues(dbService.loadValuesForCodeTable(new SimpleEnumEntityDto(selectedItem.getRaceCategories().get(0)), RowingRaceCodeTables.CT_RACE_CATEGORY, RowingRaceCodeTables.CT_TEAM_CATEGORIES), this.locale.getLanguage());
            cbDisciplineCategory.getItems().addAll(teamCategories);
        } catch (Exception e) {
            LOG.info(String.format("Can't read data for %s from DB. Reason : %s", RowingRaceCodeTables.CT_TEAM_CATEGORIES, e.getMessage()));
        }
    }

    private void readOrganizationData() {
        organizations.clear();
        try {
            organizations.addAll(dbService.getSchools());
        } catch (ConnectivityException | SQLException e) {
            displayErrorMessage(resourceBundle.getString("ERROR_LOAD_SCHOOLS"));
            LOG.debug("Error loading organization data from database.", e);
        }
    }

    @FXML
    private void tbTeamsSelected() {
        teams.clear();
        try {
            teams.addAll(dbService.getTeamsBySchoolId(selectedItem.getId()));
            initializeTeamCategories();
        } catch (ConnectivityException | SQLException e) {
            LOG.debug(String.format("Error loading teams for school %s with id %d", selectedItem.getName(), selectedItem.getId()), e);
            displayErrorMessage(resourceBundle.getString("ERROR_LOAD_TEAMS"));
        }
    }

    @FXML
    private void newTeam() {
        tfTeamName.setText("");
        cbDisciplineCategory.getSelectionModel().selectFirst();
        selectedTeam = new TeamDto();
    }

    @FXML
    private void submitTeam() {
        if (teamDataValid()) {
            try {
                if (selectedTeam == null) {
                    selectedTeam = new TeamDto();
                }
                selectedTeam.setName(tfTeamName.getText());
                selectedTeam.setCategory(cbDisciplineCategory.getSelectionModel().getSelectedItem());
                selectedTeam.setMaxRacers(cbRaceYear.getSelectionModel().getSelectedItem().getMaxRacers());
                selectedTeam.setNumberOfAlternates(cbRaceYear.getSelectionModel().getSelectedItem().getNumberOfAlternates());
                tvTeams.getItems().add(selectedTeam);
                dbService.saveTeam(selectedTeam, selectedItem.getId());
            } catch (ConnectivityException | SQLException e) {
                LOG.error("Error saving team data.", e);
                displayErrorMessage(resourceBundle.getString("ERR_SAVE_TEAM"));
            }
        }
    }

    private boolean teamDataValid() {
        String error = "";
        if (tfTeamName.getText().equals("")) {
            error = resourceBundle.getString("ERR_TEAM_EMTPY");
        }
        if (cbDisciplineCategory.getSelectionModel().getSelectedItem() == null) {
            error += resourceBundle.getString("ERR_TEAM_CATEGORY_EMPTY");
        }
        if (!error.equals("")) {
            displayErrorMessage(error);
            return false;
        }
        return true;
    }

    @FXML
    private void newOrganiation() {
        tfZipCode.setText("");
        tfStreet.setText("");
        tfCity.setText("");
        tfOrganizationName.setText("");
        cbCountry.getSelectionModel().selectFirst();
        cbRaceCategory.getSelectionModel().selectFirst();
        selectedItem = new School();
    }

    @FXML
    private School submitOrganization() {
        School school = selectedItem;
        if (organizationDataValid()) {
            school.setName(tfOrganizationName.getText());
            Address address = new Address();
            address.setCity(tfCity.getText());
            address.setStreet(tfStreet.getText());
            address.setZip(tfZipCode.getText());
            address.setCountry(cbCountry.getSelectionModel().getSelectedItem());
            school.setAddress(address);
            List<EnumEntity> raceCategories = new ArrayList<>();
            raceCategories.add(cbRaceCategory.getSelectionModel().getSelectedItem());
            school.setRaceCategories(raceCategories);
            if (school.getId() == null || school.getId().intValue() <= 0) {
                try {
                    school.setKey(dbService.getSchoolCode());
                } catch (ConnectivityException | SQLException e) {
                    LOG.error("Error retrieving school key. ", e);
                    displayErrorMessage(resourceBundle.getString("ERR_SCHOOL_KEY_LOAD"));
                    return null;
                }
            }
            try {
                dbService.saveSchool(school);
            } catch (ConnectivityException | SQLException e) {
                LOG.error("Error while saving organization data. ", e);
                displayErrorMessage(resourceBundle.getString("ERR_SAVE_SCHOOL"));
            }
        }

        return school;
    }

    private boolean organizationDataValid() {
        String error = "";
        if (tfOrganizationName.getText().equals("")) {
            error = resourceBundle.getString("ERR_SCHOOL_NAME_EMTPY");
        }
        if (tfCity.getText().equals("")) {
            error += resourceBundle.getString("ERR_SCHOOL_CITY_EMTPY") + "\n";
        }
        if (tfStreet.getText().equals("")) {
            error += resourceBundle.getString("ERR_SCHOOL_STREET_EMTPY") + "\n";
        }
        if (tfZipCode.getText().equals("")) {
            error += resourceBundle.getString("ERR_SCHOOL_ZIP_EMTPY") + "\n";
        }
        if (cbCountry.getSelectionModel().getSelectedItem() == null) {
            error += resourceBundle.getString("ERR_SCHOOL_COUNTRY_EMTPY") + "\n";
        }
        if (cbRaceCategory.getSelectionModel().getSelectedItem() == null) {
            error += resourceBundle.getString("ERR_RACE_CATEGORY_EMPTY") + "\n";
        }
        if (!error.equals("")) {
            displayErrorMessage(error);
        }
        return true;
    }

    @FXML
    private void deleteOrganization() {
        try {
            dbService.deleteSchool(selectedItem);
        } catch (ConnectivityException | SQLException e) {
            LOG.error("Error deleting organization data.", e);
            displayErrorMessage(resourceBundle.getString("ERROR_ORGANIZATION_DELETE"));
        }
        readOrganizationData();
    }

    @FXML
    private void deleteTeam() {
        Long teamId = tvTeams.getSelectionModel().getSelectedItem().getId();
        try {
            dbService.deleteTeam(teamId);
        } catch (ConnectivityException | SQLException e) {
            LOG.error(String.format("Error while deleting team with id %d", teamId), e);
            displayErrorMessage(resourceBundle.getString("ERROR_TEAM_DELETE"));
        }
    }

}


package sk.insomnia.rowingRace.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dataStore.NoDataForKeyException;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.Team;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TeamEditDialogController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(TeamEditDialogController.class);
    @FXML
    private TextField tfTeamName;
    @FXML
    private ComboBox<EnumEntityDto> cbTeamCategory;
    @FXML
    private ComboBox<RaceYearDto> cbRaceYear;

    private Team team;
    private Stage dialogStage;
    private boolean okClicked = false;
    private List<EnumEntityDto> teamCategories;

    public void setTeam(Team team) throws DtoUtils.DtoUtilException {
        this.team = team;
        if (team != null) {
            if (team.getName() != null) tfTeamName.setText(team.getName());
            if (team.getTeamCategory() != null)
                cbTeamCategory.setValue(DtoUtils.transformWithLanguageDependentValue(team.getTeamCategory(), locale.getLanguage(), SimpleEnumEntityDto.class));
        } else {
            tfTeamName.setText("");
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;

    }

    @FXML
    private void initialize() {
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            team.setName(tfTeamName.getText());
            team.setTeamCategory(cbTeamCategory.getValue());
            team.setMaxRacers(cbRaceYear.getValue().getMaxRacers());
            team.setNumberOfAlternates(cbRaceYear.getValue().getNumberOfAlternates());
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (tfTeamName.getText() == null || tfTeamName.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_NAME_EMTPY");
        }

        if (cbTeamCategory.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_TEAM_CATEGORY_EMPTY");
        }

        if (cbRaceYear.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_RACE_YEAR_NOT_SELECTED");
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showErrorDialog(dialogStage, errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            return false;
        }
    }

    @Override
    public void initLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void initResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void initializeFormData() {
        try {
            teamCategories = dataProcessor.getSlaveValues(dataProcessor.getSchoolRaceCategory(), RowingRaceCodeTables.CT_RACE_CATEGORY, RowingRaceCodeTables.CT_TEAM_CATEGORIES);
            if (teamCategories != null) {
                this.fileService.saveOrUpdate(teamCategories, RowingRaceCodeTables.CT_TEAM_CATEGORIES);
            }
        } catch (Exception e) {
            LOG.info(String.format("Can't read data for %s from DB. Reason : %s", RowingRaceCodeTables.CT_TEAM_CATEGORIES, e.getMessage()));
        }
        if (teamCategories == null) {
            try {
                teamCategories = this.fileService.loadCodeTable(RowingRaceCodeTables.CT_TEAM_CATEGORIES);
            } catch (IOException e) {
                LOG.info(String.format("Can't read data for %s from file. Reason : %s", RowingRaceCodeTables.CT_TEAM_CATEGORIES, e.getMessage()));
            }
        }
        if (teamCategories != null) {
            setupFormControls();
        } else {
            Object[] params = {RowingRaceCodeTables.CT_TEAM_CATEGORIES};
            String errorMessage = MessageFormat.format(resourceBundle.getString("ERR_NO_VALUES_SET"), params) + "\n";
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("DATA_SAVE"),
                    resourceBundle.getString("DATA_SAVE_TITLE"));
        }
    }

    private void setupFormControls() {
        this.cbTeamCategory.getItems().clear();
        this.cbTeamCategory.getItems().addAll(teamCategories);
        setRaceYears();
    }

    private void setRaceYears() {
        this.cbRaceYear.getItems().clear();
        try {
            RowingRace rowingRace = (RowingRace) CommonDataStore.getInstanceOfClass(RowingRace.class);
            if (rowingRace != null) {
                List<RaceYearDto> raceYearDtos = DtoUtils.raceYearsToDtos(rowingRace.getRaceYears(), this.locale.getLanguage());
                this.cbRaceYear.getItems().addAll(raceYearDtos);
            }
        } catch (NoDataForKeyException e) {
            String msg = this.resourceBundle.getString("DATA_LOAD");
            displayErrorMessage(this.resourceBundle.getString("ERR_RACE_LOAD"), msg, msg);
        }
    }
}

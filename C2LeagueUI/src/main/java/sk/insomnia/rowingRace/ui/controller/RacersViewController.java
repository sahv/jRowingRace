package sk.insomnia.rowingRace.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.exceptions.RowingRaceException;
import sk.insomnia.rowingRace.listeners.DataChangeObserver;
import sk.insomnia.rowingRace.listeners.SchoolListener;
import sk.insomnia.rowingRace.listeners.TeamsListener;
import sk.insomnia.rowingRace.so.Racer;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.rowingRace.so.Team;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bobek on 6/26/2014.
 */
public class RacersViewController extends AbstractController implements TeamsListener, SchoolListener {

    private static final Logger logger = LoggerFactory.getLogger(RacersViewController.class);

    @FXML
    private TableView<Racer> racersTable;
    @FXML
    private TableColumn<Racer, String> nameColumn;
    @FXML
    private TableColumn<Racer, String> surnameColumn;
    @FXML
    private TextField tfRacerName;
    @FXML
    private TextField tfRacerSurname;
    @FXML
    private TextField tfRacerDob;
    @FXML
    private ComboBox<Team> cbRacerTeam;

    private Racer racer;
    @FXML
    private Button btnDeleteTeam;
    @FXML
    private Button btnAddTeam;
    @FXML
    private Button btnEditTeam;
    @FXML
    private Button btnAddRacer;
    @FXML
    private Button btnDeleteRacer;
    @FXML
    private Button btnNewRacer;

    @FXML
    private void initialize() {
        logger.info("Initializing controller.");
        initializeFormControls();
        DataChangeObserver.registerTeamsListener(this);
        DataChangeObserver.registerSchoolListener(this);
    }

    private void initializeFormControls() {
        racersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        racersTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Racer>() {

            @Override
            public void changed(ObservableValue<? extends Racer> observable,
                                Racer oldValue, Racer newValue) {
                showRacerDetails(newValue);
                btnAddRacer.setDisable(false);
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<Racer, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Racer, String>("surname"));
        cbRacerTeam.getItems().clear();
    }

    @FXML
    private void handleNewRacer() {
        if (racerCountReachedLimit()) {
            btnAddRacer.setDisable(true);
        }
        resetRacerForm();
    }

    private void resetRacerForm() {
        this.racer = new Racer();
        tfRacerDob.setText("");
        tfRacerName.setText("");
        tfRacerSurname.setText("");
    }

    private void showRacerDetails(Racer racer) {
        this.racer = racer;
        if (this.racer != null) {
            tfRacerName.setText(racer.getName());
            tfRacerSurname.setText(racer.getSurname());
            if (racer.getYearOfBirth() != null)
                tfRacerDob.setText(Integer.toString(racer.getYearOfBirth().intValue()));

        } else {
            tfRacerName.setText("");
            tfRacerSurname.setText("");
            tfRacerDob.setText("");
        }
    }

    @FXML
    private void handleAddTeam() {
        Team _team = new Team();
        if (showTeamEditDialog(_team)) {
            try {
                this.dataProcessor.onTeamAdded(_team);
                this.cbRacerTeam.getItems().add(_team);
            } catch (RowingRaceException e) {
                displayErrorMessage(resourceBundle.getString("ERROR_TEAM_SAVE"),
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            }
        }
    }

    public boolean showTeamEditDialog(Team team) {
        try {

            FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("/sk/insomnia/rowingRace/views/TeamEditDialog.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editTeam"));
            dialogStage.initModality(Modality.NONE);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TeamEditDialogController controller = loader.getController();
            controller.setDataProcessor(dataProcessor);
            controller.initResourceBundle(resourceBundle);
            controller.initLocale(locale);
            controller.initializeFormData();
            controller.setDialogStage(dialogStage);
            controller.setTeam(team);

            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (DtoUtils.DtoUtilException | IOException e) {
            logger.error("Can't load team data due to {}", e);
            //errorMessage, windowTitle, windowLabel
            displayErrorMessage(this.resourceBundle.getString("DATA_LOAD"), this.resourceBundle.getString("ERROR"), this.resourceBundle.getString("ERROR"));
            return false;
        }
    }

    @FXML
    private void handleDeleteTeam() {
        if (cbRacerTeam.getValue() != null) {
            try {
                this.dataProcessor.onTeamDeleted(cbRacerTeam.getValue());
                cbRacerTeam.getItems().remove(cbRacerTeam.getValue());
            } catch (RowingRaceException e) {
                displayErrorMessage(resourceBundle.getString("ERROR_TEAM_DELETE"),
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));

            }
        }
    }

    @FXML
    private void handleCbRacerTeamAction() {
        if (racerCountReachedLimit()) {
            btnAddRacer.setDisable(true);
        } else {
            btnAddRacer.setDisable(false);
        }
        refreshRacersTable();
    }

    private boolean racerCountReachedLimit() {
        if (cbRacerTeam.getValue() != null
                && cbRacerTeam.getValue().getRacers() != null
                && cbRacerTeam.getValue().getRacers().size() == (cbRacerTeam.getValue().getMaxRacers().intValue() + cbRacerTeam.getValue().getNumberOfAlternates().intValue())) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    private void handleEditTeam() {
        if (cbRacerTeam.getValue() != null) {
            Team team = cbRacerTeam.getValue();
            showTeamEditDialog(team);
            try {
                this.dataProcessor.onTeamChanged(team);
                int originalPosition = cbRacerTeam.getSelectionModel().getSelectedIndex();
                cbRacerTeam.getItems().remove(team);
                cbRacerTeam.getItems().add(originalPosition, team);
            } catch (RowingRaceException e) {
                logger.error("Error while saving team data.", e);
                displayErrorMessage(resourceBundle.getString("ERROR_TEAM_SAVE"),
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            }
        }
    }

    private void refreshRacersTable() {
        racersTable.setItems(null);
        racersTable.layout();
        if (cbRacerTeam.getValue() != null
                && cbRacerTeam.getValue().getRacers() != null) {
            ObservableList<Racer> _racers = FXCollections.observableArrayList();
            _racers.addAll(cbRacerTeam.getValue().getRacers());
            racersTable.setItems(_racers);
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
        //TODO implement this if needed
    }


    @Override
    public void onTeamsChange(List<Team> teamDtoList) {
        this.cbRacerTeam.getItems().clear();
        this.cbRacerTeam.getItems().addAll(teamDtoList);
    }

    private void enableTeamControls() {
        btnAddTeam.setDisable(false);
        btnEditTeam.setDisable(false);
        btnAddRacer.setDisable(false);
        btnDeleteRacer.setDisable(false);
        btnNewRacer.setDisable(false);
        btnDeleteTeam.setDisable(false);
    }

    private void setRacerFromForm() {
        if (this.racer == null) racer = new Racer();
        racer.setName(tfRacerName.getText());
        racer.setSurname(tfRacerSurname.getText());
        try {
            racer.setYearOfBirth(Integer.decode(tfRacerDob.getText()));
        } catch (Exception e) {
            logger.error("Invalid format of racer birth year.", e);
            displayErrorMessage(resourceBundle.getString("ERR_BAD_DATE_FORMAT"),
                    resourceBundle.getString("ERR_BAD_DATE_FORMAT_TITLE"), resourceBundle.getString("ERROR"));
        }
    }

    private void setFormByRacer(Racer racer) {

        if (racer != null) {
            if (racer.getName() != null) {
                tfRacerName.setText(racer.getName());
            } else {
                tfRacerName.setText("");
            }
            if (racer.getSurname() != null) {
                tfRacerSurname.setText(racer.getSurname());
            } else {
                tfRacerSurname.setText("");
            }
            if (racer.getYearOfBirth() != null) {
                tfRacerDob.setText(Integer.toString(racer.getYearOfBirth()));
            } else {
                tfRacerDob.setText("");
            }
        } else {
            tfRacerName.setText("");
            tfRacerSurname.setText("");
        }
        this.racer = racer;
    }

    @FXML
    private void handleAddRacer() {
        if (isInputValid()) {
            boolean isUpdate = false;
            setRacerFromForm();
            Team team = cbRacerTeam.getValue();
            if (team.getRacers() == null) {
                team.setRacers(new ArrayList<Racer>());
            }
            if (racer.getId() != null &&
                    racer.getId() > 0) {
                isUpdate = true;
            }
            if (isUpdate) {
                for (Racer r : team.getRacers()) {
                    if (r.getId().intValue() == racer.getId().intValue()) {
                        team.getRacers().remove(r);
                        team.getRacers().add(racer);
                        break;
                    }
                }
            } else {
                team.getRacers().add(racer);
            }
            refreshRacersTable();
            String errorMessage = null;
            try {
                if (isUpdate) {
                    this.dataProcessor.onRacerChanged(racer);
                } else {
                    this.dataProcessor.onRacerAdded(racer, cbRacerTeam.getSelectionModel().getSelectedItem().getId());
                }
            } catch (RowingRaceException e) {
                errorMessage = resourceBundle.getString("ERR_ADD_RACER");
                logger.info(ExceptionUtils.exceptionAsString(e));
            }
            if (errorMessage != null) {
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE_TITLE"));
            }
            resetRacerForm();
            if (cbRacerTeam.getValue() != null
                    && cbRacerTeam.getValue().getRacers() != null
                    && cbRacerTeam.getValue().getRacers().size() == 6) {
                btnAddRacer.setDisable(true);
            }
        }

        if (racerCountReachedLimit()) {
            btnAddRacer.setDisable(true);
        }

    }

    @FXML
    private void handleDeleteRacer() {
        if (racersTable.getSelectionModel().getSelectedItem() != null) {
            String errorMessage = null;
            try {
                this.dataProcessor.onRacerDeleted(racersTable.getSelectionModel().getSelectedItem());
            } catch (RowingRaceException e) {
                errorMessage = resourceBundle.getString("ERR_DELETE_RACER");
                logger.error("Error while deleting racer", e);
            }
            if (errorMessage != null) {
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE_TITLE"));
            }
            cbRacerTeam.getValue().getRacers().remove(racersTable.getSelectionModel().getSelectedItem());
            this.refreshRacersTable();
            try {
                this.dataProcessor.saveSchoolInProcess(true);
            } catch (RowingRaceException e) {
                displayErrorMessage(e.getMessage(),
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE_TITLE"));
            }
            if (btnAddRacer.isDisabled()) {
                btnAddRacer.setDisable(false);
            }
            resetRacerForm();

            if (racerCountReachedLimit()) {
                btnAddRacer.setDisable(true);
            }
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (tfRacerName.getText() == null || tfRacerName.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_FIRST_NAME_EMTPY") + "\n";
        }
        if (tfRacerDob.getText() == null || tfRacerDob.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_DOB_EMTPY") + "\n";
        }
        if (tfRacerSurname.getText() == null || tfRacerSurname.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_LAST_NAME_EMTPY") + "\n";
        }

        if (cbRacerTeam.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_TEAM_EMTPY") + "\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            return false;
        }
    }


    @Override
    public void onSchoolSelected(School school) {
        enableTeamControls();
    }
}

package sk.insomnia.rowingRace.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.dto.PerformanceDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.mapping.MappingUtil;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.Interval;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.tools.dateUtils.DateConvertor;
import sk.insomnia.tools.timeUtil.RowingRaceTimeUtil;

import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class PerformancesAdministrationController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(PerformancesAdministrationController.class);
    public static final String DIM_TIME = "DIM_TIME";
    private RowingRace rowingRace;

    @FXML
    ComboBox<RaceYearDto> cbRaceYear;

    @FXML
    ComboBox<RaceRound> cbRaceRound;

    @FXML
    TableView<PerformanceDto> tbPerformances;
    @FXML
    TableColumn tcOrganizationName;
    @FXML
    TableColumn tcTeamName;
    @FXML
    TableColumn tcPerformanceTime;
    @FXML
    TableColumn tcPerformanceDate;
    @FXML
    TableColumn tcRowIndex;
    @FXML
    TextField tfMinutes;
    @FXML
    TextField tfSeconds;
    @FXML
    TextField tfMillis;
    @FXML
    TextField tfDistance;
    @FXML
    TableColumn tcDistance;

    private PerformanceDto selectedItem;

    private ObservableList<PerformanceDto> performances = FXCollections.observableArrayList();


    public RowingRace getRowingRace() {
        return rowingRace;
    }

    @FXML
    private void initialize() {
        tbPerformances.setItems(performances);

        tbPerformances.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PerformanceDto>() {

            @Override
            public void changed(ObservableValue<? extends PerformanceDto> observable,
                                PerformanceDto oldValue, PerformanceDto newValue) {
                showPerformanceDetails(newValue);
            }
        });

        tcTeamName.setCellValueFactory(new PropertyValueFactory<PerformanceDto, String>("teamName"));
        tcOrganizationName.setCellValueFactory(new PropertyValueFactory<PerformanceDto, String>("organizationName"));
        tcRowIndex.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PerformanceDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PerformanceDto, String> performanceDtoStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                retval.setValue(String.valueOf(performances.indexOf(performanceDtoStringCellDataFeatures.getValue()) + 1));
                return retval;
            }
        });
        tcPerformanceTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PerformanceDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PerformanceDto, String> performanceDtoStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                if (performanceDtoStringCellDataFeatures.getValue().getFinalTime() != null) {
                    retval.setValue(RowingRaceTimeUtil.formatRowingTime(performanceDtoStringCellDataFeatures.getValue().getFinalTime()));
                }
                return retval;
            }
        });
        tcPerformanceDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PerformanceDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PerformanceDto, String> performanceDtoStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                if (performanceDtoStringCellDataFeatures.getValue().getCreatedOn() != null) {
                    retval.setValue(DateConvertor.dateToString(performanceDtoStringCellDataFeatures.getValue().getCreatedOn()));
                }
                return retval;
            }
        });
        tcDistance.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PerformanceDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PerformanceDto, String> performanceDtoStringCellDataFeatures) {
                SimpleStringProperty retval = new SimpleStringProperty();
                if (null != performanceDtoStringCellDataFeatures.getValue().getFinalDistance()) {
                    retval.setValue(performanceDtoStringCellDataFeatures.getValue().getFinalDistance().toString());
                }
                return retval;
            }
        });


    }

    private void showPerformanceDetails(PerformanceDto newValue) {
        if (newValue == null) {
            return;
        }
        if (newValue.getFinalTime() != null) {
            String[] time = RowingRaceTimeUtil.rowingTimeAsParams(newValue.getFinalTime());
            tfMinutes.setText(time[0]);
            tfSeconds.setText(time[1]);
            tfMillis.setText(time[2].replace(".", ""));
        }
        selectedItem = newValue;
    }

    public void setRowingRace(RowingRace rowingRace) {
        this.rowingRace = rowingRace;
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
            ControllerUtils.prepareRaceData(cbRaceYear, rowingRace, this);
        } catch (Exception e) {
            Dialogs.showErrorDialog(new Stage(), e.getMessage(),
                    resourceBundle.getString("DATA_LOAD"),
                    resourceBundle.getString("DATA_LOAD_TITLE"));
        }
    }

    @FXML
    private void handleCbRaceYearChange() {
        this.cbRaceRound.getItems().clear();
        this.cbRaceRound.getItems().addAll(cbRaceYear.getSelectionModel().getSelectedItem().getRounds());
    }

    private boolean dimensionIsDistance() {
        Discipline selectedDiscipline = cbRaceRound.getSelectionModel().getSelectedItem().getDiscipline();
        if (selectedDiscipline.getIntervals().size() > 0) {
            Interval interval = selectedDiscipline.getIntervals().get(0);
            if (interval.getDimension().getAcronym().equals(DIM_TIME)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleRaceRoundChange() {

        resetInputs();

        dimensionIsDistance();
        if (dimensionIsDistance()) {
            //TODO disable time inputs and enable distance inputs
            tfDistance.setDisable(false);
            tcPerformanceTime.setVisible(false);
            tcDistance.setVisible(true);
            tfMillis.setDisable(true);
            tfMinutes.setDisable(true);
            tfSeconds.setDisable(true);
        } else {
            //TODO disable disable distance inputs and enable time inputs
            tfDistance.setDisable(true);
            tcPerformanceTime.setVisible(true);
            tcDistance.setVisible(false);
            tfMillis.setDisable(false);
            tfMinutes.setDisable(false);
            tfSeconds.setDisable(false);
        }

        readPerformances();

    }

    private void readPerformances() {
        RaceYearDto raceYear = cbRaceYear.getSelectionModel().getSelectedItem();
        RaceRound raceRound = cbRaceRound.getSelectionModel().getSelectedItem();
        try {
            this.performances.clear();
            this.performances.addAll(dbService.getAllPerformancesForRaceYearAndRound(raceYear.getId(), raceRound.getId()));
        } catch (SQLException e) {
            LOG.error(String.format("Can't read performance data for race year %s and race round %s.", raceYear.getYear(), raceRound.getDescription()), e);
            displayErrorMessage(resourceBundle.getString("ERROR_LOAD_PERFORMANCES"));
        }
    }

    @FXML
    private void handleSavePerformance() {
        if (isRecordValid()) {
            setPerformanceData();
            try {
                dbService.saveOrUpdate(MappingUtil.toSO(selectedItem));
            } catch (ConnectivityException | SQLException e) {
                LOG.error("Can't save performance.", e);
                displayErrorMessage(resourceBundle.getString("ERROR_PERFORMANCE_SAVE"));
            }
            selectedItem = null;
            readPerformances();
        }
    }

    private void setPerformanceData() {
        selectedItem.setCreatedOn(new Date());
        if (dimensionIsDistance()) {
            selectedItem.setFinalDistance(Double.parseDouble(tfDistance.getText()));
        } else {
            selectedItem.setFinalTime(RowingRaceTimeUtil.rowingTimeAsDouble(tfMinutes.getText(), tfSeconds.getText(), tfMillis.getText()));
        }
    }

    @FXML
    private void deletePerformance() {
        if (tbPerformances.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        try {
            dbService.deletePerformance(tbPerformances.getSelectionModel().getSelectedItem());
        } catch (ConnectivityException | SQLException e) {
            LOG.info("Error deleting performance.", e);
            displayErrorMessage(resourceBundle.getString("ERROR_PERFORMANCE_DELETE"));
        }
        readPerformances();
    }

    private void resetInputs() {
        tfMinutes.setText("");
        tfSeconds.setText("");
        tfMillis.setText("");

        tfDistance.setText("");
    }

    private boolean isRecordValid() {
        String errorMessage = "";
        if (dimensionIsDistance()) {
            if (tfDistance.getText().isEmpty()) {
                errorMessage += resourceBundle.getString("MISSING_DISTANCE");
            }
            if (!errorMessage.equals("")) {
                displayErrorMessage(errorMessage);
                return false;
            }
            return true;
        } else {
            if (tfMinutes.getText().equals("")) {
                errorMessage += resourceBundle.getString("MISSING_MINUTES");
            }
            if (tfSeconds.getText().equals("")) {
                errorMessage += resourceBundle.getString("MISSING_SECONDS");
            }
            if (tfMillis.getText().equals("")) {
                errorMessage += resourceBundle.getString("MISSING_MILLIS");
            }
            if (!errorMessage.equals("")) {
                displayErrorMessage(errorMessage);
                return false;
            }
            return true;
        }
    }


    @Override
    public void setDbService(RowingRaceDbFacade dbService) {
        this.dbService = dbService;
    }
}

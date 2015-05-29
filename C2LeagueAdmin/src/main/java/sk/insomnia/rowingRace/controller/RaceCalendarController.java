package sk.insomnia.rowingRace.controller;

import extfx.scene.control.CalendarView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.application.AdminGui;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dataStore.NoDataForKeyException;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.mapping.MappingUtil;
import sk.insomnia.rowingRace.markers.DisciplineCategory;
import sk.insomnia.rowingRace.markers.RaceCategory;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.tools.dateUtils.DateConvertor;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bobek on 7/16/2014.
 */
public class RaceCalendarController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(RaceCalendarController.class);
    @FXML
    private TableView<RaceRound> raceRoundsTable;
    @FXML
    private TableColumn<RaceRound, String> roundNumber;
    @FXML
    private TableColumn<RaceRound, String> roundBegin;
    @FXML
    private TableColumn<RaceRound, String> roundEnd;
    @FXML
    private TableColumn<RaceRound, String> roundDiscipline;
    @FXML
    private ComboBox<DisciplineCategoryDto> cbRoundDisciplineCategory;
    @FXML
    private ComboBox<Discipline> cbRoundDiscipline;
    @FXML
    private ComboBox<RaceYearDto> cbRaceYear;

    @FXML
    private TextField tfRoundNumber;
    @FXML
    private TextField tfRoundEnd;
    @FXML
    private TextField tfRoundBegin;
    @FXML
    private TextField tfRoundDescription;


    private RowingRace rowingRace;
    private RaceRound selectedRound;
    private List<EnumEntityDto> raceCategoriesList;


    @FXML
    private void initialize() {
        roundNumber.setCellValueFactory(new PropertyValueFactory<RaceRound, String>("roundNumber"));
        roundBegin.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RaceRound, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RaceRound, String> raceRoundStringCellDataFeatures) {
                SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
                simpleStringProperty.setValue(DateConvertor.dateToString(raceRoundStringCellDataFeatures.getValue().getBegin()));
                return simpleStringProperty;
            }
        });
        roundEnd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RaceRound, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RaceRound, String> raceRoundStringCellDataFeatures) {
                SimpleStringProperty simpleStringProperty = new SimpleStringProperty();
                simpleStringProperty.setValue(DateConvertor.dateToString(raceRoundStringCellDataFeatures.getValue().getEnd()));
                return simpleStringProperty;
            }
        });
        roundDiscipline.setCellValueFactory(new PropertyValueFactory<RaceRound, String>("disciplineName"));
        raceRoundsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        raceRoundsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RaceRound>() {

            public void changed(ObservableValue<? extends RaceRound> observable,
                                RaceRound oldValue, RaceRound newValue) {
                showRaceRoundDetails(newValue);
            }
        });
    }

    private void showRaceRoundDetails(RaceRound raceRound) {
        if (raceRound != null) {
            this.tfRoundBegin.setText(DateConvertor.dateToString(raceRound.getBegin()));
            this.tfRoundEnd.setText(DateConvertor.dateToString(raceRound.getEnd()));
            this.tfRoundNumber.setText(raceRound.getRoundNumber().toString());
            this.tfRoundDescription.setText(raceRound.getDescription());
            //    	this.cbRoundDisciplineCategory.
            this.cbRoundDiscipline.getSelectionModel().select(raceRound.getDiscipline());
            this.selectedRound = raceRound;
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
        prepareDisciplineCategoriesData();
        try {
            ControllerUtils.prepareRaceData(cbRaceYear, rowingRace, this);
        } catch (Exception e) {
            Dialogs.showErrorDialog(new Stage(), e.getMessage(),
                    resourceBundle.getString("DATA_LOAD"),
                    resourceBundle.getString("DATA_LOAD_TITLE"));
        }
        refreshRaceRoundsTable();
        try {
            this.raceCategoriesList = dbService.getCodeTable(RowingRaceCodeTables.CT_RACE_CATEGORY, this.locale);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            logger.error("No data found for class {}", RaceCategory.class);
        }
    }

    private void prepareDisciplineCategoriesData() {
        try {
            cbRoundDisciplineCategory.getItems().clear();
            List<DisciplineCategoryDto> disciplineCategories = (List<DisciplineCategoryDto>) CommonDataStore.getValuesForClass(DisciplineCategory.class);
            cbRoundDisciplineCategory.getItems().addAll(disciplineCategories);
        } catch (NoDataForKeyException e) {
            Object[] params = {RowingRaceCodeTables.CT_RACE_CATEGORY};
            displayMessage(resourceBundle.getString("ERR_CODE_TABLE_DATA_LOAD"),
                    resourceBundle.getString("DATA_LOAD_TITLE"),
                    resourceBundle.getString("DATA_LOAD"));
        }
    }


    @Override
    public void setDbService(RowingRaceDbFacade dbService) {
        this.dbService = dbService;
    }


    @FXML
    private void handleDeleteRound() {
        RaceRound _r = raceRoundsTable.getSelectionModel().getSelectedItem();
        this.cbRaceYear.getValue().getRounds().remove(_r);
        try {
            this.dbService.deleteRowingRaceRound(_r);
        } catch (Exception e) {
            logger.error("handleDeleteRound error while saving data to DB", e);
        }
        refreshRaceRoundsTable();
    }

    @FXML
    private void handleRoundBeginClick() {
        Date d = showDatePicker();
        if (d != null) {
            this.tfRoundBegin.setText(DateConvertor.dateToString(d));
        }
    }

    @FXML
    private void handleRoundEndClick() {
        Date d = showDatePicker();
        if (d != null) {
            this.tfRoundEnd.setText(DateConvertor.dateToString(d));
        }
    }

    @FXML
    private void handleNewRound() {
        tfRoundBegin.setText("");
        tfRoundEnd.setText("");
        tfRoundNumber.setText("");
        tfRoundDescription.setText("");
        this.selectedRound = null;
    }

    @FXML
    private void handleSubmitRound() {
        if (raceRoundDataValid()) {
            if (this.rowingRace.getRaceYears() == null) {
                this.rowingRace.setRaceYears(new ArrayList<RaceYear>());
            }
            readRoundDataFromForm();
            try {
                if (this.selectedRound.getId() == null) {
                    cbRaceYear.getValue().getRounds().add(this.selectedRound);
                }
                this.dbService.addRowingRaceRound(this.selectedRound);
                this.selectedRound = null;
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = resourceBundle.getString("ERR_RACE_SAVE");
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            }
            refreshRaceRoundsTable();
            handleNewRound();
        }
    }

    private void readRoundDataFromForm() {
        RaceYearDto _ry = cbRaceYear.getValue();
        if (this.selectedRound == null) {
            this.selectedRound = new RaceRound();
        }
        this.selectedRound.setRaceYear(MappingUtil.toSO(_ry));
        try {
            this.selectedRound.setBegin(DateConvertor.stringToDate(tfRoundBegin.getText()));
            this.selectedRound.setEnd(DateConvertor.stringToDate(tfRoundEnd.getText()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        this.selectedRound.setDiscipline(new Discipline(cbRoundDiscipline.getValue()));
        this.selectedRound.setRoundNumber(Integer.parseInt(tfRoundNumber.getText()));
        this.selectedRound.setDescription(tfRoundDescription.getText());

        if (_ry.getRounds() == null) {
            _ry.setRounds(new ArrayList<RaceRound>());
        }
    }

    private boolean raceRoundDataValid() {
        String errorMessage = "";

        if (tfRoundDescription.getText() == null || tfRoundDescription.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_MISSING_ROUND_DESCRIPTION") + "\n";
        }
        try {
            DateConvertor.stringToDate(tfRoundBegin.getText());
            DateConvertor.stringToDate(tfRoundEnd.getText());
        } catch (ParseException e) {
            errorMessage = resourceBundle.getString("ERR_BAD_DATE_FORMAT") + "\n";
            errorMessage = resourceBundle.getString("ERR_BAD_DATE_FORMAT") + "\n";
            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("ERR_BAD_DATE_FORMAT_TITLE"));
        }

        if (cbRoundDiscipline.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_MISSING_ROUND_YEAR") + "\n";
        }
        if (tfRoundNumber.getText() == null || tfRoundNumber.getText().equals("")) {
            errorMessage += resourceBundle.getString("ERR_MISSING_ROUND_NUMBER") + "\n";
        }

        try {
            Integer.parseInt(tfRoundNumber.getText());
        } catch (NumberFormatException e) {
            Object[] params = {resourceBundle.getString("label.raceCalendar.raceRound.roundNumber")};
            errorMessage += MessageFormat.format(resourceBundle.getString("ERR_FIELD_IS_NOT_A_NUMBER"), params) + "\n";
        }

        if (cbRoundDiscipline.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_NO_DISCIPLINE_CATEGORY") + "\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            return false;
        }

    }

    @FXML
    private void handleRoundDisciplineCategoryChange() {
        if (cbRoundDisciplineCategory.getValue() != null) {
            cbRoundDiscipline.getItems().clear();
            if (cbRoundDisciplineCategory.getValue().getDisciplines() != null
                    && cbRoundDisciplineCategory.getValue().getDisciplines().size() > 0) {
                ObservableList<Discipline> _disciplines = FXCollections.observableArrayList();
                _disciplines.addAll(cbRoundDisciplineCategory.getValue().getDisciplines());
                cbRoundDiscipline.getItems().addAll(_disciplines);
            } else {
                String errorMessage = resourceBundle.getString("ERR_DISCIPLINES_EMTPY");
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("INFO_CORRECT_FIELDS"),
                        resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            }
        }
    }

    @FXML
    private void handleRoundDisciplineChange() {
        if (cbRoundDiscipline.getValue() != null) {
            if (cbRoundDiscipline.getValue().getIntervals() != null
                    && cbRoundDiscipline.getValue().getIntervals().size() > 0) {
            } else {
                String errorMessage = resourceBundle.getString("ERR_DISCIPLINE_INTERVALS_EMTPY");
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("INFO_CORRECT_FIELDS"),
                        resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            }
        }
    }

    private void refreshRaceRoundsTable() {
        raceRoundsTable.getItems().clear();
        if (this.cbRaceYear.getValue() != null
                && cbRaceYear.getValue().getRounds() != null) {
            ObservableList<RaceRound> _rounds = FXCollections.observableArrayList();
            _rounds.addAll(cbRaceYear.getValue().getRounds());
            raceRoundsTable.getItems().addAll(_rounds);
        }
        refreshTable(raceRoundsTable);
    }

    private final Date showDatePicker() {

        VBox root = new VBox(4);
        root.setStyle("-fx-padding: 10");

        final CalendarView datePicker = new CalendarView();
        datePicker.localeProperty().set(Locale.GERMAN);


        final Stage dialogStage = new Stage();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        hBox.getChildren().add(datePicker);
        root.getChildren().add(hBox);
        datePicker.selectedDateProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                dialogStage.close();
            }
        });

        datePicker.setLocale(this.locale);

        dialogStage.setTitle(this.resourceBundle.getString("label.dialog.editDisciplineCategory"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(new Stage());
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        dialogStage.showAndWait();
        return datePicker.getSelectedDate();

    }

    @FXML
    private void handleEditRaceYear() {
        raceYearDialog(cbRaceYear.getValue());
    }

    @FXML
    private void handleNewRaceYear() {
        RaceYearDto _ry = new RaceYearDto();
        raceYearDialog(_ry);
    }

    private void raceYearDialog(RaceYearDto _ry) {
        if (showRaceYearDialog(_ry)) {
            if (this.rowingRace == null) {
                this.rowingRace = new RowingRace();
            }
            if (this.rowingRace.getRaceYears() == null) {
                this.rowingRace.setRaceYears(new ArrayList<RaceYear>());
            }
            try {
                boolean wasInsert = true;
                if (_ry.getId() != null
                        && _ry.getId().intValue() > 0) {
                    wasInsert = false;
                }
                RaceYear raceYear = MappingUtil.toSO(_ry);
                dbService.saveOrUpdateRaceYear(raceYear);
                if (wasInsert) {
                    _ry.setId(raceYear.getId());
                    this.rowingRace.getRaceYears().add(raceYear);
                    cbRaceYear.getItems().add(_ry);
                } else {
                    cbRaceYear.getItems().remove(_ry);
                    cbRaceYear.getItems().add(_ry);
                }
            } catch (ConnectivityException | SQLException e) {
                displayMessage(resourceBundle.getString("ERR_SAVE_RACE_YEAR"),
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            }
        }
    }


    @FXML
    private void handleDeleteRaceYear() {
        if (this.dbService.isConnectivity()) {
            String errorMessage = null;
            try {
                this.dbService.deleteRaceYear(MappingUtil.toSO(cbRaceYear.getValue()));
                removeFromRowingRace(this.cbRaceYear.getValue());
                ControllerUtils.setRaceYears(this.cbRaceYear, this.rowingRace.getRaceYears(), this.locale);
            } catch (SQLException e) {
                errorMessage = resourceBundle.getString("ERR_RACE_SAVE");
                logger.error("SQL exception while deleting race year.", e);
            } catch (ConnectivityException e) {
                errorMessage = resourceBundle.getString("ERR_RACE_SAVE");
                logger.error("Connectivity exception while deleting race year.", e);
            }
            if (errorMessage != null) {
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            }
        }
    }

    //FIXME change all SO to DTO and remove all this unnecessary methods
    private void removeFromRowingRace(RaceYearDto raceYearDto) {
        if (rowingRace.getRaceYears() == null) {
            return;
        }
        for (RaceYear raceYear : rowingRace.getRaceYears()) {
            if (raceYear.getId().longValue() == raceYearDto.getId().longValue()) {
                rowingRace.getRaceYears().remove(raceYear);
                return;
            }
        }
    }

    public boolean showRaceYearDialog(RaceYearDto raceYear) {
        try {
            FXMLLoader loader = new FXMLLoader(AdminGui.class.getResource("/sk/insomnia/rowingRace/view/raceYear.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editDisciplineCategory"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RaceYearController controller = loader.getController();
            controller.initLocale(this.locale);
            controller.initResourceBundle(this.resourceBundle);
            controller.setRaceCategories(raceCategoriesList);
            controller.setDialogStage(dialogStage);
            controller.setRaceYear(raceYear);
            dialogStage.showAndWait();
            return controller.isRaceSubmitted();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @FXML
    private void handleRaceYearChange() {
        refreshRaceRoundsTable();
    }

}

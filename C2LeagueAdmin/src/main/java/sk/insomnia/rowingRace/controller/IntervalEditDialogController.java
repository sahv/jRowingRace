package sk.insomnia.rowingRace.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataDbService;
import sk.insomnia.rowingRace.so.Interval;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class IntervalEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(IntervalEditDialogController.class);

    @FXML
    private TextField tfWorkout;
    @FXML
    private TextField tfRest;
    @FXML
    private TextField tfSplit;
    @FXML
    private ComboBox<EnumEntityDto> cbDimension;

    private boolean okClicked;
    private Interval interval;
    private Stage dialogStage;
    private ObservableList<EnumEntityDto> intervalDimensionsData = FXCollections.observableArrayList();
    private ResourceBundle rb;

    private RowingRaceDbFacade dbService;

    private Locale locale;

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void initialize() {
        Locale locale = Locale.getDefault();
        this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);


    }

    public void initializeData() {
        this.loadIntervalDimensions();
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            interval.setWorkoutValue(Integer.parseInt(tfWorkout.getText()));
            interval.setRestValue(Integer.parseInt(tfRest.getText()));
            interval.setDimension(cbDimension.getValue());
            interval.setRelaySplitValue(Integer.parseInt(tfSplit.getText()));
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

        if (tfWorkout.getText() == null || tfWorkout.getText().length() == 0) {
            errorMessage += rb.getString("ERR_WORKOUT_EMPTY");
        }
        if (tfRest.getText() == null || tfRest.getText().length() == 0) {
            errorMessage += rb.getString("ERR_REST_EMPTY");
        }
        if (tfSplit.getText() == null || tfSplit.getText().length() == 0) {
            errorMessage += rb.getString("ERR_SPLIT_EMPTY");
        }
        if (cbDimension.getId() == null || cbDimension.getId().length() == 0) {
            errorMessage += rb.getString("ERR_DIMENSION_EMPTY");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.showErrorDialog(dialogStage, errorMessage,
                    rb.getString("INFO_CORRECT_FIELDS"),
                    rb.getString("INFO_CORRECT_FIELDS_TITLE"));
            return false;
        }
    }

    public void intervalDimensionsData(ObservableList<EnumEntityDto> intervalDimensionsData) {
        this.intervalDimensionsData = intervalDimensionsData;
        cbDimension.getItems().clear();
        cbDimension.getItems().addAll(intervalDimensionsData);
    }

    @SuppressWarnings("unchecked")
    private void loadIntervalDimensions() {

        try {
            cbDimension.getItems().clear();
            if (dbService.isConnectivity()) {
                List<EnumEntityDto> iDims = this.dbService.getCodeTable(RowingRaceCodeTables.CT_INTERVAL_DIMENSION, this.locale);
                this.intervalDimensionsData.clear();
                this.intervalDimensionsData.addAll(iDims);
                cbDimension.setItems(intervalDimensionsData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.showErrorDialog(dialogStage,
                    rb.getString("ERR_FILE_LOAD"),
                    rb.getString("ERR_FILE_LOAD_TITLE"), rb.getString("ERROR"), e);
        }
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) throws InstantiationException, IllegalAccessException, DtoUtils.DtoUtilException {
        this.interval = interval;
        if (interval != null) {
            if (interval.getRestValue() != null) {
                tfRest.setText(interval.getRestValue().toString());
            }
            if (interval.getWorkoutValue() != null) {
                tfWorkout.setText(interval.getWorkoutValue().toString());
            }
            if (interval.getRelaySplitValue() != null) {
                tfSplit.setText(interval.getRelaySplitValue().toString());
            }
            if (interval.getDimension() != null) {
                cbDimension.setValue(DtoUtils.transformWithLanguageDependentValue(interval.getDimension(), locale.getLanguage(), SimpleEnumEntityDto.class));
            }
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.dbService = new RowingRaceDataDbService(locale);
    }

}

package sk.insomnia.rowingRace.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RaceYearController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(RaceYearController.class);
    @FXML
    private TextField tfRaceYear;
    @FXML
    private ComboBox<EnumEntityDto> cbRaceCategories;
    @FXML
    private Slider slMaxRacers;
    @FXML
    private Slider slAlternates;

    private Stage dialogStage;
    private RaceYearDto raceYear;
    private boolean raceSubmitted = false;


    @FXML
    private void initialize() {
        cbRaceCategories.getItems().clear();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleConfirm() {
        readDataFromForm();
        dialogStage.close();
        this.raceSubmitted = true;
    }

    private void setFormData(RaceYearDto raceYear) {
        tfRaceYear.setText(raceYear.getYear());
        cbRaceCategories.getSelectionModel().select((SimpleEnumEntityDto) raceYear.getRaceCategory());
        if (raceYear.getNumberOfAlternates() != null) {
            slAlternates.setValue(raceYear.getNumberOfAlternates());
        }
        if (raceYear.getMaxRacers() != null) {
            slMaxRacers.setValue(raceYear.getMaxRacers());
        }
    }

    private void readDataFromForm() {
        if (tfRaceYear.getText() != null
                && !tfRaceYear.getText().equals("")) {
            this.raceYear.setYear(tfRaceYear.getText());
        }
        EnumEntityDto enumEntityDto = cbRaceCategories.getValue();
        DtoUtils.extractLocalizedValue(enumEntityDto, locale.getLanguage());
        this.raceYear.setRaceCategory(enumEntityDto);
        this.raceYear.setMaxRacers((int) slMaxRacers.getValue());
        this.raceYear.setNumberOfAlternates((int) slAlternates.getValue());
    }

    public boolean isRaceSubmitted() {
        return this.raceSubmitted;
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

    public void setRaceYear(RaceYearDto raceYear) {
        this.raceYear = raceYear;
        setFormData(this.raceYear);
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

    }

    @Override
    public void setDbService(RowingRaceDbFacade dbService) {
        this.dbService = dbService;
    }

    public void setRaceCategories(List<EnumEntityDto> raceCategories) {
        this.cbRaceCategories.getItems().clear();
        try {
            this.cbRaceCategories.getItems().addAll(DtoUtils.listOfLanguageSpecificValues(raceCategories, locale.getLanguage()));
        } catch (DtoUtils.DtoUtilException e) {
            logger.error("Can't set race categories due to {}", e);
        }
        this.cbRaceCategories.getSelectionModel().selectFirst();
    }
}

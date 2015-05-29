package sk.insomnia.rowingRace.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class LanguageController extends AbstractController  {


    @FXML
    private ComboBox<EnumEntityDto> cbLanguage;

    private static final Logger logger = Logger.getLogger(LanguageController.class.toString());

    private Stage dialogStage;
    private EnumEntity language;

    @FXML
    private void initialize() {
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public EnumEntity getLanguage() {
        return language;
    }

    public void setLanguage(EnumEntity language) {
        this.language = language;
    }


    @FXML
    public void handleConfirm() {
        this.language = cbLanguage.getValue();
        dialogStage.close();
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
        List<EnumEntityDto> languages = null;
        try {
            this.cbLanguage.getItems().clear();
            languages = dataProcessor.getCodeTable(RowingRaceCodeTables.CT_LANGUAGES);
            if (languages != null) {
                this.fileService.saveOrUpdate(languages, RowingRaceCodeTables.CT_LANGUAGES);
            }
        } catch (Exception e) {
            String errorMessage = resourceBundle.getString("ERR_LOADING_LANGUAGES");
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("ERR_DATA_LOAD"),
                    resourceBundle.getString("ERR_DATA_LOAD_TITLE"));

            logger.debug(ExceptionUtils.exceptionAsString(e));
        }
        if (languages == null) {
            try {
                languages = this.fileService.loadCodeTable(RowingRaceCodeTables.CT_LANGUAGES);
            } catch (IOException e) {
                String errorMessage = resourceBundle.getString("ERR_LOADING_LANGUAGES");
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("ERR_DATA_LOAD"),
                        resourceBundle.getString("ERR_DATA_LOAD_TITLE"));

                logger.debug(ExceptionUtils.exceptionAsString(e));
            }
        }
        if (languages != null) {
            this.cbLanguage.getItems().addAll(languages);
        }
    }
}

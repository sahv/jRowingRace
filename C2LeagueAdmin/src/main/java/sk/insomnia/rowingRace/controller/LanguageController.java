package sk.insomnia.rowingRace.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataFileService;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LanguageController extends AbstractController {


    @FXML
    private ComboBox<EnumEntityDto> cbLanguage;

    private final Logger logger = Logger.getLogger(LanguageController.class.toString());

    private Stage dialogStage;
    private EnumEntity language;

    private final RowingRaceFileFacade fileService = new RowingRaceDataFileService();

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public EnumEntity getLanguage() {
        return language;
    }

    public void setLanguage(EnumEntitySO language) {
        this.language = language;
    }

    public void initData() {
        List<EnumEntityDto> languages = null;
        try {
            this.cbLanguage.getItems().clear();
            if (this.dbService.isConnectivity()) {
                languages = this.dbService.getCodeTable(RowingRaceCodeTables.CT_LANGUAGES, this.locale);
                if (languages != null) {
                    this.fileService.saveOrUpdate(languages, RowingRaceCodeTables.CT_LANGUAGES);
                }
            }
        } catch (Exception e) {
            String errorMessage = this.resourceBundle.getString("ERR_LOADING_LANGUAGES");

            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    this.resourceBundle.getString("ERR_DATA_LOAD"),
                    this.resourceBundle.getString("ERR_DATA_LOAD_TITLE"));

            logger.log(Level.ALL, e.getStackTrace().toString());
        }
        if (languages == null) {
            try {
                languages = this.fileService.loadCodeTable(RowingRaceCodeTables.CT_LANGUAGES);
            } catch (IOException e) {
                String errorMessage = this.resourceBundle.getString("ERR_LOADING_LANGUAGES");
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        this.resourceBundle.getString("ERR_DATA_LOAD"),
                        this.resourceBundle.getString("ERR_DATA_LOAD_TITLE"));

                logger.log(Level.ALL, e.toString());
            }
        }
        if (languages != null) {
            this.cbLanguage.getItems().addAll(languages);
        }
    }

    @FXML
    public void handleConfirm() {
        this.language = cbLanguage.getValue();
        dialogStage.close();
    }


    @Override
    public void initializeFormData() {

    }
}

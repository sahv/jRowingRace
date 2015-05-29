package sk.insomnia.rowingRace.controller;

import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by bobek on 6/28/2014.
 */
public abstract class AbstractMessageDisplayer {

    public static final String ERR_CODE_TABLE_DATA_SAVE = "ERR_CODE_TABLE_DATA_SAVE";
    public static final String ERR_CODE_TABLE_DATA_LOAD = "ERR_CODE_TABLE_DATA_LOAD";
    public ResourceBundle resourceBundle;

    public void errorMessageBase(RowingRaceCodeTables codeTable, String key) {
        Object[] params = {codeTable};
        String errorMessage = MessageFormat.format(key, params) + "\n";
        displayMessage(errorMessage,
                resourceBundle.getString("DATA_LOAD_TITLE"),
                resourceBundle.getString("DATA_LOAD"));
    }

    public void displayMessage(String errorMessage, String windowTitle, String windowLabel) {
        Dialogs.showErrorDialog(new Stage(), errorMessage,
                windowTitle,
                windowLabel);
    }

    public void displayErrorMessage(String errorMessage) {
        Dialogs.showErrorDialog(new Stage(), errorMessage,
                resourceBundle.getString("ERROR"),
                resourceBundle.getString("ERROR"));
    }

}

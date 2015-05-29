package sk.insomnia.rowingRace.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.data.DataProcessor;
import sk.insomnia.rowingRace.listeners.DataChangeObserver;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataDbService;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by bobek on 6/26/2014.
 */
public class MainViewController extends AbstractController implements SettingPropagator {
    @FXML
    private RacersViewController racersViewController;
    @FXML
    private RaceViewController raceViewController;
    @FXML
    private SchoolViewController schoolViewController;

    private MainApplication mainApplication;


    @FXML
    private void handleClose() {
        DbConnection.closeAllConnections();
        System.exit(0);
    }

    @FXML
    private void handleLanguage() {
        showLanguageSelection();
    }

    public void showLanguageSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(MainViewController.class.getResource("/sk/insomnia/rowingRace/views/language.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editDisciplineCategory"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            LanguageController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            controller.initLocale(this.locale);
            controller.initResourceBundle(this.resourceBundle);
            controller.setDataProcessor(this.dataProcessor);

            controller.initializeFormData();

            dialogStage.showAndWait();
            EnumEntity l = controller.getLanguage();
            if (l != null) {
                Preferences prefs = Preferences.userNodeForPackage(MainViewController.class);
                try {
                    if (prefs.nodeExists("defaultLocale")) {
                        prefs.remove("defaultLocale");
                    }
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }

                prefs.put("defaultLocale", l.getAcronym());
                Locale _locale = Locale.forLanguageTag(l.getAcronym());
                resetView(_locale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initLocale(Locale locale) {
        this.locale = locale;
        this.racersViewController.initLocale(locale);
        this.raceViewController.initLocale(locale);
        this.schoolViewController.initLocale(locale);
        this.dataProcessor = new DataProcessor(new RowingRaceDataDbService(this.locale), this.locale);
        this.racersViewController.setDataProcessor(this.dataProcessor);
        this.raceViewController.setDataProcessor(this.dataProcessor);
        this.schoolViewController.setDataProcessor(this.dataProcessor);
        DataChangeObserver.registerSchoolListener(this.dataProcessor);
    }

    @Override
    public void initResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.racersViewController.initResourceBundle(resourceBundle);
        this.raceViewController.initResourceBundle(resourceBundle);
        this.schoolViewController.initResourceBundle(resourceBundle);
    }

    @Override
    public void initializeFormData() {
        this.racersViewController.initializeFormData();
        this.raceViewController.initializeFormData();
        this.schoolViewController.initializeFormData();
    }

    @Override
    public void resetView(Locale locale) {
        this.mainApplication.loadView(locale);
    }

    @Override
    public void setMainApplication(MainApplication application) {
        this.mainApplication = application;
    }
}

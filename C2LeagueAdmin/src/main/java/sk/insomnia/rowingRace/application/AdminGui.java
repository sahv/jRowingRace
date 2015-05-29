package sk.insomnia.rowingRace.application;

import extfx.scene.control.CalendarView;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.controller.AdminOverviewController;
import sk.insomnia.rowingRace.controller.DisciplineCategoryEditDialogController;
import sk.insomnia.rowingRace.controller.DisciplineEditDialogController;
import sk.insomnia.rowingRace.controller.IntervalEditDialogController;
import sk.insomnia.rowingRace.controller.LanguageController;
import sk.insomnia.rowingRace.controller.RootLayoutController;
import sk.insomnia.rowingRace.controller.TeamEditDialogController;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataDbService;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.Interval;
import sk.insomnia.rowingRace.so.Team;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class AdminGui extends Application {


    private Stage primaryStage;
    private BorderPane rootLayout;
    private ResourceBundle rb;
    private Locale locale;


    public AdminGui() {

    }

    @Override
    public void start(Stage primaryStage) {


        Preferences prefs = Preferences.userNodeForPackage(AdminGui.class);

        this.locale = Locale.forLanguageTag(prefs.get("defaultLocale", "sk").replaceAll("\\s", ""));
        this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.resources.bundles.ui", locale);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(rb.getString("label.app.title"));

        try {
            FXMLLoader loader = new FXMLLoader(AdminGui.class.getResource("/sk/insomnia/rowingRace/view/adminRootLayout.fxml"), this.rb);
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showRacersOverview();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutDownHook()));
        launch(args);
    }


    public void showLanguageSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(AdminGui.class.getResource("/sk/insomnia/rowingRace/view/language.fxml"), this.rb);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(rb.getString("label.dialog.editDisciplineCategory"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            LanguageController controller = loader.getController();
            controller.initResourceBundle(this.rb);
            controller.initLocale(this.locale);
            controller.setDbService(new RowingRaceDataDbService(this.locale));
            controller.setDialogStage(dialogStage);
            controller.initData();

            dialogStage.showAndWait();


            EnumEntity l = controller.getLanguage();
            if (l != null) {
                Preferences prefs = Preferences.userNodeForPackage(AdminGui.class);
                try {
                    if (prefs.nodeExists("defaultLocale")) {
                        prefs.remove("defaultLocale");
                    }
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
                prefs.put("defaultLocale", l.getAcronym());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRacersOverview() {
        try {
            RowingRaceDbFacade dbService = new RowingRaceDataDbService(locale);

            FXMLLoader loader = new FXMLLoader(AdminGui.class.getResource("/sk/insomnia/rowingRace/view/admin.fxml"), this.rb);
            AnchorPane overviewPage = (AnchorPane) loader.load();
            rootLayout.setCenter(overviewPage);
            AdminOverviewController controller = loader.getController();
            controller.setRowingRaceGui(this);
            controller.initLocale(locale);
            controller.initResourceBundle(rb);
            controller.setDbService(dbService);
            controller.initializeFormData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean showTeamEditDialog(Team team) {
        try {

            FXMLLoader loader = new FXMLLoader(AdminGui.class.getResource("/sk/insomnia/rowingRace/team/ui/view/TeamEditDialog.fxml"), this.rb);
            AnchorPane page = (AnchorPane) loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle(rb.getString("label.dialog.editOrganization"));
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TeamEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTeam(team);

            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Locale getLocale() {
        return locale;
    }


    private static class ShutDownHook implements Runnable{

        @Override
        public void run() {
            DbConnection.closeAllConnections();
        }
    }
}

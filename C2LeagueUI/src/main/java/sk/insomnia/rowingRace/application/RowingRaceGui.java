package sk.insomnia.rowingRace.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import sk.insomnia.rowingRace.connection.DbConnection;
import sk.insomnia.rowingRace.ui.controller.MainApplication;
import sk.insomnia.rowingRace.ui.controller.MainViewController;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class RowingRaceGui extends Application implements MainApplication {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(RowingRaceGui.class);
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ResourceBundle rb;
    private Locale locale;


    public RowingRaceGui() {

    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Preferences prefs = Preferences.userNodeForPackage(RowingRaceGui.class);
            this.locale = Locale.forLanguageTag(prefs.get("defaultLocale", "sk").replaceAll("\\s", ""));
            this.primaryStage = primaryStage;
            loadView(locale);
        } catch (Exception e) {
            LOG.debug(String.format("Error while loading application %s", e.getLocalizedMessage()));
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutDownHook()));
        launch(args);
    }

    @Override
    public void loadView(Locale locale) {
        try {
            this.rb = ResourceBundle.getBundle("sk.insomnia.rowingRace.bundles.ui", locale);
            this.primaryStage.setTitle(this.rb.getString("label.app.title"));

            FXMLLoader loader = new FXMLLoader(RowingRaceGui.class.getResource("/sk/insomnia/rowingRace/views/mainView.fxml"), this.rb);
            rootLayout = (AnchorPane) loader.load();
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add("/sk/insomnia/rowingRace/css/rowingRace.css");
            primaryStage.setScene(scene);

            MainViewController controller = loader.getController();
            controller.setMainApplication(this);
            controller.initLocale(locale);
            controller.initResourceBundle(this.rb);
            controller.initializeFormData();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ShutDownHook implements Runnable{
        @Override
        public void run() {
            DbConnection.closeAllConnections();
        }
    }

}

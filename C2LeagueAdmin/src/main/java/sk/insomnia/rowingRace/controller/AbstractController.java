package sk.insomnia.rowingRace.controller;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.insomnia.rowingRace.service.facade.RowingRaceDbFacade;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataDbService;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataFileService;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bobek on 6/28/2014.
 */
public abstract class AbstractController extends AbstractMessageDisplayer {

    public Locale locale;
    public RowingRaceDbFacade dbService;
    public final RowingRaceFileFacade fileService = new RowingRaceDataFileService();


    public abstract void initializeFormData();

    public void initLocale(Locale locale) {
        this.locale = locale;
    }

    public void initResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setDbService(RowingRaceDbFacade dbService) {
        this.dbService = dbService;
    }

    public void refreshTable(TableView table){
        final TableColumn< ?, ? > firstColumn = (TableColumn<?, ?>) table.getColumns().get( 0 );
        firstColumn.setVisible( false );
        new Timer().schedule( new TimerTask() { @Override public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    firstColumn.setVisible(true);
                }
            });
        }}, 100 );
    }

}

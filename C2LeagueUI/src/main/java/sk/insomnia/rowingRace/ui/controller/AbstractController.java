package sk.insomnia.rowingRace.ui.controller;

import sk.insomnia.rowingRace.data.DataProcessor;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.service.impl.RowingRaceDataFileService;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bobek on 6/28/2014.
 */
public abstract class AbstractController extends AbstractMessageDisplayer {

    public Locale locale;
    public ResourceBundle resourceBundle;
    public DataProcessor dataProcessor;
    public final RowingRaceFileFacade fileService = new RowingRaceDataFileService();

    public abstract void initLocale(Locale locale);

    public abstract void initResourceBundle(ResourceBundle resourceBundle);

    public void setDataProcessor(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    ;

    public abstract void initializeFormData();
}

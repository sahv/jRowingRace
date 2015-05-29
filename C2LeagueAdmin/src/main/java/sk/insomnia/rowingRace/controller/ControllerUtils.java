package sk.insomnia.rowingRace.controller;

import javafx.scene.control.ComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.so.RaceYear;
import sk.insomnia.rowingRace.so.RowingRace;

import java.util.List;
import java.util.Locale;

/**
 * Created by martin on 10/21/2014.
 */
public final class ControllerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerUtils.class);

    private ControllerUtils() {
        throw new AssertionError("This class was not meant to be instantiated.");
    }

    public static void setRaceYears(ComboBox cbRaceYear, List<RaceYear> raceYears, Locale locale) {
        cbRaceYear.getItems().clear();
        List<RaceYearDto> raceYearDtos = DtoUtils.raceYearsToDtos(raceYears, locale.getLanguage());
        cbRaceYear.getItems().addAll(raceYearDtos);
    }

    public static RowingRace loadRowingRace(AbstractController controller) throws Exception {
        RowingRace rowingRace = null;
        try {
            rowingRace = controller.dbService.loadRowingRace();
            if (rowingRace == null || rowingRace.getRaceYears() == null) {
                throw new Exception(controller.resourceBundle.getString("ERR_RACE_LOAD"));
            }
            return rowingRace;
        } catch (Exception e) {
            LOG.error("Can't load rowing race for controller.", e);
            throw new Exception(controller.resourceBundle.getString("ERR_RACE_LOAD"));
        }
    }

    public static void prepareRaceData(ComboBox cbRaceYear, RowingRace rowingRace, AbstractController controller) throws Exception {
        rowingRace = ControllerUtils.loadRowingRace(controller);
        ControllerUtils.setRaceYears(cbRaceYear, rowingRace.getRaceYears(), controller.locale);
    }


}

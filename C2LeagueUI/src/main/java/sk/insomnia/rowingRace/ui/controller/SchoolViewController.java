package sk.insomnia.rowingRace.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.exceptions.ExceptionType;
import sk.insomnia.rowingRace.exceptions.RowingRaceException;
import sk.insomnia.rowingRace.listeners.DataChangeObserver;
import sk.insomnia.rowingRace.so.Address;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bobek on 6/26/2014.
 */
public class SchoolViewController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(SchoolViewController.class);
    private School school;

    @FXML
    private TextField tfKey;
    @FXML
    private TextField tfSchoolName;

    @FXML
    private TextField tfSchoolCity;
    @FXML
    private TextField tfSchoolStreet;
    @FXML
    private TextField tfSchoolZip;
    @FXML
    private ComboBox<EnumEntityDto> cbSchoolCountry;

    @FXML
    private ComboBox<EnumEntityDto> cbRaceCategory;
    private List<EnumEntityDto> raceCategories;
    private List<EnumEntityDto> countries;


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
        readCountries();
        readRaceCategories();
        if (readSchoolData()) {
            setFormData();
            notifyDataChanges();
        }
    }

    private void notifyDataChanges() {
        try {
            DataChangeObserver.notifySchoolSelected(this.school);
        } catch (RowingRaceException e) {
            displayErrorMessage(resourceBundle.getString("ERR_RACE_LOAD"),
                    resourceBundle.getString("DATA_LOAD_TITLE"),
                    resourceBundle.getString("DATA_LOAD"));
        }
        if (this.school.getTeams() != null) {
            DataChangeObserver.notifyTeamChange(this.school.getTeams());
        }
    }

    private boolean schoolDataValid() {
        String errorMessage = "";


        if (tfSchoolName.getText() == null || tfSchoolName.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_SCHOOL_NAME_EMTPY") + "\n";
        }
        if (tfSchoolCity.getText() == null || tfSchoolCity.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_SCHOOL_CITY_EMTPY") + "\n";
        }
        if (tfSchoolStreet.getText() == null || tfSchoolStreet.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_SCHOOL_STREET_EMTPY") + "\n";
        }
        if (tfSchoolZip.getText() == null || tfSchoolZip.getText().length() == 0) {
            errorMessage += resourceBundle.getString("ERR_SCHOOL_ZIP_EMTPY") + "\n";
        }

        if (cbSchoolCountry.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_SCHOOL_COUNTRY_EMTPY") + "\n";
        }

        if (cbRaceCategory.getValue() == null) {
            errorMessage += resourceBundle.getString("ERR_RACE_CATEGORY_EMPTY") + "\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            return false;
        }

    }


    @FXML
    private void handleConfirmSchoolData() {
        if (schoolDataValid()) {
            if (this.school == null) {
                this.school = new School();
            }
            if (this.school.getKey() == null || this.school.getKey().equals("")) {
                try {
                    String key = dataProcessor.getSchoolCode();
                    this.school.setKey(key);
                    tfKey.setText(this.school.getKey());
                } catch (RowingRaceException e) {
                    if (e.getExceptionType().equals(ExceptionType.SQL_EXCEPTION)) {
                        String errorMessage = resourceBundle.getString("ERR_SCHOOL_KEY_LOAD");
                        displayErrorMessage(errorMessage,
                                resourceBundle.getString("ERR_DATA_LOAD"),
                                resourceBundle.getString("ERR_DATA_LOAD_TITLE"));
                    }
                    return;
                }
            }
            readFormData();
            notifyDataChanges();
            try {
                dataProcessor.saveSchoolInProcess(false);
            } catch (RowingRaceException e) {
                String errorMessage = resourceBundle.getString("ERR_SAVE_SCHOOL");
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("ERROR"));
                logger.info(e.getMessage());
            }
        }

    }

    private void readFormData() {
        this.school.setName(tfSchoolName.getText());
        if (this.school.getAddress() == null) {
            this.school.setAddress(new Address());
        }
        this.school.getAddress().setCity(tfSchoolCity.getText());
        this.school.getAddress().setStreet(tfSchoolStreet.getText());
        this.school.getAddress().setZip(tfSchoolZip.getText());
        this.school.getAddress().setCountry(cbSchoolCountry.getValue());
        if (school.getRaceCategories() == null) {
            school.setRaceCategories(new ArrayList<EnumEntity>());
        }
        this.school.getRaceCategories().add(cbRaceCategory.getValue());
        this.school.setKey(tfKey.getText());
    }

    private void readRaceCategories() {
        try {
            raceCategories = dataProcessor.getCodeTable(RowingRaceCodeTables.CT_RACE_CATEGORY);
            if (raceCategories != null) {
                this.fileService.saveOrUpdate(raceCategories, RowingRaceCodeTables.CT_RACE_CATEGORY);
            }
        } catch (Exception e) {
            logger.error(String.format("Can't load data from DB, cause : %s ", e.getMessage()));
        } finally {
            try {
                if (raceCategories == null) {
                    raceCategories = this.fileService.loadCodeTable(RowingRaceCodeTables.CT_RACE_CATEGORY);
                }
                cbRaceCategory.getItems().clear();
                cbRaceCategory.getItems().addAll(raceCategories);
            } catch (Exception e) {
                Object[] params = {RowingRaceCodeTables.CT_RACE_CATEGORY};
                String errorMessage = MessageFormat.format(resourceBundle.getString("ERR_CODE_TABLE_DATA_LOAD"), params) + "\n";
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("ERR_SAVE_SCHOOL"),
                        resourceBundle.getString("ERR_SAVE_SCHOOL"));
                logger.error(e.getMessage());
            }
        }
    }

    private boolean readSchoolData() {
        if (this.school == null) {
            try {
                this.school = this.fileService.loadSchool(null);
            } catch (Exception e) {
                String errorMessage = resourceBundle.getString("ERR_SCHOOL_DATA_LOAD");
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("ERR_DATA_LOAD"),
                        resourceBundle.getString("ERR_DATA_LOAD_TITLE"));
                logger.debug(e.getMessage());
            }
        }
        if (school != null) {
            return true;
        }
        return false;
    }

    private void setFormData() {
        tfKey.setText(this.school.getKey());
        if (school.getAddress() != null) {
            tfSchoolCity.setText(this.school.getAddress().getCity());
            tfSchoolName.setText(this.school.getName());
            tfSchoolStreet.setText(this.school.getAddress().getStreet());
            tfSchoolZip.setText(this.school.getAddress().getZip());
            cbSchoolCountry.getSelectionModel().select(DtoUtils.findBySo(cbSchoolCountry.getItems(), school.getAddress().getCountry()));
        }
        if (school.getRaceCategories() != null && school.getRaceCategories().size() > 0) {
            cbRaceCategory.getSelectionModel().select(DtoUtils.findBySo(cbRaceCategory.getItems(), school.getRaceCategories().get(0)));
        }
    }

    private void readCountries() {
        try {
            countries = dataProcessor.getCodeTable(RowingRaceCodeTables.CT_COUNTRIES);
        } catch (Exception e1) {
            logger.debug("Can't load countries data from DB.", e1.getMessage());
        } finally {
            try {
                if (countries != null) {
                    this.fileService.saveOrUpdate(countries, RowingRaceCodeTables.CT_COUNTRIES);
                }
                if (countries == null) {
                    countries = this.fileService.loadCodeTable(RowingRaceCodeTables.CT_COUNTRIES);
                }
                cbSchoolCountry.getItems().clear();
                cbSchoolCountry.getItems().addAll(countries);
            } catch (Exception e) {
                Object[] params = {RowingRaceCodeTables.CT_COUNTRIES};
                String errorMessage = MessageFormat.format(resourceBundle.getString("ERR_CODE_TABLE_DATA_LOAD"), params) + "\n";
                displayErrorMessage(errorMessage,
                        resourceBundle.getString("ERR_DATA_LOAD"),
                        resourceBundle.getString("ERR_DATA_LOAD_TITLE"));
                logger.debug(ExceptionUtils.exceptionAsString(e));
            }
        }
    }


}

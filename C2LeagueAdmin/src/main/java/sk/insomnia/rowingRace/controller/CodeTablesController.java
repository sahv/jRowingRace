package sk.insomnia.rowingRace.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.listener.DataChangeObserver;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.LanguageMutation;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by bobek on 6/29/2014.
 */
public class CodeTablesController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(CodeTablesController.class);
    public static final String ERR_ACRONYM_EMTPY = "ERR_ACRONYM_EMTPY";
    public static final String ERR_NO_RACE_CATEGORY_SELECTED = "ERR_NO_RACE_CATEGORY_SELECTED";

    @FXML
    private TableView tbCodeTable;

    @FXML
    private TableColumn tcAcronym;

    @FXML
    private TableColumn tcExpression;

    @FXML
    private TextField tfAcronym;

    @FXML
    private TextField tfExpression;

    @FXML
    private ComboBox<EnumEntityDto> cbLanguage;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnAdd;

    @FXML
    private TableView tbLanguageMutations;

    @FXML
    private TableColumn tcLanguage;

    @FXML
    private Button bntNewRaceCategory;

    @FXML
    private Button btnAddCategory;

    @FXML
    private Button deleteCategory;

    @FXML
    private ComboBox<RowingRaceCodeTables> cbCodeTable;

    private ObservableList<EnumEntityDto> codeTableValues = FXCollections.observableArrayList();
    private ObservableList<LanguageMutation> languageMutations = FXCollections.observableArrayList();

    private EnumEntityDto actualEnumEntityDto;
    private LanguageMutation mutationInProcess;

    private RowingRaceCodeTables selectedCodeTable;

    public CodeTablesController() {
    }


    @FXML
    private void initialize() {
        initializeFormControls();
    }

    private void initializeFormControls() {
        initializeRaceCategoriesTable();
        initializeLanguageMutationsTable();
        cbLanguage.getItems().clear();

    }

    private void initializeLanguageMutationsTable() {
        tbLanguageMutations.setItems(languageMutations);
        tbLanguageMutations.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbLanguageMutations.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LanguageMutation>() {
            @Override
            public void changed(ObservableValue<? extends LanguageMutation> observable,
                                LanguageMutation oldValue, LanguageMutation newValue) {
                showMutationDetails(newValue);
            }
        });
        tcLanguage.setCellValueFactory(new PropertyValueFactory<LanguageMutation, String>("language"));
        tcExpression.setCellValueFactory(new PropertyValueFactory<LanguageMutation, String>("value"));
    }

    private void showMutationDetails(LanguageMutation newValue) {
        if (newValue == null) {
            return;
        }
        mutationInProcess = newValue;
        setMutationForm(mutationInProcess);
    }

    private void initializeRaceCategoriesTable() {
        tbCodeTable.setItems(codeTableValues);
        tbCodeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbCodeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EnumEntityDto>() {

            @Override
            public void changed(ObservableValue<? extends EnumEntityDto> observable,
                                EnumEntityDto oldValue, EnumEntityDto newValue) {
                showRaceCategoryDetails(newValue);
            }
        });
        tcAcronym.setCellValueFactory(new PropertyValueFactory<EnumEntitySO, String>("acronym"));
        tcExpression.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EnumEntitySO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<EnumEntitySO, String> raceResultDtoStringCellDataFeatures) {
                List<LanguageMutation> mutations = raceResultDtoStringCellDataFeatures.getValue().getLanguageMutations();
                SimpleStringProperty retval = new SimpleStringProperty();
                for (LanguageMutation mutation : mutations) {
                    if (mutation.getLanguage().equals(locale.getLanguage())) {
                        retval.setValue(mutation.getValue());
                        break;
                    }
                }
                if (retval.getValue().isEmpty()) {
                    if (mutations.size() > 0) {
                        retval.setValue(mutations.get(0).getValue());
                    }
                }
                return retval;
            }
        });
    }

    private void showRaceCategoryDetails(EnumEntityDto enumEntityDto) {
        if (enumEntityDto == null) {
            return;
        }
        actualEnumEntityDto = enumEntityDto;
        resetForm();
        this.languageMutations.clear();
        if (actualEnumEntityDto.getLanguageMutations() != null && actualEnumEntityDto.getLanguageMutations().size() > 0) {
            this.languageMutations.addAll(actualEnumEntityDto.getLanguageMutations());
        }
        tfAcronym.setText(actualEnumEntityDto.getAcronym());
    }

    private void setMutationForm(LanguageMutation mutation) {
        cbLanguage.getSelectionModel().select(DtoUtils.findBySo(cbLanguage.getItems(), mutation.getLanguage()));
        tfExpression.setText(mutation.getValue());
    }

    @FXML
    private void onDelete() {
        EnumEntitySO raceCategory = getEntityFromCategoriesTable();
        LanguageMutation languageMutation = (LanguageMutation) tbLanguageMutations.getSelectionModel().getSelectedItem();
        raceCategory.getLanguageMutations().remove(languageMutation);
        tbLanguageMutations.getItems().remove(languageMutation);
        try {
            dbService.deleteMutation(languageMutation);
        } catch (SQLException | ConnectivityException e) {
            logger.error(String.format("Error occured while deleting language mutation %s", languageMutation), e);
            displayErrorCTSAve();
        }
    }

    @FXML
    private void onDeleteCategory() {
        if (!recordInProgressEmpty()) {
            try {
                dbService.deleteCodeTableValue(actualEnumEntityDto, selectedCodeTable);
                tbCodeTable.getItems().remove(actualEnumEntityDto);
                actualEnumEntityDto = null;
            } catch (ConnectivityException | SQLException e) {
                logger.error(String.format("Error occured while deleting value %s", actualEnumEntityDto.getAcronym()), e);
                displayErrorCTSAve();
            }
            resetForm();
            DataChangeObserver.notifyCTSelected(codeTableValues, selectedCodeTable);
        }
    }

    @FXML
    private void onCodeTableSelected() {
        this.selectedCodeTable = cbCodeTable.getSelectionModel().getSelectedItem();
        intializeCodeTableData();
    }

    private boolean recordInProgressEmpty() {
        if (this.actualEnumEntityDto == null) {
            errorMessageBase(selectedCodeTable, ERR_NO_RACE_CATEGORY_SELECTED);
            return true;
        }
        return false;
    }

    @FXML
    private void onAddCategory() {
        if (actualEnumEntityDto == null) {
            actualEnumEntityDto = new SimpleEnumEntityDto();
        }
        if (tfAcronym.getText() != null && !tfAcronym.getText().isEmpty()) {
            actualEnumEntityDto.setAcronym(tfAcronym.getText());
        } else {
            errorMessageBase(selectedCodeTable, ERR_ACRONYM_EMTPY);
        }
        if (actualEnumEntityDto.getId() == null) {
            codeTableValues.add(actualEnumEntityDto);
        }
        saveRaceCategory(actualEnumEntityDto);
        resetForm();
        DataChangeObserver.notifyCTSelected(codeTableValues, selectedCodeTable);
        refreshTable(tbCodeTable);
    }

    private EnumEntitySO getEntityFromCategoriesTable() {
        return (EnumEntitySO) tbCodeTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onAdd() {
        if (mutationRecordValid()) {
            boolean wasInsert = false;
            if (mutationInProcess == null) {
                mutationInProcess = new LanguageMutation();
            }

            if (mutationInProcess.getId() == null) {
                wasInsert = true;
            }
            readMutationFromForm(mutationInProcess);
            try {
                if (wasInsert) {
                    languageMutations.add(mutationInProcess);
                    EnumEntitySO raceCategory = getEntityFromCategoriesTable();
                    raceCategory.getLanguageMutations().add(mutationInProcess);
                    dbService.addMutationToKey(raceCategory.getId(), mutationInProcess);
                } else {
                    dbService.updateMutation(mutationInProcess);
                }
            } catch (ConnectivityException | SQLException e) {
                logger.error("Error saving or updating mutation", e);
                displayErrorCTSAve();
            }

            resetMutationForm();
            refreshTable(tbCodeTable);
            refreshTable(tbLanguageMutations);
            this.mutationInProcess = null;
        }
    }

    private void resetForm() {
        resetRaceCategoryForm();
        resetMutationForm();
    }

    private void resetRaceCategoryForm() {
        tfAcronym.setText("");
    }

    private void resetMutationForm() {
        tfExpression.setText("");
        cbLanguage.getSelectionModel().selectFirst();
    }

    private void saveRaceCategory(EnumEntityDto raceCategory) {
        try {
            dbService.saveCodeTableValue(raceCategory, selectedCodeTable);
        } catch (ConnectivityException | SQLException e) {
            logger.error("Error while saving race category data", e);
            displayErrorCTSAve();
        }
    }

    private void displayErrorCTSAve() {
        errorMessageBase(selectedCodeTable, ERR_CODE_TABLE_DATA_SAVE);
    }

    private boolean mutationRecordValid() {
        if (!tfExpression.getText().isEmpty() && cbLanguage.getSelectionModel().getSelectedItem() != null) {
            return true;
        }
        return false;
    }

    private void readMutationFromForm(LanguageMutation languageMutation) {
        languageMutation.setValue(tfExpression.getText());
        languageMutation.setLanguage(cbLanguage.getValue());
        languageMutation.setCodeTable(selectedCodeTable);
    }

    @FXML
    private void onNewRaceCategory() {
        resetForm();
        actualEnumEntityDto = new SimpleEnumEntityDto();
    }

    @FXML
    private void onNewMutation() {
        resetMutationForm();
        this.mutationInProcess = null;
    }

    @Override
    public void initializeFormData() {
        this.cbCodeTable.getItems().clear();
        this.cbCodeTable.getItems().addAll(RowingRaceCodeTables.values());
        initializeLanguagesData();
    }

    private void initializeLanguagesData() {
        this.cbLanguage.getItems().clear();
        try {
            List<EnumEntityDto> languages = dbService.getCodeTable(RowingRaceCodeTables.CT_LANGUAGES, this.locale);
            this.cbLanguage.getItems().addAll(languages);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            e.printStackTrace();
            displayErrorCTSAve();
        }
    }

    private void displayErrorCTLoad() {
        errorMessageBase(selectedCodeTable, ERR_CODE_TABLE_DATA_LOAD);
    }


    private void intializeCodeTableData() {
        this.languageMutations.clear();
        this.codeTableValues.clear();
        if (selectedCodeTable == null){
            return;
        }
        try {
            List<EnumEntityDto> ctValues = dbService.getCodeTable(selectedCodeTable, this.locale);
            this.codeTableValues.addAll(ctValues);
            DataChangeObserver.notifyCTSelected(ctValues, selectedCodeTable);
        } catch (DtoUtils.DtoUtilException | ConnectivityException | SQLException e) {
            logger.error("Error loading code table values", e);
            displayErrorCTLoad();
        }
    }

}

package sk.insomnia.rowingRace.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dataStore.NoDataForKeyException;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.markers.RaceCategory;
import sk.insomnia.rowingRace.markers.TeamCategory;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by bobek on 8/4/2014.
 */
public class MasterSlaveCodeTableController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(MasterSlaveCodeTableController.class);

    @FXML
    ComboBox<RowingRaceCodeTables> cbMasterCodeTable;
    @FXML
    ComboBox<RowingRaceCodeTables> cbSlaveCodeTable;
    @FXML
    TableView<EnumEntityDto> tbMasterCodeTable;
    @FXML
    TableView<EnumEntityDto> tbSlaveCodeTable;
    @FXML
    TableColumn tcMasterCodeTableValue;
    @FXML
    TableColumn tcSlaveCodeTableValues;

    private static final ObservableList<EnumEntityDto> masterCodeTableValues = FXCollections.observableArrayList();
    private static final ObservableList<EnumEntityDto> slaveCodeTableValues = FXCollections.observableArrayList();


    @FXML

    private void initialize() {

        tbMasterCodeTable.setItems(masterCodeTableValues);
        tcMasterCodeTableValue.setCellValueFactory(new PropertyValueFactory<EnumEntityDto, String>("value"));
        tbMasterCodeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EnumEntityDto>() {
            @Override
            public void changed(ObservableValue<? extends EnumEntityDto> observableValue, EnumEntityDto oldValue, EnumEntityDto newValue) {
                showCodeTableSlaveValues(newValue);
            }
        });

        tbSlaveCodeTable.setItems(slaveCodeTableValues);
        tbSlaveCodeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tcSlaveCodeTableValues.setCellValueFactory(new PropertyValueFactory<SimpleEnumEntityDto, String>("value"));

        cbMasterCodeTable.getItems().clear();
        cbMasterCodeTable.getItems().add(RowingRaceCodeTables.CT_RACE_CATEGORY);
        cbMasterCodeTable.getSelectionModel().selectFirst();

        cbSlaveCodeTable.getItems().clear();
        cbSlaveCodeTable.getItems().add(RowingRaceCodeTables.CT_TEAM_CATEGORIES);
        cbSlaveCodeTable.getSelectionModel().selectFirst();
    }

    @FXML
    private void onSave() {
        if (tbSlaveCodeTable.getSelectionModel().getSelectedItems().size() <= 0) {
            return;
        }
        EnumEntityDto masterValue = tbMasterCodeTable.getSelectionModel().getSelectedItem();
        try {
            dbService.saveSlaveValues(masterValue.getId(),
                    RowingRaceCodeTables.CT_RACE_CATEGORY,
                    tbSlaveCodeTable.getSelectionModel().getSelectedItems(),
                    RowingRaceCodeTables.CT_TEAM_CATEGORIES);
        } catch (SQLException e) {
            LOG.error("Can't save slave values for key {} of codetable {}", masterValue.getId(), RowingRaceCodeTables.CT_RACE_CATEGORY, e);
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }
    }

    private final void showCodeTableSlaveValues(EnumEntityDto masterCodeTableValue) {
        if (masterCodeTableValue == null) {
            return;
        }
        List<EnumEntityDto> selectedValues = null;
        try {
            selectedValues = DtoUtils.listOfLanguageSpecificValues(dbService.loadValuesForCodeTable(masterCodeTableValue, RowingRaceCodeTables.CT_RACE_CATEGORY, RowingRaceCodeTables.CT_TEAM_CATEGORIES), this.locale.getLanguage());
        } catch (DtoUtils.DtoUtilException | SQLException e) {
            LOG.error("Can't load slave values for code table {} with key {}", RowingRaceCodeTables.CT_RACE_CATEGORY, masterCodeTableValue.getId());
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }
        selectValues(selectedValues);
        refreshTable(tbSlaveCodeTable);
    }

    private void selectValues(List<EnumEntityDto> selectedValues) {
        tbSlaveCodeTable.getSelectionModel().clearSelection();
        for (EnumEntityDto enumEntityDto : selectedValues) {
            for (EnumEntityDto value : slaveCodeTableValues) {
                if (value.getId().longValue() == enumEntityDto.getId().longValue()) {
                    tbSlaveCodeTable.getSelectionModel().select(value);
                    break;
                }
            }

        }
    }

    @Override
    public void initializeFormData() {
        try {
            masterCodeTableValues.addAll(dbService.getCodeTable(cbMasterCodeTable.getValue(), this.locale));
        } catch (DtoUtils.DtoUtilException| SQLException | ConnectivityException e) {
            LOG.debug("Can't read race category data from data store.");
            errorMessageBase(RowingRaceCodeTables.CT_RACE_CATEGORY, ERR_CODE_TABLE_DATA_LOAD);
        }

        try {
            slaveCodeTableValues.addAll(dbService.getCodeTable(cbSlaveCodeTable.getValue(), this.locale));
        } catch (DtoUtils.DtoUtilException| SQLException | ConnectivityException e) {
            LOG.debug("Can't read race category data from data store.");
            errorMessageBase(RowingRaceCodeTables.CT_TEAM_CATEGORIES, ERR_CODE_TABLE_DATA_LOAD);
        }
    }
}

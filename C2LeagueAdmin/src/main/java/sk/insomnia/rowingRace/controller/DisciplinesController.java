package sk.insomnia.rowingRace.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.dataStore.CommonDataStore;
import sk.insomnia.rowingRace.dataStore.NoDataForKeyException;
import sk.insomnia.rowingRace.dto.DisciplineCategoryDto;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.markers.DisciplineCategory;
import sk.insomnia.rowingRace.service.facade.ConnectivityException;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.Interval;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobek on 7/16/2014.
 */
public class DisciplinesController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(DisciplinesController.class);

    @FXML
    private ComboBox<DisciplineCategoryDto> cbDisciplineCategory;

    @FXML
    private TableView<Discipline> disciplinesTable;


    @FXML
    private TableColumn<Discipline, String> disciplineNameColumn;

    @FXML
    private TableView<Interval> intervalsTable;
    @FXML
    private TableColumn<Interval, String> intervalOrderColumn;
    @FXML
    private TableColumn<Interval, String> intervalWorkoutColumn;
    @FXML
    private TableColumn<Interval, String> intervalRestColumn;
    @FXML
    private TableColumn<Interval, String> intervalDimensionColumn;

    @FXML
    private void initialize() {
        cbDisciplineCategory.getItems().clear();
        intervalOrderColumn.setCellValueFactory(new PropertyValueFactory<Interval, String>("intervalNumber"));
        intervalWorkoutColumn.setCellValueFactory(new PropertyValueFactory<Interval, String>("workoutValue"));
        intervalRestColumn.setCellValueFactory(new PropertyValueFactory<Interval, String>("restValue"));
        intervalDimensionColumn.setCellValueFactory(new PropertyValueFactory<Interval, String>("dimension"));
        intervalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        disciplineNameColumn.setCellValueFactory(new PropertyValueFactory<Discipline, String>("name"));
        disciplinesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        disciplinesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Discipline>() {
            public void changed(ObservableValue<? extends Discipline> observable,
                                Discipline oldValue, Discipline newValue) {
                refreshIntervalsTable();
            }
        });
    }

    @FXML
    private void handleNewDisciplineCategory() {
        DisciplineCategoryDto _disciplineCategory = new DisciplineCategoryDto();

        boolean okClicked = showDisciplineCategoryEditDialog(_disciplineCategory);
        if (okClicked) {
            try {
                List<DisciplineCategoryDto> disciplineCategoryDtos = (List<DisciplineCategoryDto>) CommonDataStore.getValuesForClass(DisciplineCategory.class);
                //TODO : verify if this is correctly stored in DS
                disciplineCategoryDtos.add(_disciplineCategory);
                initDisciplineCategoriesCB();
            } catch (NoDataForKeyException e) {
                logger.info("No data found for discipline cateogries on data store.");
                //TODO : display error message here
            }
            saveDisciplineCategories(_disciplineCategory);
            refreshDisciplinesTable();
        }
    }

    @FXML
    private void handleDisciplineCategoryChange() {
        refreshDisciplinesTable();
    }

    @FXML
    private void handleEditDiscipline() {
        Discipline _discipline = disciplinesTable.getSelectionModel().getSelectedItem();
        if (_discipline != null) {
            boolean okClicked = showDisciplineEditDialog(_discipline);
            if (okClicked) {
                String errorMessage = null;
                try {
                    this.dbService.saveDiscipline(_discipline);
                } catch (ConnectivityException | SQLException e) {
                    errorMessage = resourceBundle.getString("ERR_DISCIPLINE_SAVE");
                    logger.error("Error saving discipline data", ExceptionUtils.exceptionAsString(e));
                }
                if (errorMessage != null) {
                    Dialogs.showErrorDialog(new Stage(), errorMessage,
                            resourceBundle.getString("DATA_SAVE"),
                            resourceBundle.getString("DATA_SAVE"));
                } else {
                    refreshDisciplinesTable();
                }
            }
        }
    }

    @FXML
    private void handleDeleteDiscipline() {
        Discipline _discipline = disciplinesTable.getSelectionModel().getSelectedItem();
        if (_discipline != null) {
            cbDisciplineCategory.getValue().getDisciplines().remove(_discipline);
            refreshDisciplinesTable();
            saveDisciplineCategories(cbDisciplineCategory.getValue());
        } else {
            Dialogs.showWarningDialog(new Stage(),
                    resourceBundle.getString("WARN_SELECT_PERSON"),
                    resourceBundle.getString("WARN_SELECT_PERSON_TITLE"),
                    resourceBundle.getString("WARN_NO_SELECTION"));
        }
    }


    @FXML
    private void handleNewDiscipline() {

        if (cbDisciplineCategory.getValue() == null) {
            String errorMessage = resourceBundle.getString("ERR_CATEGORY_EMTPY");
            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
        } else {
            Discipline _discipline = new Discipline();
            boolean okClicked = showDisciplineEditDialog(_discipline);
            if (okClicked) {
                if (cbDisciplineCategory.getValue().getDisciplines() == null) {
                    cbDisciplineCategory.getValue().setDisciplines(new ArrayList<Discipline>());
                }
                cbDisciplineCategory.getValue().getDisciplines().add(_discipline);
                if (okClicked) {
                    String errorMessage = null;
                    try {
                        this.dbService.addDisciplineToCategory(_discipline, cbDisciplineCategory.getValue().getId());
                    } catch (ConnectivityException | SQLException e) {
                        errorMessage = resourceBundle.getString("ERR_DISCIPLINE_SAVE");
                        logger.error("Error while adding discipline to category.", e);
                    }
                    if (errorMessage != null) {
                        Dialogs.showErrorDialog(new Stage(), errorMessage,
                                resourceBundle.getString("DATA_SAVE"),
                                resourceBundle.getString("DATA_SAVE"));
                    } else {
                        refreshDisciplinesTable();
                    }
                }
            }
        }
    }

    @FXML
    private void handleDeleteInterval() {
        if (intervalsTable.getSelectionModel().getSelectedItem() != null) {
            Discipline _discipline = disciplinesTable.getSelectionModel().getSelectedItem();
            _discipline.getIntervals().remove(intervalsTable.getSelectionModel().getSelectedItem());
            saveDisciplineCategories(cbDisciplineCategory.getValue());
            refreshIntervalsTable();
        }
    }

    @FXML
    private void handleNewInterval() {
        Interval _interval = new Interval();
        boolean okClicked = showIntervalEditDialog(_interval);
        if (okClicked) {
            Discipline _discipline = disciplinesTable.getSelectionModel().getSelectedItem();
            if (_discipline.getIntervals() == null) {
                _discipline.setIntervals(new ArrayList<Interval>());
            }
            _interval.setIntervalNumber(Integer.toString((_discipline.getIntervals().size() + 1)));
            _discipline.getIntervals().add(_interval);
            String errorMessage = null;
            try {
                this.dbService.addIntervalToDiscipline(_interval, _discipline.getId());
            } catch (ConnectivityException | SQLException e) {
                errorMessage = resourceBundle.getString("ERR_INTERVAL_SAVE");
                logger.error("Error while saving interval addition to discipline.", e);
            }
            if (errorMessage != null) {
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            } else {
                refreshIntervalsTable();
            }
        }
    }

    public boolean showIntervalEditDialog(Interval interval) {
        try {

            FXMLLoader loader = new FXMLLoader(DisciplinesController.class.getResource("/sk/insomnia/rowingRace/view/IntervalEditDialog.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editInterval"));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(new Stage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            IntervalEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setLocale(this.locale);
            controller.initializeData();
            try {
                controller.setInterval(interval);
            } catch (DtoUtils.DtoUtilException | IllegalAccessException | InstantiationException e) {
                logger.error("Can't set interval due to {}", e);
            }
            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleEditInterval() {
        Interval _interval = intervalsTable.getSelectionModel().getSelectedItem();
        boolean okClicked = showIntervalEditDialog(_interval);
        if (okClicked) {
            String errorMessage = null;
            try {
                this.dbService.saveInterval(_interval);
            } catch (ConnectivityException | SQLException e) {
                errorMessage = resourceBundle.getString("ERR_INTERVAL_SAVE");
                logger.error("Error while saving interval data.", e);
            }
            if (errorMessage != null) {
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("DATA_SAVE"),
                        resourceBundle.getString("DATA_SAVE"));
            } else {
                refreshIntervalsTable();
            }
        }
    }

    private void saveDisciplineCategories(DisciplineCategoryDto _disciplineCategory) {
        try {
            if (this.dbService.isConnectivity()) {
                this.dbService.saveDisciplineCategory(_disciplineCategory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshDisciplinesTable() {
        if (cbDisciplineCategory.getValue() != null) {
            int selectedIndex = disciplinesTable.getSelectionModel().getSelectedIndex();
            disciplinesTable.setItems(null);
            disciplinesTable.layout();
            if (cbDisciplineCategory.getValue().getDisciplines() != null) {
                ObservableList<Discipline> _disciplines = FXCollections.observableArrayList();
                _disciplines.addAll(cbDisciplineCategory.getValue().getDisciplines());
                disciplinesTable.setItems(_disciplines);
                disciplinesTable.getSelectionModel().select(selectedIndex);
            } else {
                String errorMessage = resourceBundle.getString("ERR_NO_DISCIPLINES");
                Dialogs.showErrorDialog(new Stage(), errorMessage,
                        resourceBundle.getString("INFO_CORRECT_FIELDS"),
                        resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
            }
        }
    }

    private void refreshIntervalsTable() {
        if (disciplinesTable.getSelectionModel().getSelectedIndex() >= 0) {
            int selectedIndex = intervalsTable.getSelectionModel().getSelectedIndex();

            ObservableList<Interval> _intervalsData = FXCollections.observableArrayList();
            intervalsTable.setItems(null);
            intervalsTable.layout();
            if (disciplinesTable.getSelectionModel().getSelectedItem().getIntervals() != null) {
                _intervalsData.addAll(disciplinesTable.getSelectionModel().getSelectedItem().getIntervals());

                intervalsTable.setItems(_intervalsData);
                if (selectedIndex >= 0) {
                    intervalsTable.getSelectionModel().select(selectedIndex);
                }
            }
        } else {
            String errorMessage = resourceBundle.getString("ERR_CATEGORY_EMTPY");
            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
        }

    }

    private void initDisciplineCategoriesCB() throws NoDataForKeyException {
        cbDisciplineCategory.getItems().clear();
        cbDisciplineCategory.getItems().addAll((java.util.Collection<DisciplineCategoryDto>) CommonDataStore.getValuesForClass(DisciplineCategory.class));
    }


    @Override
    public void initializeFormData() {
        try {
            initDisciplineCategoriesCB();
        } catch (Exception e1) {
            String errorMessage = resourceBundle.getString("ERR_DISCIPLINE_CATEGORIES_DATA_LOAD");
            Dialogs.showErrorDialog(new Stage(), errorMessage,
                    resourceBundle.getString("ERR_DATA_LOAD"),
                    resourceBundle.getString("ERR_DATA_LOAD_TITLE"));
        }
    }


    public boolean showDisciplineCategoryEditDialog(DisciplineCategoryDto disciplineCategory) {
        try {
            FXMLLoader loader = new FXMLLoader(DisciplinesController.class.getResource("/sk/insomnia/rowingRace/view/disciplineCategoryEditDialog.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editDisciplineCategory"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(new Stage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DisciplineCategoryEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDisciplineCategory(disciplineCategory);

            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showDisciplineEditDialog(Discipline discipline) {
        try {
            FXMLLoader loader = new FXMLLoader(DisciplinesController.class.getResource("/sk/insomnia/rowingRace/view/disciplineEditDialog.fxml"), this.resourceBundle);
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceBundle.getString("label.dialog.editDiscipline"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(new Stage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DisciplineEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDiscipline(discipline);
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            return false;
        }
    }


}

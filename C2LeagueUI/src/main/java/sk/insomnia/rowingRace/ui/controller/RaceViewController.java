package sk.insomnia.rowingRace.ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.communication.PMOperations;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.DtoUtils;
import sk.insomnia.rowingRace.dto.RaceYearDto;
import sk.insomnia.rowingRace.exceptions.RowingRaceException;
import sk.insomnia.rowingRace.listeners.DataChangeObserver;
import sk.insomnia.rowingRace.listeners.RaceListener;
import sk.insomnia.rowingRace.listeners.TeamsListener;
import sk.insomnia.rowingRace.remote.PMDataHandler;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.PerformanceParameter;
import sk.insomnia.rowingRace.so.Performances;
import sk.insomnia.rowingRace.so.RaceRound;
import sk.insomnia.rowingRace.so.Racer;
import sk.insomnia.rowingRace.so.RacerInterval;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.Team;
import sk.insomnia.rowingRace.timer.TimeDisplay;
import sk.insomnia.tools.exceptionUtils.ExceptionUtils;
import sk.insomnia.tools.timeUtil.RowingRaceTimeUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bobek on 6/26/2014.
 */
public class RaceViewController extends AbstractController implements TeamsListener, TimeDisplay, RaceListener {

    private static final Logger logger = LoggerFactory.getLogger(RaceViewController.class);

    @FXML
    private Label rowingCadenceLabel;
    @FXML
    private Label lbRowingPaceTime;

    @FXML
    private ComboBox<RaceYearDto> cbRaceRaceYear;
    @FXML
    private ComboBox<RaceRound> cbRaceRaceRound;
    @FXML
    private ComboBox<Team> cbRaceTeam;
    @FXML
    private Label lblRaceDisciplineDescription;
    @FXML
    private Label lblWorkStatus;
    @FXML
    private Label lblDeviceConnected;
    @FXML
    private Label lblWorkoutDistance;
    @FXML
    private Label lblRowingTime;
    @FXML
    private Button btnSavePerformance;
    @FXML
    private Button bntStartRace;
    @FXML
    private TableView<RacerInterval> raceIntervalsTable;
    @FXML
    private TableColumn<RacerInterval, String> ritIntervalNumber;
    @FXML
    private TableColumn<RacerInterval, Racer> ritRacer;
    @FXML
    private TableColumn<RacerInterval, String> ritTime;


    private final PMDataHandler remote = new PMDataHandler();
    private int workState = -1;
    private int deviceCount = 0;

    private boolean raceRunning = false;

    private int raceDistance;
    private int relaySplit;
    private boolean raceOnDistance = true;
    private int relayRounds = 0;
    private Performances performances;
    private Performance performance;
    private final ObservableList<Racer> teamMembers = FXCollections.observableArrayList();
    private RowingRace rowingRace;
    private static final int maxRacersInRace = 4;
    private static final int minRacersInRace = 4;
    private final ObservableList<RacerInterval> _intervalsData = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        DataChangeObserver.registerTeamsListener(this);
        DataChangeObserver.registerRaceListener(this);

        cbRaceRaceRound.getItems().clear();
        cbRaceTeam.getItems().clear();

        try {
            this.connectivityWatcher.setCycleCount(Timeline.INDEFINITE);
            this.connectivityWatcher.play();
        } catch (Exception e) {
            this.connectivityWatcher.stop();
        }


    }

    private void initRaceIntervalTable() {
        raceIntervalsTable.setItems(_intervalsData);
        raceIntervalsTable.setPlaceholder(new Text(resourceBundle.getString("NO_DATA_IN_TABLE")));
        ritIntervalNumber.setCellValueFactory(new PropertyValueFactory<RacerInterval, String>("intervalNumber"));
        ritRacer.setCellValueFactory(new PropertyValueFactory<RacerInterval, Racer>("fullName"));
        ritRacer.setEditable(true);
        ritRacer.setCellFactory(new Callback<TableColumn<RacerInterval, Racer>, TableCell<RacerInterval, Racer>>() {
            @Override
            public TableCell<RacerInterval, Racer> call(
                    TableColumn<RacerInterval, Racer> arg0) {
                ComboBoxTableCell cb = new ComboBoxTableCell<RacerInterval, Racer>();
                cb.getItems().addAll(teamMembers);
                return cb;
            }

            ;
        });
        ritRacer.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RacerInterval, Racer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RacerInterval, Racer> t) {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setRacer(t.getNewValue());
            }

        });
        ritTime.setCellValueFactory(new PropertyValueFactory<RacerInterval, String>("time"));
        raceIntervalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @Override
    public void initLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void initResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initRaceIntervalTable();
    }


    @Override
    public void onTeamsChange(List<Team> teamList) {
        this.cbRaceTeam.getItems().clear();
        this.cbRaceTeam.getItems().addAll(teamList);
    }

    @Override
    public void initializeFormData() {
        setupPerformance();
    }

    @FXML
    private void handleCbRaceYearAction() {
        cbRaceRaceRound.getItems().clear();
        if (cbRaceRaceYear.getValue() != null &&
                cbRaceRaceYear.getValue().getRounds() != null) {
            List<RaceRound> rounds = new ArrayList<RaceRound>();
            Calendar today = GregorianCalendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            for (RaceRound rr : cbRaceRaceYear.getValue().getRounds()) {
                if (rr.getBegin().getTime() <= today.getTime().getTime() && rr.getEnd().getTime() >= today.getTime().getTime()) {
                    rounds.add(rr);
                }
            }
            cbRaceRaceRound.getItems().addAll(rounds);
        }
    }

    @FXML
    private void handleRaceRound() {
        if (cbRaceRaceRound.getValue() != null) {
            lblRaceDisciplineDescription.setText(cbRaceRaceRound.getValue().getDescription());
        }
    }

    @FXML
    private void handleRaceTeamAction() {
        if (cbRaceTeam.getValue() == null) {
            String errorMessage = resourceBundle.getString("ERR_NO_TEAMS");
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
        } else if (cbRaceTeam.getValue().getId() == null) {
            String errorMessage = resourceBundle.getString("ERR_TEAM_NOT_REGISTERED");
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("ERR_TEAM_NOT_REGISTERED_MSG"),
                    resourceBundle.getString("ERR_TEAM_NOT_REGISTERED"));
        } else if (cbRaceTeam.getValue().getRacers() == null
                || cbRaceTeam.getValue().getRacers().size() <= 0) {
            String errorMessage = resourceBundle.getString("ERR_NO_RACERS_IN_ORGANIZATION");
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
        } else if (
                cbRaceRaceRound.getValue() == null
                        || cbRaceRaceRound.getValue().getDiscipline() == null
                        || cbRaceRaceRound.getValue().getDiscipline().getIntervals() == null) {
            String errorMessage = resourceBundle.getString("ERR_DISCIPLINE_INTERVALS_EMTPY");
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_CORRECT_FIELDS"),
                    resourceBundle.getString("INFO_CORRECT_FIELDS_TITLE"));
        } else {
            boolean performanceToSave = false;

            this.teamMembers.clear();
            this.teamMembers.addAll(cbRaceTeam.getValue().getRacers());
            refreshRaceIntervalsTable();
        }
    }

    private void refreshRaceIntervalsTable() {

        if (this.cbRaceTeam.getValue().getRacers().size() < 4) {
            Object[] params = {RowingRaceCodeTables.CT_COUNTRIES};
            String errorMessage = MessageFormat.format(resourceBundle.getString("ERR_MINIMUM_TEAM_MEMBERS"), this.minRacersInRace) + "\n";
            displayErrorMessage(errorMessage,
                    resourceBundle.getString("INFO_ACTION_NOT_ALLOWED"),
                    resourceBundle.getString("INFO_ACTION_NOT_ALLOWED_TITLE"));
            return;
        }
        raceIntervalsTable.getItems().clear();
        if (cbRaceRaceRound.getValue().getDiscipline().getIntervals() != null
                && this.cbRaceTeam.getValue().getRacers() != null) {
            if (cbRaceRaceRound.getValue().getDiscipline().getIntervals().size() == 1) {
                relayRounds = cbRaceRaceRound.getValue().getDiscipline().getIntervals().get(0).getWorkoutValue() / cbRaceRaceRound.getValue().getDiscipline().getIntervals().get(0).getRelaySplitValue();
                this.raceDistance = cbRaceRaceRound.getValue().getDiscipline().getIntervals().get(0).getWorkoutValue();
                this.relaySplit = cbRaceRaceRound.getValue().getDiscipline().getIntervals().get(0).getRelaySplitValue();
                if (cbRaceRaceRound.getValue().getDiscipline().getIntervals().get(0).getDimension().getAcronym().equals("DIM_TIME")) {
                    this.raceOnDistance = false;
                }
            } else {
                relayRounds = cbRaceRaceRound.getValue().getDiscipline().getIntervals().size();
            }
            for (int i = 0; i < relayRounds; i++) {
                RacerInterval _ri = new RacerInterval();
                _ri.setIntervalNumber(i + 1);
                _ri.setRacer(cbRaceTeam.getValue().getRacers().get(i % maxRacersInRace));
                this._intervalsData.add(_ri);
            }
            raceIntervalsTable.layout();
        }
    }

    @FXML
    private void handleSavePerformance() {
        if (performances == null || performances.getPerformances() == null) {
            String saved = this.resourceBundle.getString("NO_PERFORMANCE_TO_SAVE");
            displayInfoMessage(saved, saved, saved);
            return;
        }
        boolean saveSuccess = true;
        for (Performance p : performances.getPerformances()) {
            try {
                dataProcessor.saveOrUpdatePerformance(p);
                performances.getPerformances().remove(p);
            } catch (RowingRaceException e) {
                logger.error("Error saving performance.", e);
                String notSaved = this.resourceBundle.getString("INFO_PERFORMANCE_NOT_SAVED");
                String[] args = {Integer.toString(performance.getFinalDistance()), RowingRaceTimeUtil.formatRowingTime(performance.getFinalTime())};
                displayInfoMessage(MessageFormat.format(this.resourceBundle.getString("PERFORMANCE_DETAIL"), args), notSaved, notSaved);
                saveSuccess = false;
            }
        }
        if (saveSuccess) {
            try {
                fileService.deletePerformance();
            } catch (IOException e) {
                logger.debug(ExceptionUtils.exceptionAsString(e));
            }
            String saved = this.resourceBundle.getString("INFO_PERFORMANCE_SAVED_DB");
            displayInfoMessage(saved, saved, saved);
        } else {
            try {
                fileService.saveOrUpdate(performances);
                String saved = this.resourceBundle.getString("INFO_PERFORMANCE_SAVED_FS");
                displayInfoMessage(saved, saved, saved);
            } catch (IOException e) {
                logger.error("Can't write performance data to file after attempt to store it in database.");
            }
        }
    }


    private void setupPerformance() {
        try {
            logger.info("going to load performances from file");
            this.performances = fileService.loadPerformance();
        } catch (IOException e) {
            logger.info("error while loading performance data from file : " + ExceptionUtils.exceptionAsString(e));
        }
        if (this.performances == null) {
            this.performances = new Performances();
            logger.info("Loading performance data from file failed, instantiating new ");
            btnSavePerformance.setDisable(true);
        } else {
            btnSavePerformance.setDisable(false);
        }
        lblDeviceConnected.setText(resourceBundle.getString("label.pm.disconnected"));
    }

    @Override
    public void showTime(String time) {
        this.lbRowingPaceTime.setText(time);
    }

    @FXML
    private void handleRaceStartStop() {


        if (raceRunning) {
            this.performanceWatcher.stop();
            this.workState = -1;
            this.remote.callResetCmd();
            this.remote.callGoReadyCmd();
        } else {
            this.remote.callResetCmd();
            this.remote.callGoReadyCmd();
        }

        if (deviceCount <= 0) {
            deviceCount = remote.connectToDevice();
        }

        if (deviceCount > 0) {
            this.raceRunning = true;
            PMOperations.setWorkout(this.remote, cbRaceRaceRound.getValue().getDiscipline());
            // only while one rowing machine is supposed to be used
            this.remote.setPort(0);
            this.performanceWatcher.setCycleCount(Timeline.INDEFINITE);
            this.performanceWatcher.play();
        }
        refreshRaceIntervalsTable();
    }

    Timeline connectivityWatcher = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            deviceCount = 0;
            try {
                deviceCount = remote.connectToDevice();
            } catch (Exception e) {
                logger.error("Can't connect to device, cause : ",e);
                connectivityWatcher.stop();
            }
            if (deviceCount > 0) {
                lblDeviceConnected.setText(resourceBundle.getString("label.pm.connected"));
            } else {
                lblDeviceConnected.setText(resourceBundle.getString("label.pm.disconnected"));
            }
        }
    }));
    Timeline performanceWatcher = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            if (deviceCount > 0) {
                double speed = PMOperations.getSpeed(remote);
                lbRowingPaceTime.setText(MessageFormat.format(resourceBundle.getString("label.actualRowingTime"), RowingRaceTimeUtil.rowingSpeedAsParams(speed)));

                int cadence = PMOperations.getCadence(remote);
                String[] props = {Integer.toString(cadence)};
                rowingCadenceLabel.setText(MessageFormat.format(resourceBundle.getString("label.cadence"), props));

                double seconds = PMOperations.getDataGetWorkTime(remote);
                int sekundy = (int) ((seconds / 100) % 60);
                lblRowingTime.setText(MessageFormat.format(resourceBundle.getString("label.rowingTime"), RowingRaceTimeUtil.formatRowingTime(seconds)));

                int newWorkState = PMOperations.getWorkoutState(remote);
                if (newWorkState != workState) {
                    workState = newWorkState;
                    lblWorkStatus.setText(resourceBundle.getString("workState." + newWorkState));
                }
                logger.debug("workstate : " + workState);

                int distance = 0;

                try {
                    distance = PMOperations.intArrayToInt(PMOperations.getDataGetWorkDistance(remote));
                } catch (Exception e) {
                    logger.debug(" something bad happen during distance calculations : " + ExceptionUtils.exceptionAsString(e));
                }

                String[] wrkDistanceProps = {distance / 10 + " m / " + raceDistance + " m"};
                lblWorkoutDistance.setText(MessageFormat.format(resourceBundle.getString("label.workoutDistance"), wrkDistanceProps));

                if (workState == 1) {
                    if (performance == null) {
                        performance = new Performance();
                        performance.setTeam(cbRaceTeam.getValue());
                        logger.debug("performance created");
                    }
                    if (performance.getParameters() == null) {
                        performance.setParameters(new ArrayList<PerformanceParameter>());
                    }

                    try {
                        if (raceIntervalsTable.getSelectionModel().getSelectedIndex() < 0) {
                            raceIntervalsTable.getSelectionModel().select(0);
                            logger.debug("setting raceIntervalsTable's selection model to 0");
                        }
                        int currSplit = raceIntervalsTable.getSelectionModel().getSelectedIndex();
                        int workout = 0;
                        if (raceOnDistance) {
                            workout = distance / 10;
                        } else {
                            workout = (int) (seconds / 100);
                        }
                        int splitShift = ((raceDistance - workout) / relaySplit);
                        if (splitShift > currSplit) {
                            currSplit++;
                            if (raceIntervalsTable.getSelectionModel().getSelectedItem() == null) {
                                raceIntervalsTable.getSelectionModel().select(0);
                            }
                            raceIntervalsTable.getSelectionModel().getSelectedItem().setDistance(new Long(distance));
                            raceIntervalsTable.getSelectionModel().getSelectedItem().setTime(RowingRaceTimeUtil.formatRowingTime(seconds));
                            // posun sa na dalsie
                            raceIntervalsTable.getSelectionModel().select(currSplit);
                            raceIntervalsTable.getColumns().get(2).setVisible(false);
                            raceIntervalsTable.getColumns().get(2).setVisible(true);
                        }
                    } catch (Exception e) {
                        logger.debug("racer shift gone bad :" + ExceptionUtils.exceptionAsString(e));
                    }

                    if (sekundy % 3 == 0) {
                        PerformanceParameter performanceParam = new PerformanceParameter();
                        performanceParam.setCadence(cadence);
                        performanceParam.setSpeed(speed);
                        performanceParam.setTime(seconds);
                        performanceParam.setRacedBy(raceIntervalsTable.getSelectionModel().getSelectedItem().getRacer());
                        logger.debug("performance parameters, added racer : " + raceIntervalsTable.getSelectionModel().getSelectedItem().getRacer().getFullName());
                        performance.getParameters().add(performanceParam);
                        logger.debug("performance parameters added at : " + sekundy);
                    }
                }

                if (performance != null && (workState == 10 || workState == 12)) {
                    if (distance == 0) {
                        distance = raceDistance;
                    }
                    performance.setFinalDistance(distance);
                    performance.setDate(new Date());
                    performance.setFinalTime(seconds);
                    performance.setRaceRound(cbRaceRaceRound.getValue());
                    logger.debug("performance watcher going down");
                    performanceWatcher.stop();
                    logger.debug("performance watcher is down");
                    btnSavePerformance.setDisable(false);
/*
                    try {
                        logger.debug("connected, going to write data to DB");
                        dataProcessor.saveOrUpdatePerformance(performance);
                        logger.debug("data writen to DB");
                    } catch (RowingRaceException e) {
                        logger.debug("Error saving performance data to database.", e);
                    }
*/
                    if (performances.getPerformances() == null) {
                        performances.setPerformances(new ArrayList<Performance>());
                    }
                    performances.getPerformances().add(performance);
                    try {
                        logger.debug("save performance data to file");
                        fileService.saveOrUpdate(performances);
                    } catch (Exception e) {
                        logger.debug("save performance data to file failed : " + ExceptionUtils.exceptionAsString(e));
                    }
                    performance = null;
                }

            } else {
                logger.info("No performance, or wrong workState ");
            }
        }
    }));

    @Override
    public void onRaceSelected(RowingRace race) {
        this.rowingRace = race;
        setRaceYears();
    }

    private void setRaceYears() {
        this.cbRaceRaceYear.getItems().clear();
        List<RaceYearDto> raceYearDtos = DtoUtils.raceYearsToDtos(this.rowingRace.getRaceYears(), this.locale.getLanguage());
        this.cbRaceRaceYear.getItems().addAll(raceYearDtos);
    }

}

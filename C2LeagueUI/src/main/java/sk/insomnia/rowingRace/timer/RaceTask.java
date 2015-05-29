package sk.insomnia.rowingRace.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.remote.RowingRaceRemote;

import java.util.TimerTask;

public class RaceTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(RaceTask.class);
    private TimeDisplay timeDisplay;
    private RowingRaceRemote rw;

    public RaceTask() {
    }

    public RaceTask(TimeDisplay timeDisplay, RowingRaceRemote rwRemote) {
        this.timeDisplay = timeDisplay;
        this.rw = rwRemote;
    }

    @Override
    public void run() {
        if (rw.getSpeed(0) > 0) {
            long speed = rw.getSpeed(0);
            speed *= 500;
            speed /= 1000;
            logger.debug(String.format("current speed %d", speed));
            int minuty = (int) (speed % 60);
            int sekundy = (int) (speed / 60);
            this.timeDisplay.showTime(minuty + ":" + sekundy);
        }
    }

}

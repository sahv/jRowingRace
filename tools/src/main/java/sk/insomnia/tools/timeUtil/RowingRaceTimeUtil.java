package sk.insomnia.tools.timeUtil;

import java.text.DecimalFormat;

/**
 * Created by martin on 9/28/2014.
 */
public class RowingRaceTimeUtil {

    private static final DecimalFormat df2 = new DecimalFormat("#.00");
    private static final DecimalFormat df = new DecimalFormat("00");


    public static double rowingTimeAsDouble(String minutes, String seconds, String millis) {
        double time = Integer.parseInt(minutes) * 60 * 100;
        time += Integer.parseInt(seconds) * 100;
        time += Integer.parseInt(millis) * 0.01;
        return time;
    }

    public static String[] rowingSpeedAsParams(double speed) {
        int sekundy = (int) (speed % 60);
        int minuty = (int) (speed / 60);
        String[] params = {df.format(minuty) + ":" + df.format(sekundy)};
        return params;
    }

    public static String[] rowingTimeAsParams(double millis) {
        int sekundy = (int) ((millis / 100) % 60);
        int minuty = (int) ((millis / 100) / 60);
        millis = millis % 1;
        String[] rowingTimeParams = {df.format(minuty), df.format(sekundy), df2.format(millis)};
        return rowingTimeParams;
    }

    public static String formatRowingTime(double millis) {
        int sekundy = (int) ((millis / 100) % 60);
        int minuty = (int) ((millis / 100) / 60);
        millis = millis % 1;
        return String.format("%s:%s%s", df.format(minuty), df.format(sekundy), df2.format(millis));
    }
}

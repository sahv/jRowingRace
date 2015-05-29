package sk.insomnia.tools.timeUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RowingRaceTimeUtilTest {

    private static final double rowingSpeed = 600;
    private static final double rowingTime = 6000.99;
    @Test
    public void testRowingSpeedAsParams() throws Exception {
        String[] asParams = RowingRaceTimeUtil.rowingSpeedAsParams(rowingSpeed);
        assertEquals("10:00", asParams[0]);
    }

    @Test
    public void testTimeAsStringToDouble(){
        String[] timeStr = RowingRaceTimeUtil.rowingTimeAsParams(rowingTime);
        double time = RowingRaceTimeUtil.rowingTimeAsDouble(timeStr[0], timeStr[1], timeStr[2].replace(".",""));
        assertEquals(rowingTime, time, 0);
    }

    @Test
    public void testRowingTimeAsParams() throws Exception {
        String[] asParams = RowingRaceTimeUtil.rowingTimeAsParams(rowingTime);
        assertEquals("01", asParams[0]);
        assertEquals("00", asParams[1]);
        assertEquals(".99", asParams[2]);
    }

    @Test
    public void testFormatRowingTime() throws Exception {
        assertEquals("01:00.90", RowingRaceTimeUtil.formatRowingTime(rowingTime));
    }
}
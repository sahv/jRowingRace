package sk.insomnia.rowingRace.communication;

import sk.insomnia.rowingRace.remote.CSAFECommand;
import sk.insomnia.rowingRace.remote.CSAFEScreenType;
import sk.insomnia.rowingRace.remote.CSAFEScreenValue;
import sk.insomnia.rowingRace.remote.CSAFEUnitType;
import sk.insomnia.rowingRace.remote.CSAFEWorkoutType;
import sk.insomnia.rowingRace.remote.PMDataHandler;
import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.Interval;

import java.util.Arrays;


public class PMOperations {


    public static void resetPM(PMDataHandler dataHandler){
        dataHandler.callResetCmd();
    }
	public static void setPMWorkout(PMDataHandler dataHandler,Discipline discipline){
		int distance = 0;
		for (Interval i:discipline.getIntervals()){
			distance+=i.getWorkoutValue();
		}
		int[] workout = {distance};
		 
		int[] command = {0xF1};
		
		int[] contents = {CSAFECommand.CSAFE_SETPMCFG_CMD.getCommandIdentifier(),
						CSAFECommand.CSAFE_PM_SET_RACEOPERATIONTYPE.getCommandIdentifier(),
						CSAFECommand.CSAFE_PM_SET_WORKOUTTYPE.getCommandIdentifier(),
						CSAFEWorkoutType.FIXED_DISTANCE_NO_SPLISTS.getValue(),
						CSAFECommand.CSAFE_PM_SET_WORKOUTDURATION.getCommandIdentifier()};		

		for (int b:workout){
			contents = addElement(contents,b);
		}
		
		contents = addElement(contents,CSAFECommand.CSAFE_PM_CONFIGURE_WORKOUT.getCommandIdentifier());
		contents = addElement(contents,0x01);
		contents = addElement(contents,CSAFECommand.CSAFE_PM_SET_SCREENSTATE.getCommandIdentifier());
		contents = addElement(contents,CSAFEScreenType.WORKOUT.getValue());
		contents = addElement(contents,CSAFEScreenValue.PREPARE_TO_ROW.getValue());
		contents = addElement(contents, 0x76);
		contents = addElement(contents, 0x1E);
		contents = addElement(contents, 0x08);
		
		contents = addElement(contents,calculateChecksum(contents));
		
		for (int b:contents){
			command = addElement(command,b);
		}
		command = addElement(command,0xF2);
		
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), command);
	}
	public static void setWorkout(PMDataHandler dataHandler,Discipline discipline){
		dataHandler.callGoFinishedCmd();
		
		dataHandler.callGoFinishedCmd();
		
		dataHandler.callResetCmd();
		
		dataHandler.callGoReadyCmd();
		
		int unitsType = 0;
		int distance = 0;
		for (Interval i:discipline.getIntervals()){
			distance+=i.getWorkoutValue();
		}
//		distance = discipline.getIntervals().get(0).getWorkoutValue();
		byte[] workout = PMOperations.intToByteArray(distance);
		int splitUnit = 0;
		int minSplitValue = 2000;
		if (discipline.getIntervals().get(0).getDimension().getAcronym().equals("DIM_DISTANCE")){
			unitsType = CSAFEUnitType.METER.getCode();
			splitUnit = 128;
			minSplitValue = 100;
			int[] workoutData = {CSAFECommand.CSAFE_SETHORIZONTAL_CMD.getCommandIdentifier(),
								 CSAFECommand.CSAFE_SETHORIZONTAL_CMD.getRequestBytesNumber(),
								 workout[0],
								 workout[1],
								 unitsType};
			dataHandler.sendCommand(dataHandler.getPort(), workoutData);
			int [] distanceSplitData = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
					0x07,
					CSAFECommand.CSAFE_PM_SET_SPLITDURATION.getCommandIdentifier(),
					CSAFECommand.CSAFE_PM_SET_SPLITDURATION.getRequestBytesNumber(),
					splitUnit,
					minSplitValue,
					0x00,
					0x00,
					0x00};

			dataHandler.sendCommand(dataHandler.getPort(), distanceSplitData);
		} else {
			unitsType = CSAFEUnitType.SECONDS_PER_KILOMETER.getCode();
			int hours  = (distance/60/60);
			int minutes = (distance/60)%60;
			int seconds = (distance%60);
			int[] workoutData = {CSAFECommand.CSAFE_SETTWORK_CMD.getCommandIdentifier(),CSAFECommand.CSAFE_SETTWORK_CMD.getRequestBytesNumber(),hours,minutes,seconds};
			byte[] splitDurationData = intToByteArray(minSplitValue);
			dataHandler.sendCommand(dataHandler.getPort(), workoutData);
			int [] timeSplitData = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
					0x07,
					CSAFECommand.CSAFE_PM_SET_SPLITDURATION.getCommandIdentifier(),
					CSAFECommand.CSAFE_PM_SET_SPLITDURATION.getRequestBytesNumber(),
					splitUnit,
					splitDurationData[0],
					splitDurationData[1],
					0x00,
					0x00};

			dataHandler.sendCommand(dataHandler.getPort(), timeSplitData);
		}

		dataHandler.callSetProgrammedWorkoutCmd();
		dataHandler.callGoInUseCmd();		
	}
	
	public static double getSpeed(PMDataHandler dataHandler){
		int[] response = dataHandler.callGetPace();
		double speed = 0;
			if (response.length>4){
			int[] _paceData = {response[4],response[5]};
			speed = PMOperations.intArrayToInt(_paceData); 
	    	speed = (speed/1000)*500;
		}
		return speed; 
	}
	
	public static int getWorkoutState(PMDataHandler dataHandler){
		int [] commandData = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
								0x01,
							  CSAFECommand.CSAFE_PM_GET_WORKOUTSTATE.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), commandData);
		return response[6];
	}
	public static int getRaceModeStatus(PMDataHandler dataHandler){		
		int [] command = {
				CSAFECommand.CSAFE_GETPMCFG_CMD.getCommandIdentifier(),
				CSAFECommand.CSAFE_GET_RACEMODESTATUS.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), command);
		return response[9];
		
	}
	public static int[] getWork(PMDataHandler dataHandler){
		int [] command = {CSAFECommand.CSAFE_GETTWORK_CMD.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), command);
		if (response.length > 6){
			int[] ret = {response[4],response[5],response[6]};
			return ret ;
		}
		return response;
	}
	public static double getDataGetWorkTime(PMDataHandler dataHandler){
		int [] command = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
				0x01,
				CSAFECommand.CSAFE_PM_GET_WORKTIME.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), command);
		double seconds = 0;
		if (response.length >=9){
			int[] ret = {response[6],response[7],response[8],response[9]};
			int[] fret = {response[10]};
			seconds = intArrayToInt(ret);
			double fraction = 0;
			fraction+=(double)intArrayToInt(fret)/100;
			seconds +=fraction;
		}
		return seconds;
	}
	
	public static int calculateChecksum(byte[] bytes){
		byte checksum = 0;
		for (int i = 0; i < bytes.length; i++){
			checksum ^=bytes[i];
		}
		return checksum;
	}
	
	public static int calculateChecksum(int[] bytes){
		byte checksum = 0;
		for (int i = 0; i < bytes.length; i++){
			checksum ^=bytes[i];
		}
		return checksum;
	}
	
	public static int[] getDataGetWorkDistance(PMDataHandler dataHandler){
		int [] commandData = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
				0x01,
				CSAFECommand.CSAFE_PM_GET_WORKDISTANCE.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), commandData); 
		int[] _distanceData = {response[6],response[7],response[8],response[9],response[10]};
		return _distanceData;
	}
	public static int[] getDataGetTotalWorkDistance(PMDataHandler dataHandler){
		int [] commandData = {CSAFECommand.CSAFE_SETUSERCFG1_CMD.getCommandIdentifier(),
							  0x01,
							  CSAFECommand.CSAFE_PM_GET_TOTAL_WORKDISTANCE.getCommandIdentifier()};
		int[] response = dataHandler.sendCommand(dataHandler.getPort(), commandData); 
		int[] _distanceData = {response[3]};
		return _distanceData;
	}

	public static int getCadence(PMDataHandler dataHandler){
		int[] response = dataHandler.callGetCadence();		
		int[] _paceData = {response[4],response[5]};
		int cadence = PMOperations.intArrayToInt(_paceData);
		return cadence;
	}
	
	public static final byte[] intToByteArray(int value) {
		byte[] _values = {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
		byte[] values = {0,0,0,0};
	     for (int x = _values.length-1; x>=0; x--){
	    	 values[(_values.length-1)-x] = _values[x];
	     }
	     return values;
	}
	public static int byteArrayToInt(byte[] b){
		int x = 0;
	    if (b.length>0) x+=  b[0] & 0xFF;
	    if (b.length>1) x|= (b[1] & 0xFF) << 8;
	    if (b.length>2) x|= (b[2] & 0xFF) << 16;
	    if (b.length>3) x|= (b[3] & 0xFF) << 24;	    
	    return x;
	}
	public static int intArrayToInt(int[] b){
		int x = 0;
	    if (b.length>0) x+=  b[0] & 0xFF;
	    if (b.length>1) x|= (b[1] & 0xFF) << 8;
	    if (b.length>2) x|= (b[2] & 0xFF) << 16;
	    if (b.length>3) x|= (b[3] & 0xFF) << 24;	    
	    return x;
	}

	public static int[] addElement(int[] array, int element){
		int[] result =  Arrays.copyOf(array, array.length+1);
		result[array.length] = element;
		return result;
	}
	public static byte[] addElement(byte[] array, byte element){
		byte[] result =  Arrays.copyOf(array, array.length+1);
		result[array.length] = element;
		return result;
	}
}

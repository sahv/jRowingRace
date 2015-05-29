package sk.insomnia.rowingRace.remote;

public class PMDataHandler extends RowingRaceRemote {
	
	private int port;
	
	public int callGetStatusCmd(){
		int[] command = {CSAFECommand.CSAFE_GETSTATUS_CMD.getCommandIdentifier()};
		int[] response = this.sendCommand(this.port, command);
		return response[1];
	}
	public void callResetCmd(){
		int[] command = {CSAFECommand.CSAFE_RESET_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}
	public void callGoIdleCmd(){
		int[] command = {CSAFECommand.CSAFE_GOIDLE_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}
	
	public void callGoHaveIdCmd(){
		int[] command = {CSAFECommand.CSAFE_GOHAVEID_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}
	
	public void callGoInUseCmd(){
		int[] command = {CSAFECommand.CSAFE_GOINUSE_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}

	public void callGoFinishedCmd(){
		int[] command = {CSAFECommand.CSAFE_GOFINISHED_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}

	public void callGoReadyCmd(){
		int[] command = {CSAFECommand.CSAFE_GOREADY_CMD.getCommandIdentifier()};
		this.sendCommand(this.port, command);
	}

	public int[] callGetVersionCmd(){
		int[] command = {CSAFECommand.CSAFE_GETVERSION_CMD.getCommandIdentifier()};
		return this.sendCommand(this.port, command);
	}
	
	public void callSetProgrammedWorkoutCmd(){
		int[] command = {CSAFECommand.CSAFE_SETPROGRAM_CMD.getCommandIdentifier(),
						 CSAFECommand.CSAFE_SETPROGRAM_CMD.getRequestBytesNumber(),
						 0x00,
						 0x00};		
		this.sendCommand(this.port, command);
	}
	
	public void callSetPredefinedWorkoutCmd(){
		int[] command = {CSAFECommand.CSAFE_SETPROGRAM_CMD.getCommandIdentifier(),0x02,0x01,0x00};
		this.sendCommand(this.port, command);
	}
	
	public int[] callGetPace(){
		int[] command = {CSAFECommand.CSAFE_GETPACE_CMD.getCommandIdentifier()};
		return this.sendCommand(this.port, command);
	}
	
	public int[] callGetCadence(){
		int[] command = {CSAFECommand.CSAFE_GETCADENCE_CMD.getCommandIdentifier()};
		return this.sendCommand(this.port, command);
	}
	
	public int[] callGetWorkoutState(){
		int[] command = {CSAFECommand.CSAFE_GETWORKOUT_STATE.getCommandIdentifier()};
		return this.sendCommand(this.port, command);
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	
}
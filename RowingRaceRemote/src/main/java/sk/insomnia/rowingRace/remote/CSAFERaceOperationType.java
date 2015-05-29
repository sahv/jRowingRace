package sk.insomnia.rowingRace.remote;

public enum CSAFERaceOperationType {
	DISABLE(0),
	PARTICIPATION_REQUEST(1),
	SLEEP(2),
	ERG_INIT(3),
	PHYSICAL_ADDRESS_INIT(4),
	RACE_WARMUP(5),
	RACE_INIT(6),
	TIME_SYNC(7),
	RACE_WAIT_TO_START(8),
	RACE_START(9),
	FALSE_START(10),
	TERMINATE(11),
	IDLE(12);

	private int code;
	
	CSAFERaceOperationType (int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
}

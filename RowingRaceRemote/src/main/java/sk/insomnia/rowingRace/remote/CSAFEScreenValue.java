package sk.insomnia.rowingRace.remote;

public enum CSAFEScreenValue {
	NONE(0),
	PREPARE_TO_ROW(1),
	TERMINATE_WORKOUT(2),
	REARM_WORKOUT(3),
	REFRESH_LOCAL_COPIES_OF_LOGCARD_STRUCTURES(4),
	PREPARE_FOR_RACE_START(5),
	RETURN_TO_NORMAL_MODE(6),
	LOGCARD_BUSY_WARNING(7),
	LOGCARD_SELECT_USER(8),
	RESET_RACE_PARAMETERS(9),
	CABLLE_TEST_SLAVE_MODE(10);

	private int value;

	CSAFEScreenValue (int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
}

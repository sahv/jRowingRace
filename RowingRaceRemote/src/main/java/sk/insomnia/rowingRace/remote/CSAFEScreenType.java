package sk.insomnia.rowingRace.remote;

public enum CSAFEScreenType {
	NO_ACTION(0),
	WORKOUT(1),
	RACE(2),
	CSAFE(3);
	
	private int value;
	
	CSAFEScreenType (int value){
		this.value = value;
	}

	public int getValue(){
		return this.value;
	}
}

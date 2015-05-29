package sk.insomnia.rowingRace.remote;

public enum CSAFEWorkoutType {

		JUST_ROW_NO_SPLITS(0),
		JUST_ROW_SPLIST(1),
		FIXED_DISTANCE_NO_SPLISTS(2),
		FIXED_DISTANCE_SPLITS(3),
		FIXED_TIME_NO_SPLITS(4),
		FIXED_TIME_SPLITS(5),
		FIXED_TIME_INTERVAL(6),
		FIXED_DISTANCE_INTERVAL(7),
		VARIABLE_INTERVAL(8);
		
		
		private int value;
		
		CSAFEWorkoutType (int value){
			this.value = value;
		}
		
		public int getValue(){
			return this.value;
		}
}

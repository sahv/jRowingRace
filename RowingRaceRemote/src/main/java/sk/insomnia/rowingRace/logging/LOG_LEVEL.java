package sk.insomnia.rowingRace.logging;

public enum LOG_LEVEL {
	OFF_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return ""; }
		@Override
		public int getIndex() { return -1; }		
	},
	FATAL_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<***FATAL***>>\t"; }
		@Override
		public int getIndex() { return 0; }
	},
	ERROR_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<-ERROR->>\t"; }
		@Override
		public int getIndex() { return 1; }
	},
	WARN_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<WARN>>\t"; }
		@Override
		public int getIndex() { return 2; }
	},
	INFO_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<INFO>>\t"; }
		@Override
		public int getIndex() { return 3; }
	},
	DEBUG_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<DEBUG>>\t"; }
		@Override
		public int getIndex() { return 4; }
	},
	TRACE_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return "\t<<TRACE>>\t"; }
		@Override
		public int getIndex() { return 5; }
	},
	ALL_LOG_LEVEL { 
		@Override
		public String getHighlightMessage() { return ""; }
		@Override
		public int getIndex() { return 6; }
	};
	
	abstract int getIndex();
	abstract String getHighlightMessage();
}

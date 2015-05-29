package sk.insomnia.rowingRace.constants;

public enum RowingRaceCodeTables {

	
    CT_TEAM_CATEGORIES("RR_TEAM_CATEGORY",Constants.FILE_CT_TEAM_CATEGORIES),
    CT_COUNTRIES("RR_COUNTRY",Constants.FILE_CT_COUNTRIES),
    CT_INTERVAL_DIMENSION("RR_INTERVAL_DIMENSION",Constants.FILE_CT_DIMENSIONS_NAME),
    CT_LANGUAGES("RR_LANGUAGES",Constants.FILE_CT_LANGUAGES),
    CT_RACE_CATEGORY("RR_RACE_CATEGORY",Constants.FILE_CT_RACE_CATEGORIES),
    CT_DISCIPLINE_CATEGORY("RR_DISCIPLINE_CATEGORY",Constants.FILE_CT_DISCIPLINE_CATEGORIES);
    
	private final String tableName;
	private final String fileName;

	RowingRaceCodeTables(String tableName,String fileName){
		this.tableName = tableName;
		this.fileName = fileName;
	}
	
	public String getTableName(){
		return this.tableName;
	}

	public String getFileName() {
		return fileName;
	}
	
	
}

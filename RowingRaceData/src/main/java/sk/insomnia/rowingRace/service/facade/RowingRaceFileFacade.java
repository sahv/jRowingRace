package sk.insomnia.rowingRace.service.facade;

import java.io.IOException;
import java.util.List;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.so.DisciplineCategory;
import sk.insomnia.rowingRace.so.Performances;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;

public interface RowingRaceFileFacade {

	public void saveSchool(School school) throws IOException;
	public School loadSchool(String key) throws IOException;
	
	public String getSchoolCode() throws IOException;
	
	public void saveRowingRace(RowingRace race) throws IOException;
	public RowingRace loadRowingRace() throws IOException;
	
	public void saveOrUpdate(Performances performances) throws IOException;
	public void deletePerformance() throws IOException;
	public Performances loadPerformance() throws IOException;
	
	public List<DisciplineCategory> loadDisciplineCategories() throws IOException;
	public void saveDisciplineCategories(List<DisciplineCategory> data) throws IOException;
	

    public void saveOrUpdate(List<EnumEntityDto> data, RowingRaceCodeTables codeTable) throws IOException;
    public List<EnumEntityDto> loadCodeTable(RowingRaceCodeTables codeTable) throws IOException;

}

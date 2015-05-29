package sk.insomnia.rowingRace.dao;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.sql.SQLException;
import java.util.List;

public interface CodeTableDao {

    public void saveOrUpdate(EnumEntity data, RowingRaceCodeTables codeTable) throws SQLException;

    public List<EnumEntity> getAll(RowingRaceCodeTables codeTable) throws SQLException;

    public EnumEntity findById(Long id, RowingRaceCodeTables codeTable) throws SQLException;

    public EnumEntity findByAcronym(String acronym, RowingRaceCodeTables codeTable, boolean withMutations) throws SQLException;

    public void deleteCodeTableValue(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException;

    public List<EnumEntity> getSlaveValues(EnumEntity masterValue, RowingRaceCodeTables masterCodeTable, RowingRaceCodeTables slaveCodeTable) throws SQLException;

    public void saveSlaveValues(Long masterValueId, RowingRaceCodeTables masterCodeTable, List<EnumEntity> slaveValues, RowingRaceCodeTables slaveCodeTable) throws SQLException;
}

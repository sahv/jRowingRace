package sk.insomnia.rowingRace.dao;

import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RaceCategoryDao {

    public EnumEntitySO findById(Long id) throws SQLException;
    public EnumEntitySO findById(Long id, Connection connection) throws SQLException;

    public List<EnumEntitySO> getAll() throws SQLException;
    public List<EnumEntity> findBySchoolId(Long schooolId, Connection connection) throws SQLException;

    public void saveOrUpdate(EnumEntitySO raceCategory) throws SQLException;

}

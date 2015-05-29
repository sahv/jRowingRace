package sk.insomnia.rowingRace.dao;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.LanguageMutation;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by bobek on 6/26/2014.
 */
public interface LanguageMutationsDao {

    public List<LanguageMutation> getMutationsForKey(Long id, RowingRaceCodeTables codeTable) throws SQLException;
    public void addMutationToKey(Long keyId, LanguageMutation mutation) throws SQLException;
    public void removeMutationFromKey(LanguageMutation mutation) throws SQLException;
    public void removeAllMutationsFromKey(EnumEntity enumEntity, RowingRaceCodeTables codeTable) throws SQLException;
    public void updateMutation(LanguageMutation mutation) throws SQLException;
}

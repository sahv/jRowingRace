package sk.insomnia.rowingRace.dataStore;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bobek on 7/16/2014.
 */
public final class CommonDataStore {

    private static final Map<Class, Object> data = new HashMap<>();
    private static final Map<Class<? extends EnumEntity>, List<? extends EnumEntityDto>> enumEntities = new HashMap();
    private static final Map<RowingRaceCodeTables, List<EnumEntityDto>> codeTableValues = new HashMap();


    public static Object getInstanceOfClass(Class clazz) throws NoDataForKeyException {
        if (data.containsKey(clazz)) {
            return data.get(clazz);
        }
        throw new NoDataForKeyException(String.format("Data store doesn't contain values for class %s", clazz.getCanonicalName()));
    }

    public static void registerInstance(Class clazz, Object instance) {
        data.put(clazz, instance);
    }

    public static List<? extends EnumEntityDto> getValuesForClass(Class<? extends EnumEntity> clazz) throws NoDataForKeyException {
        if (enumEntities.containsKey(clazz)) {
            return enumEntities.get(clazz);
        }
        throw new NoDataForKeyException(String.format("Data store doesn't contain values for class %s", clazz.getCanonicalName()));
    }

    public static void registerValuesForClass(Class<? extends EnumEntity> clazz, List<? extends EnumEntityDto> values) {
        enumEntities.put(clazz, values);
    }
    public static void registerValuesForCodeTable(RowingRaceCodeTables codeTable, List<EnumEntityDto> values) {
        codeTableValues.put(codeTable, values);
    }
    public static List<EnumEntityDto> getValuesForCodeTable(RowingRaceCodeTables codeTable) throws NoDataForKeyException {
        if (codeTableValues.containsKey(codeTable)) {
            return codeTableValues.get(codeTable);
        }
        throw new NoDataForKeyException(String.format("Data store doesn't contain values for code table %s", codeTable));
    }
}

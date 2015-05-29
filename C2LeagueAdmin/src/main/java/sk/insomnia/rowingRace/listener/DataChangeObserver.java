package sk.insomnia.rowingRace.listener;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bobek on 6/29/2014.
 */
public class DataChangeObserver {

    private static final Map<RowingRaceCodeTables, List<CodeTableListener>> CODE_TABLE_LISTENER_LIST = new HashMap<>();

    public static void registerRaceCategoriesListener(CodeTableListener listener, RowingRaceCodeTables codeTable) {
        if (CODE_TABLE_LISTENER_LIST.containsKey(codeTable)) {
            CODE_TABLE_LISTENER_LIST.get(codeTable).add(listener);
        } else {
            List<CodeTableListener> codeTableListeners = new ArrayList<>();
            codeTableListeners.add(listener);
            CODE_TABLE_LISTENER_LIST.put(codeTable, codeTableListeners);
        }
    }

    public static void notifyCTSelected(List<EnumEntityDto> values, RowingRaceCodeTables codeTable) {
        if (CODE_TABLE_LISTENER_LIST.containsKey(codeTable)) {
            for (CodeTableListener listener : CODE_TABLE_LISTENER_LIST.get(codeTable)) {
                listener.onCodeTableSelected(values, codeTable);
            }
        }
    }
}

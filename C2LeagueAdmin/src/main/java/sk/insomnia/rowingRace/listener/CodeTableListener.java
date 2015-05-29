package sk.insomnia.rowingRace.listener;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.util.List;

/**
 * Created by bobek on 6/29/2014.
 */
public interface CodeTableListener {

    public void onCodeTableSelected(List<EnumEntityDto> values, RowingRaceCodeTables codeTable);
}

package sk.insomnia.rowingRace.so;

import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;

/**
 * Created by bobek on 6/26/2014.
 */
public class LanguageMutation {

    private Long id;
    private String value;
    private EnumEntity language;
    private RowingRaceCodeTables codeTable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EnumEntity getLanguage() {
        return language;
    }

    public void setLanguage(EnumEntity language) {
        this.language = language;
    }

    public RowingRaceCodeTables getCodeTable() {
        return codeTable;
    }

    public void setCodeTable(RowingRaceCodeTables codeTable) {
        this.codeTable = codeTable;
    }
}

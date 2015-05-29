package sk.insomnia.rowingRace.dto;

import sk.insomnia.rowingRace.mapping.MappingUtil;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.LanguageMutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleEnumEntityDto implements Comparable, Serializable, EnumEntityDto {

    /**
     *
     */
    private static final long serialVersionUID = 7110541025568978853L;
    private Long id;
    private String acronym;
    private String value;
    private List<LanguageMutation> languageMutations = new ArrayList<LanguageMutation>();

    public SimpleEnumEntityDto() {
    }

    public SimpleEnumEntityDto(EnumEntitySO that) {
        this.setId(that.getId());
        this.setAcronym(that.getAcronym());
        if (that.getLanguageMutations() != null) {
            this.setLanguageMutations(that.getLanguageMutations());
        }
    }

    public SimpleEnumEntityDto(EnumEntity that) {
        this.setId(that.getId());
        this.setAcronym(that.getAcronym());
        if (that.getLanguageMutations() != null) {
            this.setLanguageMutations(that.getLanguageMutations());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EnumEntity toEnumEntity() {
        EnumEntity that = new EnumEntitySO();
        that.setId(this.getId());
        that.setAcronym(this.getAcronym());
        if (this.getLanguageMutations() != null) {
            that.setLanguageMutations(this.getLanguageMutations());
        }
        return that;
    }

    public static List<EnumEntity> toSoList(final List<EnumEntityDto> dtoList) {
        final List<EnumEntity> retvals = new ArrayList<>();
        for (EnumEntityDto value : dtoList) {
            retvals.add(MappingUtil.toSO(value));
        }
        return retvals;
    }


    @Override
    public String toString() {
        return this.value;
    }

    public List<LanguageMutation> getLanguageMutations() {
        return languageMutations;
    }

    public void setLanguageMutations(List<LanguageMutation> languageMutations) {
        this.languageMutations = languageMutations;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof SimpleEnumEntityDto)) {
            return 1;
        }
        SimpleEnumEntityDto _o = (SimpleEnumEntityDto) o;
        if (_o.getId() == null || this.getId() == null) {
            return 1;
        }
        if (this.getId().longValue() < _o.getId().longValue()) {
            return -1;
        }
        return 0;
    }
}

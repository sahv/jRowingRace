package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: bobek
 * Date: 13.6.2013
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public class EnumEntitySO implements EnumEntity, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 909431371833813415L;
    private Long id;
    private String acronym;
    private List<LanguageMutation> languageMutations = new ArrayList<LanguageMutation>();

    public EnumEntitySO() {
        super();
    }

    public EnumEntitySO(EnumEntitySO that) {
        if (that.getAcronym() != null) this.setAcronym(that.getAcronym());
        if (that.getId() != null) this.setId(that.getId());
        if (that.getLanguageMutations() != null) this.setLanguageMutations(that.getLanguageMutations());
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

    public String toString() {
        String str = "";
        if (this.acronym != null) {
            str += acronym;
        }
        return str;
    }

    public List<LanguageMutation> getLanguageMutations() {
        return languageMutations;
    }

    public void setLanguageMutations(List<LanguageMutation> languageMutations) {
        this.languageMutations = languageMutations;
    }
}

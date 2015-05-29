package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;

public interface EnumEntity extends Serializable {

    public Long getId();
    public String getAcronym();
    public List<LanguageMutation> getLanguageMutations();

    public void setId(Long id);
    public void setAcronym(String acronym);
    public void setLanguageMutations(List<LanguageMutation> languageMutations);
}

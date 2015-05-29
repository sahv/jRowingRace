package sk.insomnia.rowingRace.so;

import java.io.Serializable;
import java.util.List;

public class DisciplineCategory
        extends EnumEntitySO implements Serializable, EnumEntity {

    private static final long serialVersionUID = -6223250526284813355L;
    private List<Discipline> disciplines;
    
	public List<Discipline> getDisciplines() {
		return disciplines;
	}
	public void setDisciplines(List<Discipline> disciplines) {
		this.disciplines = disciplines;
	}
    
    
    
}

package sk.insomnia.rowingRace.dto;

import sk.insomnia.rowingRace.so.Discipline;
import sk.insomnia.rowingRace.so.EnumEntity;

import java.util.List;

public class DisciplineCategoryDto extends SimpleEnumEntityDto implements ComplexEnumEntityDto {

    public DisciplineCategoryDto() {
    }

    public DisciplineCategoryDto(EnumEntityDto enumEntity) {
        if (enumEntity.getId() != null) {
            this.setId(enumEntity.getId());
        }
        if (enumEntity.getAcronym() != null) {
            this.setAcronym(enumEntity.getAcronym());
        }
        if (enumEntity.getLanguageMutations() != null) {
            this.setLanguageMutations(enumEntity.getLanguageMutations());
        }

        if (enumEntity.getValue()!=null){
            this.setValue(enumEntity.getValue());
        }
    }

    private List<Discipline> disciplines;

    public List<Discipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    @Override
    public <D extends ComplexEnumEntityDto, S extends EnumEntityDto> D transform(S enumEntityBasicValue, D classSpecificValues) throws IllegalAccessException, InstantiationException {
        if (!(classSpecificValues instanceof DisciplineCategoryDto)) {
            throw new IllegalStateException(String.format("Expected class %s but found %s", DisciplineCategoryDto.class, classSpecificValues.getClass()));
        }
        DisciplineCategoryDto disciplineCategoryDto = new DisciplineCategoryDto(enumEntityBasicValue);
        disciplineCategoryDto.setDisciplines(((DisciplineCategoryDto) classSpecificValues).getDisciplines());
        return (D) disciplineCategoryDto;
    }
}

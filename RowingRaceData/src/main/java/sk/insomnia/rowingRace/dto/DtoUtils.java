package sk.insomnia.rowingRace.dto;

import sk.insomnia.rowingRace.mapping.MappingUtil;
import sk.insomnia.rowingRace.so.EnumEntity;
import sk.insomnia.rowingRace.so.EnumEntitySO;
import sk.insomnia.rowingRace.so.LanguageMutation;
import sk.insomnia.rowingRace.so.RaceYear;

import java.util.ArrayList;
import java.util.List;


public final class DtoUtils {

    private DtoUtils() {
        throw new IllegalStateException("DtoUtils should not be instantiated.");
    }

    public static final List<EnumEntityDto> listOfLanguageSpecificValues(List<? extends EnumEntity> enumEntities, String language) throws DtoUtilException {
        if (enumEntities == null) {
            throw new IllegalStateException("Can't transform empty list.");
        }
        List<EnumEntityDto> enumEntityDtos = new ArrayList<>();
        for (EnumEntity enumEntity : enumEntities) {
            enumEntityDtos.add(transformWithLanguageDependentValue(enumEntity, language, SimpleEnumEntityDto.class));
        }
        return enumEntityDtos;
    }

    public static final <D extends ComplexEnumEntityDto> List<D> listOfLanguageSpecificValuesForDtoClass(List<D> complexEntities, String language, Class<D> dtoClass) throws DtoUtilException {
        if (complexEntities == null) {
            throw new IllegalStateException("Can't transform empty list.");
        }
        List<D> entityDtos = new ArrayList<>();
        for (D complexEntity : complexEntities) {
            EnumEntityDto enumEntity = transformWithLanguageDependentValue(complexEntity, language, SimpleEnumEntityDto.class);
            D d = null;
            try {
                d = dtoClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                throw new DtoUtilException(String.format("Error instantiating class %s", dtoClass));
            }
            try {
                d = d.transform(enumEntity, complexEntity);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new DtoUtilException(e.getMessage());
            }
            entityDtos.add(d);
        }
        return entityDtos;
    }

    public static final List<RaceYearDto> raceYearsToDtos(List<RaceYear> raceYears, String language) {
        List<RaceYearDto> raceYearDtos = MappingUtil.asDtoList(raceYears);
        for (RaceYearDto raceYearDto : raceYearDtos) {
            DtoUtils.extractLocalizedValue(raceYearDto.getRaceCategory(), language);
        }
        return raceYearDtos;
    }

    public static final <T extends EnumEntityDto> T transformWithLanguageDependentValue(final EnumEntity enumEntity, final String language, Class<T> clazz) throws DtoUtilException {
        T enumEntityDto = null;
        try {
            enumEntityDto = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new DtoUtilException(String.format("Error instantiating %s", clazz));
        }
        enumEntityDto.setId(enumEntity.getId());
        enumEntityDto.setAcronym(enumEntity.getAcronym());
        if (enumEntity.getLanguageMutations() == null) {
            enumEntityDto.setValue(enumEntity.getAcronym());
            return enumEntityDto;
        }
        for (LanguageMutation languageMutation : enumEntity.getLanguageMutations()) {
            if (languageMutation.getLanguage().getAcronym().toUpperCase().equals(language.toUpperCase())) {
                enumEntityDto.setValue(languageMutation.getValue());
                break;
            }
        }
        enumEntityDto.setLanguageMutations(enumEntity.getLanguageMutations());
        return enumEntityDto;
    }

    public static void extractLocalizedValue(EnumEntityDto enumEntityDto, String language) {
        for (LanguageMutation languageMutation : enumEntityDto.getLanguageMutations()) {
            if (languageMutation.getLanguage().getAcronym().toUpperCase().equals(language.toUpperCase())) {
                enumEntityDto.setValue(languageMutation.getValue());
                break;
            }
        }
    }

    public static EnumEntityDto findBySo(List<EnumEntityDto> haystack, EnumEntity needle) {
        if (haystack == null) {
            throw new IllegalStateException("Can't search for a needle when there's no haystack.");
        }
        for (EnumEntityDto enumEntityDto : haystack) {
            if (enumEntityDto.getId().longValue() == needle.getId().longValue()) {
                return enumEntityDto;
            }
        }
        return null;
    }


    public static class DtoUtilException extends Exception {

        public DtoUtilException(String message) {
            super(message);
        }
    }

}

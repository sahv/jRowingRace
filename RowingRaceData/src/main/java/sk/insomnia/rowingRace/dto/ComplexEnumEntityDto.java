package sk.insomnia.rowingRace.dto;

/**
 * Created by martin on 8/24/2014.
 */
public interface ComplexEnumEntityDto extends EnumEntityDto {
    public <D extends ComplexEnumEntityDto, S extends EnumEntityDto> D transform(S enumEntityBasicValue, D classSpecificValues) throws IllegalAccessException, InstantiationException;

}

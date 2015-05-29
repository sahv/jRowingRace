package sk.insomnia.rowingRace.dto;

import sk.insomnia.rowingRace.so.EnumEntity;

/**
 * Created by martin on 8/24/2014.
 */
public interface EnumEntityDto extends EnumEntity {

    public String getValue();

    public void setValue(String value);
}

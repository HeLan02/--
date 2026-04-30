package com.jskx.cfs.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BooleanTinyIntConverter implements AttributeConverter<Boolean, Byte> {
  @Override
  public Byte convertToDatabaseColumn(Boolean attribute) {
    return (byte) (Boolean.TRUE.equals(attribute) ? 1 : 0);
  }

  @Override
  public Boolean convertToEntityAttribute(Byte dbData) {
    if (dbData == null) {
      return false;
    }
    return dbData != 0;
  }
}


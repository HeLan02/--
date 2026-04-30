package com.jskx.cfs.web.dto;

import com.jskx.cfs.model.Classroom;

public record ClassroomDto(
    Long id,
    Long buildingId,
    String buildingName,
    String roomNumber,
    Integer capacity,
    Boolean hasMultimedia,
    Boolean hasAc
) {
  public static ClassroomDto from(Classroom c) {
    return new ClassroomDto(
        c.getId(),
        c.getBuilding().getId(),
        c.getBuilding().getName(),
        c.getRoomNumber(),
        c.getCapacity(),
        c.getHasMultimedia(),
        c.getHasAc()
    );
  }
}


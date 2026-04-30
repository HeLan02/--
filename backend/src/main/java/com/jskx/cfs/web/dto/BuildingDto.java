package com.jskx.cfs.web.dto;

import com.jskx.cfs.model.Building;

public record BuildingDto(Long id, String name, String location, Integer floors) {
  public static BuildingDto from(Building b) {
    return new BuildingDto(b.getId(), b.getName(), b.getLocation(), b.getFloors());
  }
}


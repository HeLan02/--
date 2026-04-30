package com.jskx.cfs.service;

import com.jskx.cfs.model.Building;
import com.jskx.cfs.repo.BuildingRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuildingService {
  private final BuildingRepository repo;

  public BuildingService(BuildingRepository repo) {
    this.repo = repo;
  }

  public List<Building> list() {
    return repo.findAll();
  }

  public Building get(Long id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("building not found: " + id));
  }

  @Transactional
  public Building create(Building b) {
    b.setId(null);
    return repo.save(b);
  }

  @Transactional
  public Building update(Long id, Building patch) {
    Building b = get(id);
    b.setName(patch.getName());
    b.setLocation(patch.getLocation());
    b.setFloors(patch.getFloors());
    return repo.save(b);
  }

  @Transactional
  public void delete(Long id) {
    repo.deleteById(id);
  }
}


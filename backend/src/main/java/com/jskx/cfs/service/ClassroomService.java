package com.jskx.cfs.service;

import com.jskx.cfs.model.Building;
import com.jskx.cfs.model.Classroom;
import com.jskx.cfs.repo.BuildingRepository;
import com.jskx.cfs.repo.ClassroomRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomService {
  private final ClassroomRepository classroomRepo;
  private final BuildingRepository buildingRepo;

  public ClassroomService(ClassroomRepository classroomRepo, BuildingRepository buildingRepo) {
    this.classroomRepo = classroomRepo;
    this.buildingRepo = buildingRepo;
  }

  public Classroom get(Long id) {
    return classroomRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("classroom not found: " + id));
  }

  public List<Classroom> listByBuilding(Long buildingId) {
    if (buildingId == null) {
      return classroomRepo.findAll();
    }
    return classroomRepo.findByBuildingIdOrderByRoomNumberAsc(buildingId);
  }

  @Transactional
  public Classroom create(Long buildingId, Classroom c) {
    Building b = buildingRepo.findById(buildingId).orElseThrow(() -> new IllegalArgumentException("building not found: " + buildingId));
    c.setId(null);
    c.setBuilding(b);
    return classroomRepo.save(c);
  }

  @Transactional
  public Classroom update(Long id, Classroom patch) {
    Classroom c = get(id);
    if (patch.getBuilding() != null && patch.getBuilding().getId() != null) {
      Building b = buildingRepo.findById(patch.getBuilding().getId())
          .orElseThrow(() -> new IllegalArgumentException("building not found: " + patch.getBuilding().getId()));
      c.setBuilding(b);
    }
    c.setRoomNumber(patch.getRoomNumber());
    c.setCapacity(patch.getCapacity());
    c.setHasMultimedia(patch.getHasMultimedia());
    c.setHasAc(patch.getHasAc());
    return classroomRepo.save(c);
  }

  @Transactional
  public void delete(Long id) {
    classroomRepo.deleteById(id);
  }
}


package com.jskx.cfs.web;

import com.jskx.cfs.model.Building;
import com.jskx.cfs.model.Classroom;
import com.jskx.cfs.service.ClassroomQueryService;
import com.jskx.cfs.service.ClassroomService;
import com.jskx.cfs.web.dto.ClassroomDto;
import com.jskx.cfs.web.dto.FreeClassroomDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClassroomController {
  private final ClassroomService classroomService;
  private final ClassroomQueryService queryService;

  public ClassroomController(ClassroomService classroomService, ClassroomQueryService queryService) {
    this.classroomService = classroomService;
    this.queryService = queryService;
  }

  @GetMapping("/classrooms")
  public List<ClassroomDto> list(@RequestParam(required = false) Long buildingId) {
    return classroomService.listByBuilding(buildingId).stream().map(ClassroomDto::from).toList();
  }

  @PostMapping("/classroom")
  public ClassroomDto create(@RequestParam Long buildingId, @Valid @RequestBody Classroom classroom) {
    return ClassroomDto.from(classroomService.create(buildingId, classroom));
  }

  @PutMapping("/classroom/{id}")
  public ClassroomDto update(@PathVariable Long id, @Valid @RequestBody Classroom classroom) {
    return ClassroomDto.from(classroomService.update(id, classroom));
  }

  @DeleteMapping("/classroom/{id}")
  public void delete(@PathVariable Long id) {
    classroomService.delete(id);
  }

  @GetMapping("/classrooms/free")
  public List<ClassroomDto> free(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam Integer startPeriod,
      @RequestParam Integer endPeriod,
      @RequestParam(required = false) Long buildingId,
      @RequestParam(required = false) Integer minCapacity,
      @RequestParam(required = false) Integer week
  ) {
    return queryService.freeClassrooms(date, startPeriod, endPeriod, buildingId, minCapacity, week);
  }

  @GetMapping("/classrooms/recommend")
  public List<FreeClassroomDto> recommend(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam Integer startPeriod,
      @RequestParam Integer endPeriod,
      @RequestParam(required = false) Long buildingId,
      @RequestParam(required = false) Integer minCapacity,
      @RequestParam(required = false) Integer week
  ) {
    return queryService.recommend(date, startPeriod, endPeriod, buildingId, minCapacity, week);
  }
}


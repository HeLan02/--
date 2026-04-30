package com.jskx.cfs.web;

import com.jskx.cfs.model.Building;
import com.jskx.cfs.service.BuildingService;
import com.jskx.cfs.web.dto.BuildingDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BuildingController {
  private final BuildingService service;

  public BuildingController(BuildingService service) {
    this.service = service;
  }

  @GetMapping("/buildings")
  public List<BuildingDto> list() {
    return service.list().stream().map(BuildingDto::from).toList();
  }

  @PostMapping("/building")
  public BuildingDto create(@Valid @RequestBody Building building) {
    return BuildingDto.from(service.create(building));
  }

  @PutMapping("/building/{id}")
  public BuildingDto update(@PathVariable Long id, @Valid @RequestBody Building building) {
    return BuildingDto.from(service.update(id, building));
  }

  @DeleteMapping("/building/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}


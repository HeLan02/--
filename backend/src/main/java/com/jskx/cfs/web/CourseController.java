package com.jskx.cfs.web;

import com.jskx.cfs.service.CourseService;
import com.jskx.cfs.web.dto.CourseScheduleDto;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class CourseController {
  private final CourseService service;

  public CourseController(CourseService service) {
    this.service = service;
  }

  @GetMapping("/courses")
  public List<CourseScheduleDto> list(@RequestParam Long classroomId, @RequestParam(required = false) Integer week) {
    return service.list(classroomId, week).stream().map(CourseScheduleDto::from).toList();
  }

  @PostMapping(value = "/courses/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String importExcel(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("file is empty");
    }
    try {
      int count = service.importExcel(file.getInputStream());
      return "imported: " + count;
    } catch (Exception e) {
      throw new IllegalArgumentException("import failed: " + e.getMessage(), e);
    }
  }
}


package com.jskx.cfs.service;

import com.jskx.cfs.model.Classroom;
import com.jskx.cfs.model.CourseSchedule;
import com.jskx.cfs.repo.ClassroomRepository;
import com.jskx.cfs.repo.CourseScheduleRepository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {
  private final CourseScheduleRepository courseRepo;
  private final ClassroomRepository classroomRepo;

  public CourseService(CourseScheduleRepository courseRepo, ClassroomRepository classroomRepo) {
    this.courseRepo = courseRepo;
    this.classroomRepo = classroomRepo;
  }

  public List<CourseSchedule> list(Long classroomId, Integer week) {
    if (classroomId == null) {
      throw new IllegalArgumentException("classroomId is required");
    }
    return courseRepo.findByClassroomAndWeek(classroomId, week);
  }

  /**
   * Excel 模板（第一行表头，顺序固定）：
   * classroomId, courseName, dayOfWeek(1-7), startPeriod, endPeriod, weekStart, weekEnd, teacher
   */
  @Transactional
  public int importExcel(InputStream is) {
    try (Workbook wb = new XSSFWorkbook(is)) {
      Sheet sheet = wb.getSheetAt(0);
      List<CourseSchedule> toSave = new ArrayList<>();
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          continue;
        }
        Long classroomId = (long) numeric(row, 0);
        String courseName = string(row, 1);
        int dayOfWeek = (int) numeric(row, 2);
        int startPeriod = (int) numeric(row, 3);
        int endPeriod = (int) numeric(row, 4);
        int weekStart = (int) numeric(row, 5);
        int weekEnd = (int) numeric(row, 6);
        String teacher = string(row, 7);

        Classroom classroom = classroomRepo.findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("classroom not found: " + classroomId));

        CourseSchedule cs = new CourseSchedule();
        cs.setClassroom(classroom);
        cs.setCourseName(courseName);
        cs.setDayOfWeek(dayOfWeek);
        cs.setStartPeriod(startPeriod);
        cs.setEndPeriod(endPeriod);
        cs.setWeekStart(weekStart);
        cs.setWeekEnd(weekEnd);
        cs.setTeacher(teacher);
        toSave.add(cs);
      }
      courseRepo.saveAll(toSave);
      return toSave.size();
    } catch (Exception e) {
      throw new IllegalArgumentException("failed to import excel: " + e.getMessage(), e);
    }
  }

  private static double numeric(Row row, int idx) {
    var cell = row.getCell(idx);
    if (cell == null) {
      throw new IllegalArgumentException("missing numeric cell at col " + (idx + 1));
    }
    if (cell.getCellType() == CellType.STRING) {
      String s = cell.getStringCellValue();
      if (s == null || s.trim().isEmpty()) {
        throw new IllegalArgumentException("empty numeric cell at col " + (idx + 1));
      }
      return Double.parseDouble(s.trim());
    }
    return cell.getNumericCellValue();
  }

  private static String string(Row row, int idx) {
    var cell = row.getCell(idx);
    if (cell == null) {
      return null;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf((long) cell.getNumericCellValue());
    }
    return cell.getStringCellValue();
  }
}


package com.jskx.cfs.web.dto;

import com.jskx.cfs.model.CourseSchedule;

public record CourseScheduleDto(
    Long id,
    Long classroomId,
    String courseName,
    Integer dayOfWeek,
    Integer startPeriod,
    Integer endPeriod,
    Integer weekStart,
    Integer weekEnd,
    String teacher
) {
  public static CourseScheduleDto from(CourseSchedule cs) {
    return new CourseScheduleDto(
        cs.getId(),
        cs.getClassroom().getId(),
        cs.getCourseName(),
        cs.getDayOfWeek(),
        cs.getStartPeriod(),
        cs.getEndPeriod(),
        cs.getWeekStart(),
        cs.getWeekEnd(),
        cs.getTeacher()
    );
  }
}


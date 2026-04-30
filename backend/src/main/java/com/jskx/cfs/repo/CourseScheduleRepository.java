package com.jskx.cfs.repo;

import com.jskx.cfs.model.CourseSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

  @Query("""
      select cs from CourseSchedule cs
      where cs.classroom.id = :classroomId
        and (:week is null or (:week between cs.weekStart and cs.weekEnd))
      order by cs.dayOfWeek asc, cs.startPeriod asc
      """)
  List<CourseSchedule> findByClassroomAndWeek(@Param("classroomId") Long classroomId, @Param("week") Integer week);

  @Query("""
      select cs from CourseSchedule cs
      where cs.dayOfWeek = :dayOfWeek
        and (:week is null or (:week between cs.weekStart and cs.weekEnd))
        and cs.startPeriod <= :endPeriod
        and cs.endPeriod >= :startPeriod
      """)
  List<CourseSchedule> findOverlaps(@Param("dayOfWeek") Integer dayOfWeek,
      @Param("week") Integer week,
      @Param("startPeriod") Integer startPeriod,
      @Param("endPeriod") Integer endPeriod);
}


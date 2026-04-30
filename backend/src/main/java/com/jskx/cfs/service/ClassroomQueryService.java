package com.jskx.cfs.service;

import com.jskx.cfs.model.CourseSchedule;
import com.jskx.cfs.model.ReservationStatus;
import com.jskx.cfs.repo.ClassroomRepository;
import com.jskx.cfs.repo.CourseScheduleRepository;
import com.jskx.cfs.repo.ReservationRepository;
import com.jskx.cfs.web.dto.ClassroomDto;
import com.jskx.cfs.web.dto.FreeClassroomDto;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomQueryService {
  private final ClassroomRepository classroomRepo;
  private final CourseScheduleRepository courseRepo;
  private final ReservationRepository reservationRepo;

  public ClassroomQueryService(ClassroomRepository classroomRepo, CourseScheduleRepository courseRepo,
      ReservationRepository reservationRepo) {
    this.classroomRepo = classroomRepo;
    this.courseRepo = courseRepo;
    this.reservationRepo = reservationRepo;
  }

  @Transactional(readOnly = true)
  public List<ClassroomDto> freeClassrooms(LocalDate date, Integer startPeriod, Integer endPeriod, Long buildingId,
      Integer minCapacity, Integer week) {
    if (date == null || startPeriod == null || endPeriod == null) {
      throw new IllegalArgumentException("date/startPeriod/endPeriod are required");
    }
    if (startPeriod > endPeriod) {
      throw new IllegalArgumentException("startPeriod must be <= endPeriod");
    }

    int dayOfWeek = date.getDayOfWeek().getValue(); // 1-7
    Set<Long> occupied = new HashSet<>();

    List<CourseSchedule> courseOverlaps = courseRepo.findOverlaps(dayOfWeek, week, startPeriod, endPeriod);
    for (CourseSchedule cs : courseOverlaps) {
      occupied.add(cs.getClassroom().getId());
    }

    var approvedReservations = reservationRepo.findApprovedOverlapsAllClassrooms(date, startPeriod, endPeriod,
        ReservationStatus.APPROVED.code());
    for (var r : approvedReservations) {
      occupied.add(r.getClassroom().getId());
    }

    return classroomRepo.listForQuery(buildingId, minCapacity).stream()
        .filter(c -> !occupied.contains(c.getId()))
        .map(ClassroomDto::from)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<FreeClassroomDto> recommend(LocalDate date, Integer startPeriod, Integer endPeriod, Long buildingId,
      Integer minCapacity, Integer week) {
    List<ClassroomDto> free = freeClassrooms(date, startPeriod, endPeriod, buildingId, minCapacity, week);
    return free.stream()
        .map(c -> new FreeClassroomDto(c, score(c, minCapacity)))
        .sorted(Comparator.comparingInt(FreeClassroomDto::score).reversed()
            .thenComparing(fc -> fc.classroom().buildingId())
            .thenComparing(fc -> fc.classroom().roomNumber()))
        .toList();
  }

  private static int score(ClassroomDto c, Integer minCapacity) {
    int s = 0;
    if (minCapacity != null) {
      int diff = Math.max(0, c.capacity() - minCapacity);
      s += Math.max(0, 1000 - diff); // 越接近越好
    } else {
      s += Math.min(1000, c.capacity());
    }
    if (Boolean.TRUE.equals(c.hasMultimedia())) {
      s += 30;
    }
    if (Boolean.TRUE.equals(c.hasAc())) {
      s += 20;
    }
    return s;
  }
}


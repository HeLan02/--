package com.jskx.cfs.service;

import com.jskx.cfs.model.Classroom;
import com.jskx.cfs.model.CourseSchedule;
import com.jskx.cfs.model.Reservation;
import com.jskx.cfs.model.ReservationStatus;
import com.jskx.cfs.repo.ClassroomRepository;
import com.jskx.cfs.repo.CourseScheduleRepository;
import com.jskx.cfs.repo.ReservationRepository;
import com.jskx.cfs.web.dto.ReservationCreateRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
  private final ReservationRepository reservationRepo;
  private final ClassroomRepository classroomRepo;
  private final CourseScheduleRepository courseRepo;

  public ReservationService(ReservationRepository reservationRepo, ClassroomRepository classroomRepo,
      CourseScheduleRepository courseRepo) {
    this.reservationRepo = reservationRepo;
    this.classroomRepo = classroomRepo;
    this.courseRepo = courseRepo;
  }

  public List<Reservation> myReservations(String userId, Integer status) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("userId is required");
    }
    return reservationRepo.findMy(userId, status);
  }

  public List<Reservation> pending() {
    return reservationRepo.findPending();
  }

  @Transactional
  public Reservation create(String userId, String userName, ReservationCreateRequest req, Integer week) {
    validatePeriods(req.startPeriod(), req.endPeriod());
    Classroom classroom = classroomRepo.findById(req.classroomId())
        .orElseThrow(() -> new IllegalArgumentException("classroom not found: " + req.classroomId()));

    ensureNoConflict(classroom.getId(), req.reservationDate(), req.startPeriod(), req.endPeriod(), week);

    Reservation r = new Reservation();
    r.setUserId(userId);
    r.setUserName(userName);
    r.setClassroom(classroom);
    r.setPurpose(req.purpose());
    r.setReservationDate(req.reservationDate());
    r.setStartPeriod(req.startPeriod());
    r.setEndPeriod(req.endPeriod());
    r.setAttendeeCount(req.attendeeCount());
    r.setContact(req.contact());
    r.setStatus(ReservationStatus.PENDING.code());
    return reservationRepo.save(r);
  }

  @Transactional
  public Reservation cancel(Long id, String userId) {
    Reservation r = reservationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("reservation not found: " + id));
    if (!r.getUserId().equals(userId)) {
      throw new IllegalArgumentException("no permission to cancel this reservation");
    }
    ReservationStatus s = ReservationStatus.fromCode(r.getStatus());
    if (s != ReservationStatus.PENDING && s != ReservationStatus.APPROVED) {
      throw new IllegalArgumentException("only pending/approved reservations can be cancelled");
    }
    r.setStatus(ReservationStatus.CANCELLED.code());
    return reservationRepo.save(r);
  }

  @Transactional
  public Reservation approve(Long id, Authentication admin, Integer week) {
    Reservation r = reservationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("reservation not found: " + id));
    if (ReservationStatus.fromCode(r.getStatus()) != ReservationStatus.PENDING) {
      throw new IllegalArgumentException("only pending reservations can be approved");
    }
    ensureNoConflict(r.getClassroom().getId(), r.getReservationDate(), r.getStartPeriod(), r.getEndPeriod(), week);
    r.setStatus(ReservationStatus.APPROVED.code());
    r.setReviewerId(admin.getName());
    r.setReviewComment(null);
    return reservationRepo.save(r);
  }

  @Transactional
  public Reservation reject(Long id, Authentication admin, String comment) {
    Reservation r = reservationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("reservation not found: " + id));
    if (ReservationStatus.fromCode(r.getStatus()) != ReservationStatus.PENDING) {
      throw new IllegalArgumentException("only pending reservations can be rejected");
    }
    r.setStatus(ReservationStatus.REJECTED.code());
    r.setReviewerId(admin.getName());
    r.setReviewComment(comment);
    return reservationRepo.save(r);
  }

  private void ensureNoConflict(Long classroomId, LocalDate date, int startPeriod, int endPeriod, Integer week) {
    var approved = reservationRepo.findApprovedOverlaps(classroomId, date, startPeriod, endPeriod,
        ReservationStatus.APPROVED.code());
    if (!approved.isEmpty()) {
      throw new IllegalArgumentException("time conflicts with an approved reservation");
    }

    int dayOfWeek = date.getDayOfWeek().getValue();
    var courses = courseRepo.findOverlaps(dayOfWeek, week, startPeriod, endPeriod);
    for (CourseSchedule cs : courses) {
      if (cs.getClassroom().getId().equals(classroomId)
          && TimeOverlap.overlaps(startPeriod, endPeriod, cs.getStartPeriod(), cs.getEndPeriod())) {
        throw new IllegalArgumentException("time conflicts with course schedule");
      }
    }
  }

  private static void validatePeriods(Integer startPeriod, Integer endPeriod) {
    if (startPeriod == null || endPeriod == null) {
      throw new IllegalArgumentException("startPeriod/endPeriod are required");
    }
    if (startPeriod > endPeriod) {
      throw new IllegalArgumentException("startPeriod must be <= endPeriod");
    }
  }
}


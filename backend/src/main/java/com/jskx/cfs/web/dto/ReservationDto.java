package com.jskx.cfs.web.dto;

import com.jskx.cfs.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDto(
    Long id,
    String userId,
    String userName,
    ClassroomDto classroom,
    String purpose,
    LocalDate reservationDate,
    Integer startPeriod,
    Integer endPeriod,
    Integer attendeeCount,
    String contact,
    Integer status,
    String reviewerId,
    String reviewComment,
    LocalDateTime createTime,
    LocalDateTime updateTime
) {
  public static ReservationDto from(Reservation r) {
    return new ReservationDto(
        r.getId(),
        r.getUserId(),
        r.getUserName(),
        ClassroomDto.from(r.getClassroom()),
        r.getPurpose(),
        r.getReservationDate(),
        r.getStartPeriod(),
        r.getEndPeriod(),
        r.getAttendeeCount(),
        r.getContact(),
        r.getStatus(),
        r.getReviewerId(),
        r.getReviewComment(),
        r.getCreateTime(),
        r.getUpdateTime()
    );
  }
}


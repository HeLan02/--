package com.jskx.cfs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reservation")
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false, length = 50)
  private String userId;

  @Column(name = "user_name", nullable = false, length = 50)
  private String userName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "classroom_id", nullable = false)
  private Classroom classroom;

  @Column(nullable = false, length = 200)
  private String purpose;

  @Column(name = "reservation_date", nullable = false)
  private LocalDate reservationDate;

  @Column(name = "start_period", nullable = false)
  private Integer startPeriod;

  @Column(name = "end_period", nullable = false)
  private Integer endPeriod;

  @Column(name = "attendee_count", nullable = false)
  private Integer attendeeCount;

  @Column(nullable = false, length = 100)
  private String contact;

  @Column(nullable = false)
  private Integer status;

  @Column(name = "reviewer_id", length = 50)
  private String reviewerId;

  @Column(name = "review_comment", length = 300)
  private String reviewComment;

  @Column(name = "create_time", nullable = false)
  private LocalDateTime createTime;

  @Column(name = "update_time", nullable = false)
  private LocalDateTime updateTime;

  @PrePersist
  void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    createTime = now;
    updateTime = now;
  }

  @PreUpdate
  void preUpdate() {
    updateTime = LocalDateTime.now();
  }
}


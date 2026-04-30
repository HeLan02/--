package com.jskx.cfs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "course_schedule")
public class CourseSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "classroom_id", nullable = false)
  private Classroom classroom;

  @Column(name = "course_name", nullable = false, length = 100)
  private String courseName;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek; // 1-7

  @Column(name = "start_period", nullable = false)
  private Integer startPeriod;

  @Column(name = "end_period", nullable = false)
  private Integer endPeriod;

  @Column(name = "week_start", nullable = false)
  private Integer weekStart;

  @Column(name = "week_end", nullable = false)
  private Integer weekEnd;

  @Column(length = 50)
  private String teacher;
}


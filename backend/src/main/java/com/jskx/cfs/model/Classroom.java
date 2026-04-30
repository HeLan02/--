package com.jskx.cfs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "classroom")
public class Classroom {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "building_id", nullable = false)
  private Building building;

  @Column(name = "room_number", nullable = false, length = 20)
  private String roomNumber;

  @Column(nullable = false)
  private Integer capacity;

  @Column(name = "has_multimedia", nullable = false)
  @Convert(converter = BooleanTinyIntConverter.class)
  private Boolean hasMultimedia;

  @Column(name = "has_ac", nullable = false)
  @Convert(converter = BooleanTinyIntConverter.class)
  private Boolean hasAc;
}


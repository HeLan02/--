package com.jskx.cfs.repo;

import com.jskx.cfs.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Query("""
      select r from Reservation r
      where r.userId = :userId
        and (:status is null or r.status = :status)
      order by r.createTime desc
      """)
  List<Reservation> findMy(@Param("userId") String userId, @Param("status") Integer status);

  @Query("""
      select r from Reservation r
      where r.status = 0
      order by r.createTime asc
      """)
  List<Reservation> findPending();

  @Query("""
      select r from Reservation r
      where r.classroom.id = :classroomId
        and r.reservationDate = :date
        and r.status = :status
        and r.startPeriod <= :endPeriod
        and r.endPeriod >= :startPeriod
      """)
  List<Reservation> findApprovedOverlaps(@Param("classroomId") Long classroomId,
      @Param("date") LocalDate date,
      @Param("startPeriod") Integer startPeriod,
      @Param("endPeriod") Integer endPeriod,
      @Param("status") Integer status);

  @Query("""
      select r from Reservation r
      where r.reservationDate = :date
        and r.status = :status
        and r.startPeriod <= :endPeriod
        and r.endPeriod >= :startPeriod
      """)
  List<Reservation> findApprovedOverlapsAllClassrooms(@Param("date") LocalDate date,
      @Param("startPeriod") Integer startPeriod,
      @Param("endPeriod") Integer endPeriod,
      @Param("status") Integer status);
}


package com.jskx.cfs.repo;

import com.jskx.cfs.model.Classroom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
  List<Classroom> findByBuildingIdOrderByRoomNumberAsc(Long buildingId);

  @Query("""
      select c from Classroom c
      where (:buildingId is null or c.building.id = :buildingId)
        and (:minCapacity is null or c.capacity >= :minCapacity)
      order by c.building.id asc, c.roomNumber asc
      """)
  List<Classroom> listForQuery(@Param("buildingId") Long buildingId, @Param("minCapacity") Integer minCapacity);
}


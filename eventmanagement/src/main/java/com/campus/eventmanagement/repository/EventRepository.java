package com.campus.eventmanagement.repository;

import com.campus.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDepartment(String department);

    List<Event> findByEventType(String eventType);

    List<Event> findByDepartmentAndEventType(String department, String eventType);

    @Query("SELECT e FROM Event e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Event> searchByKeyword(@Param("keyword") String keyword);
}
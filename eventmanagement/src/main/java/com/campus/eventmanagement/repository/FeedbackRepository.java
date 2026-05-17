package com.campus.eventmanagement.repository;

import com.campus.eventmanagement.model.Feedback;
import com.campus.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByEvent(Event event);
    @Transactional
    void deleteByEvent(Event event);
}
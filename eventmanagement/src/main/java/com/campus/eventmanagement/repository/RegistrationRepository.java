package com.campus.eventmanagement.repository;

import com.campus.eventmanagement.model.Registration;
import com.campus.eventmanagement.model.User;
import com.campus.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUser(User user);

    List<Registration> findByEvent(Event event);

    Optional<Registration> findByEventAndUser(Event event, User user);

    long countByEvent(Event event);
    @Transactional
    void deleteByEvent(Event event);
}
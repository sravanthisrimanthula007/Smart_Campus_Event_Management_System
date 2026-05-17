package com.campus.eventmanagement.service;

import com.campus.eventmanagement.model.Event;
import com.campus.eventmanagement.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private com.campus.eventmanagement.repository.RegistrationRepository registrationRepository;

    @Autowired
    private com.campus.eventmanagement.repository.FeedbackRepository feedbackRepository;
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        registrationRepository.deleteByEvent(event);
        feedbackRepository.deleteByEvent(event);
        eventRepository.deleteById(id);
    }
    public List<Event> searchEvents(String department, String eventType) {
        if (department != null && !department.isEmpty() &&
            eventType != null && !eventType.isEmpty()) {
            return eventRepository.findByDepartmentAndEventType(department, eventType);
        } else if (department != null && !department.isEmpty()) {
            return eventRepository.findByDepartment(department);
        } else if (eventType != null && !eventType.isEmpty()) {
            return eventRepository.findByEventType(eventType);
        }
        return eventRepository.findAll();
    }
    public Event cancelEvent(Long id) {
        Event event = getEventById(id);
        event.setStatus("CANCELLED");
        return eventRepository.save(event);
    }

    public Event postponeEvent(Long id, String newDate) {
        Event event = getEventById(id);
        event.setStatus("POSTPONED");
        event.setEventDate(java.time.LocalDate.parse(newDate));
        return eventRepository.save(event);
    }
}
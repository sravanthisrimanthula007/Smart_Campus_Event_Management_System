package com.campus.eventmanagement.service;

import com.campus.eventmanagement.model.Event;
import com.campus.eventmanagement.model.Registration;
import com.campus.eventmanagement.model.User;
import com.campus.eventmanagement.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventService eventService;

    public String registerForEvent(Event event, User user) {
        if (registrationRepository.findByEventAndUser(event, user).isPresent()) {
            return "ALREADY_REGISTERED";
        }
        if (event.getRegisteredCount() >= event.getTotalSeats()) {
            return "SEATS_FULL";
        }
        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegisteredOn(LocalDate.now());
        registration.setStatus("CONFIRMED");
        registrationRepository.save(registration);

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventService.saveEvent(event);

        return "SUCCESS";
    }

    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    public List<Registration> getRegistrationsByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }

    public long countByEvent(Event event) {
        return registrationRepository.countByEvent(event);
    }
}
package com.campus.eventmanagement.scheduler;

import com.campus.eventmanagement.model.Registration;
import com.campus.eventmanagement.repository.RegistrationRepository;
import com.campus.eventmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    // Every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Registration> allRegistrations = registrationRepository.findAll();

        for (Registration reg : allRegistrations) {
            if (reg.getEvent().getEventDate().equals(LocalDate.now())
                    && "ACTIVE".equals(reg.getEvent().getStatus())) {
                emailService.sendReminderEmail(
                    reg.getUser().getEmail(),
                    reg.getUser().getName(),
                    reg.getEvent().getTitle(),
                    reg.getEvent().getEventDate().toString(),
                    reg.getEvent().getVenue()
                );
            }
        }
    }
}
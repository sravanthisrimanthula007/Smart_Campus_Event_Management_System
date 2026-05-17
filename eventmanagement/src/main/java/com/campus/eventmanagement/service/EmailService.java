package com.campus.eventmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReminderEmail(String toEmail, String studentName, String eventTitle, String eventDate, String venue) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reminder: " + eventTitle + " is Tomorrow!");
        message.setText(
            "Dear " + studentName + ",\n\n" +
            "This is a reminder that you are registered for:\n\n" +
            "Event: " + eventTitle + "\n" +
            "Date: " + eventDate + "\n" +
            "Venue: " + venue + "\n\n" +
            "Please be on time!\n\n" +
            "Regards,\nCampus Events Team"
        );
        mailSender.send(message);
    }
}
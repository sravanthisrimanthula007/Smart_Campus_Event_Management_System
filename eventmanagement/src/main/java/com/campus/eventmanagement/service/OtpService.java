package com.campus.eventmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private Map<String, String> otpStore = new HashMap<>();

    public String generateAndSendOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Campus Events - OTP Verification");
        message.setText("Your OTP for Campus Events registration is: " + otp +
                "\n\nThis OTP is valid for 10 minutes." +
                "\n\nDo not share this OTP with anyone." +
                "\n\nRegards,\nCampus Events Team");

        mailSender.send(message);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }
    public void sendConfirmationEmail(String email, String name, String eventTitle, String eventDate, String venue) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Registration Confirmed - " + eventTitle);
        message.setText(
            "Dear " + name + ",\n\n" +
            "Your registration has been confirmed!\n\n" +
            "Event Details:\n" +
            "Event: " + eventTitle + "\n" +
            "Date: " + eventDate + "\n" +
            "Venue: " + venue + "\n\n" +
            "Please arrive 10 minutes before the event starts.\n\n" +
            "Regards,\n" +
            "Campus Events Team"
        );
        mailSender.send(message);
    }
    public void sendSummaryEmail(String summaryText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(fromEmail);
        message.setSubject("Campus Events - Daily Summary Report");
        message.setText(summaryText);
        mailSender.send(message);
    }
    public void sendEventCancelEmail(String email, String name, String eventTitle, String eventDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Event Cancelled - " + eventTitle);
        message.setText(
            "Dear " + name + ",\n\n" +
            "We regret to inform you that the following event has been CANCELLED:\n\n" +
            "Event: " + eventTitle + "\n" +
            "Originally scheduled on: " + eventDate + "\n\n" +
            "We apologize for the inconvenience caused.\n" +
            "Please check our portal for upcoming events.\n\n" +
            "Regards,\n" +
            "Campus Events Team"
        );
        mailSender.send(message);
    }

    public void sendEventPostponeEmail(String email, String name, String eventTitle, String newDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Event Postponed - " + eventTitle);
        message.setText(
            "Dear " + name + ",\n\n" +
            "Please note that the following event has been POSTPONED:\n\n" +
            "Event: " + eventTitle + "\n" +
            "New Date: " + newDate + "\n\n" +
            "Please update your schedule accordingly.\n" +
            "We apologize for any inconvenience.\n\n" +
            "Regards,\n" +
            "Campus Events Team"
        );
        mailSender.send(message);
    }
}
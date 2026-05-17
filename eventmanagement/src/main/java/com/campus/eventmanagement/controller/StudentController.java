package com.campus.eventmanagement.controller;

import com.campus.eventmanagement.model.Event;
import com.campus.eventmanagement.model.User;
import com.campus.eventmanagement.model.Feedback;
import com.campus.eventmanagement.repository.UserRepository;
import com.campus.eventmanagement.service.EventService;
import com.campus.eventmanagement.service.RegistrationService;
import com.campus.eventmanagement.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.campus.eventmanagement.service.OtpService;
@Controller
public class StudentController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private OtpService otpService;
    @GetMapping("/events")
    public String browseEvents(@RequestParam(required = false) String department,
                               @RequestParam(required = false) String eventType,
                               Model model) {
        model.addAttribute("events", eventService.searchEvents(department, eventType));
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedType", eventType);
        return "events";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "event-detail";
    }

    @PostMapping("/events/{id}/register")
    public String register(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        String username = authentication.getName();

        User user = userRepository.findByEmail(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(username);
            newUser.setName(username);
            newUser.setRole("STUDENT");
            newUser.setPassword("N/A");
            return userRepository.save(newUser);
        });

        String result = registrationService.registerForEvent(event, user);

        switch (result) {
        case "SUCCESS" -> {
            redirectAttributes.addFlashAttribute("success", "Registered successfully! Confirmation email sent.");
            try {
                otpService.sendConfirmationEmail(user.getEmail(), user.getName(), event.getTitle(), event.getEventDate().toString(), event.getVenue());
            } catch (Exception e) {
                // email failed but registration succeeded
            }
        }
            case "ALREADY_REGISTERED" -> redirectAttributes.addFlashAttribute("error", "You are already registered!");
            case "SEATS_FULL" -> redirectAttributes.addFlashAttribute("error", "Sorry, no seats available!");
        }
        return "redirect:/events/" + id;
    }

    @GetMapping("/my-events")
    public String myEvents(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            model.addAttribute("registrations",
                registrationService.getRegistrationsByUser(user));
        } else {
            model.addAttribute("registrations", new java.util.ArrayList<>());
        }
        return "my-events";
    }
    @PostMapping("/events/{id}/feedback")
    public String submitFeedback(@PathVariable Long id,
                                 @RequestParam int rating,
                                 @RequestParam String comment,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        String username = authentication.getName();

        User user = userRepository.findByEmail(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(username);
            newUser.setName(username);
            newUser.setRole("STUDENT");
            newUser.setPassword("N/A");
            return userRepository.save(newUser);
        });

        Feedback feedback = new Feedback();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedbackService.saveFeedback(feedback);

        redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully!");
        return "redirect:/events/" + id;
    }
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }
}
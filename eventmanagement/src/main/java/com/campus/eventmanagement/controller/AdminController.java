package com.campus.eventmanagement.controller;

import com.campus.eventmanagement.model.Event;
import com.campus.eventmanagement.service.EventService;
import com.campus.eventmanagement.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private com.campus.eventmanagement.repository.UserRepository userRepository;

    @Autowired
    private com.campus.eventmanagement.repository.RegistrationRepository registrationRepository;
    
    @Autowired
    private com.campus.eventmanagement.service.OtpService otpService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", events.size());
        model.addAttribute("totalStudents", userRepository.countByRole("STUDENT"));
        model.addAttribute("totalRegistrations", registrationRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute Event event,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/event-form";
        }
        eventService.saveEvent(event);
        redirectAttributes.addFlashAttribute("success", "Event saved successfully!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "admin/event-form";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        
        Map<Long, Long> registrationCounts = new java.util.HashMap<>();
        for (Event event : events) {
            registrationCounts.put(event.getId(), registrationService.countByEvent(event));
        }
        model.addAttribute("registrationCounts", registrationCounts);
        return "admin/stats";
    }
    @GetMapping("/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getRegistrationsByEvent(event));
        return "admin/registrations";
    }
    @GetMapping("/send-summary")
    public String sendSummary(RedirectAttributes redirectAttributes) {
        List<Event> events = eventService.getAllEvents();
        StringBuilder summary = new StringBuilder();
        summary.append("Campus Events - Daily Summary Report\n");
        summary.append("=====================================\n\n");
        summary.append("Total Events: ").append(events.size()).append("\n");
        summary.append("Total Registrations: ").append(registrationRepository.count()).append("\n\n");
        summary.append("Event-wise Registrations:\n");
        summary.append("-------------------------\n");
        for (Event event : events) {
            long count = registrationService.countByEvent(event);
            summary.append("• ").append(event.getTitle())
                   .append(" (").append(event.getDepartment()).append(")")
                   .append(" → ").append(count).append("/").append(event.getTotalSeats())
                   .append(" registrations\n");
        }
        summary.append("\nGenerated on: ").append(java.time.LocalDateTime.now());

        try {
            otpService.sendSummaryEmail(summary.toString());
            redirectAttributes.addFlashAttribute("success", "Summary email sent to admin!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send summary email.");
        }
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/events/cancel/{id}")
    public String cancelEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Event event = eventService.cancelEvent(id);
        List<com.campus.eventmanagement.model.Registration> registrations = 
            registrationService.getRegistrationsByEvent(event);
        for (com.campus.eventmanagement.model.Registration reg : registrations) {
            try {
                otpService.sendEventCancelEmail(
                    reg.getUser().getEmail(),
                    reg.getUser().getName(),
                    event.getTitle(),
                    event.getEventDate().toString()
                );
            } catch (Exception e) {
                // continue even if email fails
            }
        }
        redirectAttributes.addFlashAttribute("success", 
            "Event cancelled and students notified!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/postpone/{id}")
    public String postponeEvent(@PathVariable Long id,
                                 @RequestParam String newDate,
                                 RedirectAttributes redirectAttributes) {
        Event event = eventService.postponeEvent(id, newDate);
        List<com.campus.eventmanagement.model.Registration> registrations = 
            registrationService.getRegistrationsByEvent(event);
        for (com.campus.eventmanagement.model.Registration reg : registrations) {
            try {
                otpService.sendEventPostponeEmail(
                    reg.getUser().getEmail(),
                    reg.getUser().getName(),
                    event.getTitle(),
                    event.getEventDate().toString()
                );
            } catch (Exception e) {
                // continue even if email fails
            }
        }
        redirectAttributes.addFlashAttribute("success", 
            "Event postponed and students notified!");
        return "redirect:/admin/dashboard";
    }
}
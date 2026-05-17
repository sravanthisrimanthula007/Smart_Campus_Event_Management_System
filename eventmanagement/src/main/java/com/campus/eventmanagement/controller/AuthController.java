package com.campus.eventmanagement.controller;

import com.campus.eventmanagement.service.OtpService;
import com.campus.eventmanagement.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private OtpService otpService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/events";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String phone,
                         @RequestParam String department,
                         @RequestParam String yearOfStudy,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        if (studentService.emailExists(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already registered! Please login.");
            return "redirect:/signup";
        }

        session.setAttribute("signup_name", name);
        session.setAttribute("signup_email", email);
        session.setAttribute("signup_password", password);
        session.setAttribute("signup_phone", phone);
        session.setAttribute("signup_department", department);
        session.setAttribute("signup_year", yearOfStudy);

        try {
            otpService.generateAndSendOtp(email);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/verify-otp";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send OTP. Check your email.");
            return "redirect:/signup";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("signup_email");
        if (email == null) {
            return "redirect:/signup";
        }

        if (!otpService.verifyOtp(email, otp)) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP! Please try again.");
            return "redirect:/verify-otp";
        }

        String name = (String) session.getAttribute("signup_name");
        String password = (String) session.getAttribute("signup_password");
        String phone = (String) session.getAttribute("signup_phone");
        String dept = (String) session.getAttribute("signup_department");
        String year = (String) session.getAttribute("signup_year");

        studentService.registerStudent(name, email, password, phone, dept, year);

        session.removeAttribute("signup_name");
        session.removeAttribute("signup_email");
        session.removeAttribute("signup_password");
        session.removeAttribute("signup_phone");
        session.removeAttribute("signup_department");
        session.removeAttribute("signup_year");

        try {
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    email, null,
                    java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_STUDENT")
                    )
                );
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(authToken);
            session.setAttribute(
                org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                org.springframework.security.core.context.SecurityContextHolder.getContext()
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("success", "Account created! Please login.");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("success", "Welcome! Account verified successfully!");
        return "redirect:/events";
    }
}
package com.campus.eventmanagement.service;

import com.campus.eventmanagement.model.User;
import com.campus.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private UserRepository userRepository;

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public String registerStudent(String name, String email, String password,
                                   String phone, String department, String yearOfStudy) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "EMAIL_EXISTS";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRole("STUDENT");
        user.setPhone(phone);
        user.setDepartment(department);
        user.setYearOfStudy(yearOfStudy);
        userRepository.save(user);
        return "SUCCESS";
    }
}
package com.campus.eventmanagement.service;

import com.campus.eventmanagement.model.Feedback;
import com.campus.eventmanagement.model.Event;
import com.campus.eventmanagement.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByEvent(Event event) {
        return feedbackRepository.findByEvent(event);
    }
}
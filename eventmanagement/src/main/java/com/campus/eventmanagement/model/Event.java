package com.campus.eventmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    @NotNull
    @Size(min = 10, max = 500)
    private String description;

    @NotNull
    private LocalDate eventDate;

    @NotNull
    private String department;

    @NotNull
    private String eventType;

    private String venue;

    private int totalSeats;

    private int registeredCount;
    private String status = "ACTIVE";
}
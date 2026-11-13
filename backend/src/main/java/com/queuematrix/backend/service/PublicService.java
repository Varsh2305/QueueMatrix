package com.queuematrix.backend.service;

import com.queuematrix.backend.dto.response.PublicHomeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicService {

    public PublicHomeResponse getPublicHome() {
        List<PublicHomeResponse.CategoryInfo> categories = List.of(
                new PublicHomeResponse.CategoryInfo("HOSPITALS", "Hospitals", "Outpatient clinics, diagnostic centers, and triage desks", "hospital"),
                new PublicHomeResponse.CategoryInfo("RESTAURANTS", "Restaurants", "Table seating waitlists, takeaway pickup, and host stations", "utensils"),
                new PublicHomeResponse.CategoryInfo("BANKS", "Banks", "Teller counters, loan desks, and branch advisory services", "landmark"),
                new PublicHomeResponse.CategoryInfo("GOVERNMENT_OFFICES", "Government Offices", "Citizen service centers, licensing, and documentation desks", "building-columns"),
                new PublicHomeResponse.CategoryInfo("UNIVERSITIES", "Universities", "Admissions, student records, registrar, and fee counters", "graduation-cap"),
                new PublicHomeResponse.CategoryInfo("OTHER", "Other Service Organizations", "Telecom centers, repair depots, embassies, and retail stores", "briefcase")
        );

        List<String> features = List.of(
                "Real-time queue tracking and digital token boards",
                "Secure password-based authentication with BCrypt & JWT",
                "Multi-counter routing and automated token calls",
                "Virtual queues allowing customers to wait comfortably anywhere"
        );

        return new PublicHomeResponse(
                "QueueMatrix",
                "Welcome to QueueMatrix",
                "Smarter Queues. Better Experiences.",
                "QueueMatrix manages service queues, bookings and customer journeys.",
                categories,
                features
        );
    }
}

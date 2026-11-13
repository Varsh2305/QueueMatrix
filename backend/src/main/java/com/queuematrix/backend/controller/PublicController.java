package com.queuematrix.backend.controller;

import com.queuematrix.backend.dto.response.ApiResponse;
import com.queuematrix.backend.dto.response.PublicHomeResponse;
import com.queuematrix.backend.service.PublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<PublicHomeResponse>> getPublicHome() {
        return ResponseEntity.ok(ApiResponse.success("QueueMatrix public home details", publicService.getPublicHome()));
    }
}

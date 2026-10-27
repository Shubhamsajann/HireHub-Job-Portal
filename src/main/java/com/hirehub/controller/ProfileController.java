package com.hirehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/api/profile")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("You are authenticated!");
    }
}

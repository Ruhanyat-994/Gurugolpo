package com.loginFeature.login.controller;

import com.loginFeature.login.entity.University;
import com.loginFeature.login.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UniversityController {
    
    @Autowired
    private UniversityService universityService;
    
    @GetMapping
    public ResponseEntity<List<University>> getAllUniversities() {
        List<University> universities = universityService.getAllActiveUniversities();
        return ResponseEntity.ok(universities);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<University> getUniversityById(@PathVariable Long id) {
        return universityService.getUniversityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<University> createUniversity(@RequestBody University university) {
        University created = universityService.createUniversity(university);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<University> updateUniversity(@PathVariable Long id, @RequestBody University university) {
        try {
            University updated = universityService.updateUniversity(id, university);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUniversity(@PathVariable Long id) {
        boolean deleted = universityService.deleteUniversity(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> initializeDefaultUniversities() {
        universityService.initializeDefaultUniversities();
        return ResponseEntity.ok("Default universities initialized successfully");
    }
}

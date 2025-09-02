package com.loginFeature.login.service;

import com.loginFeature.login.entity.University;
import com.loginFeature.login.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {
    
    @Autowired
    private UniversityRepository universityRepository;
    
    public List<University> getAllActiveUniversities() {
        return universityRepository.findAllActive();
    }
    
    public Optional<University> getUniversityById(Long id) {
        return universityRepository.findById(id);
    }
    
    public Optional<University> getUniversityByName(String name) {
        return universityRepository.findByName(name);
    }
    
    public University createUniversity(University university) {
        // Set default values
        if (university.getIsActive() == null) {
            university.setIsActive(true);
        }
        return universityRepository.save(university);
    }
    
    public University updateUniversity(Long id, University university) {
        Optional<University> existing = universityRepository.findById(id);
        if (existing.isPresent()) {
            University existingUniversity = existing.get();
            existingUniversity.setName(university.getName());
            existingUniversity.setDescription(university.getDescription());
            existingUniversity.setIsActive(university.getIsActive());
            return universityRepository.save(existingUniversity);
        }
        throw new RuntimeException("University not found with id: " + id);
    }
    
    public boolean deleteUniversity(Long id) {
        Optional<University> existing = universityRepository.findById(id);
        if (existing.isPresent()) {
            University university = existing.get();
            university.setIsActive(false);
            universityRepository.save(university);
            return true;
        }
        return false;
    }
    
    public void initializeDefaultUniversities() {
        // Check if universities already exist
        List<University> existing = universityRepository.findAllActive();
        if (!existing.isEmpty()) {
            return; // Already initialized
        }
        
        // Create default universities
        String[] defaultUniversities = {
            "United International University",
            "University of Dhaka",
            "Bangladesh University of Engineering and Technology",
            "Chittagong University",
            "Khulna University",
            "Rajshahi University",
            "Jahangirnagar University",
            "Shahjalal University of Science and Technology",
            "Dhaka University of Engineering and Technology",
            "Bangladesh Agricultural University"
        };
        
        for (String name : defaultUniversities) {
            University university = new University();
            university.setName(name);
            university.setDescription("Leading educational institution in Bangladesh");
            university.setIsActive(true);
            universityRepository.save(university);
        }
    }
}

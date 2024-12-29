package com.example.HyphaemaProgressTracker.services;

import org.springframework.stereotype.Service;

import com.example.HyphaemaProgressTracker.entities.Patient;

/**
 * This service provides methods for creating and managing Patient objects.
 */
@Service
public class PatientService {
    public Patient createNewPatient() {
        Patient patient = new Patient();
        return patient;
    }
}



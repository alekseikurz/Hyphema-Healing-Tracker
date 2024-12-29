package com.example.HyphaemaProgressTracker.securityConfiguration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.HyphaemaProgressTracker.entities.Patient;
import com.example.HyphaemaProgressTracker.entities.PatientRepository;


/**
 * Custom implementation of the UserDetailsService interface used by Spring Security.
 * This service is responsible for loading user-specific data, such as credentials and roles,
 * from the database (Patient entity) for authentication and authorization purposes.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PatientRepository patientRepository;

    /**
     * Constructor that injects the PatientRepository dependency.
     * The PatientRepository is used to fetch patient data from the database based on login credentials.
     *
     * @param patientRepository the repository for accessing patient data in the database.
     */
    public CustomUserDetailsService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }   

    /**
     * Loads user-specific data (username, password, and roles) based on the provided username.
     * This method is invoked by Spring Security to authenticate a user during the login process.
     *
     * @param username the username of the patient (which is stored in the database as a login).
     * @return a fully populated UserDetails object containing the patient's username, password, and roles.
     * @throws UsernameNotFoundException if the username is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the patient from the database using the provided username (login).
        Patient patient = patientRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Return a UserDetails object (from Spring Security) that represents the patient.
        // The user's roles are split from the comma-separated string stored in the 'roles' field.        
        return org.springframework.security.core.userdetails.User
                .withUsername(patient.getLogin()) 
                .password(patient.getPassword()) 
                .roles(patient.getRoles().split(",")) 
                .build(); 
    }
}

package com.example.HyphaemaProgressTracker.dataLoader;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.HyphaemaProgressTracker.entities.Eye;
import com.example.HyphaemaProgressTracker.entities.EyeRepository;
import com.example.HyphaemaProgressTracker.entities.Injury;
import com.example.HyphaemaProgressTracker.entities.InjuryRepository;
import com.example.HyphaemaProgressTracker.entities.Patient;
import com.example.HyphaemaProgressTracker.entities.PatientRepository;
import com.example.HyphaemaProgressTracker.enums.EyeSide;


/**
 * Component responsible for preloading initial data into the application.
 * <p>
 * The {@code DataLoader} class implements {@link CommandLineRunner}, allowing it to execute custom
 * logic when the Spring application starts. It is typically used for populating the database with
 * initial data such as test users, injuries, or other entities.
 * </p>
 * 
 * Responsibilities:
 * <ul>
 *     <li>Interacts with repositories to manage database entities</li>
 *     <li>Encrypts sensitive data (e.g., passwords) before storing it</li>
 *     <li>Runs custom initialization logic on application startup</li>
 * </ul>
 * 
 * Dependencies:
 * <ul>
 *     <li>{@link PatientRepository} for managing {@link Patient} entities</li>
 *     <li>{@link InjuryRepository} for managing {@link Injury} entities</li>
 *     <li>{@link EyeRepository} for managing {@link Eye} entities</li>
 *     <li>{@link BCryptPasswordEncoder} for securely encoding passwords</li>
 * </ul>
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final InjuryRepository injuryRepository;
    private final EyeRepository eyeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructs a new {@code DataLoader} with the required dependencies.
     * 
     * @param patientRepository the repository for {@link Patient} entities
     * @param injuryRepository the repository for {@link Injury} entities
     * @param eyeRepository the repository for {@link Eye} entities
     * @param passwordEncoder the encoder for securely hashing passwords
     */
    public DataLoader(PatientRepository patientRepository, InjuryRepository injuryRepository, EyeRepository eyeRepository, BCryptPasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.injuryRepository = injuryRepository;
        this.eyeRepository = eyeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes custom logic during application startup.
     * <p>
     * This method is invoked automatically after the application context is loaded.
     * It can be used to preload the database with initial data or perform setup tasks.
     * </p>
     * 
     * @param args command-line arguments passed to the application
     */
    @Override
    public void run(String... args) {
       
    }
}

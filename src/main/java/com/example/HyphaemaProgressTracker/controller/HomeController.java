package com.example.HyphaemaProgressTracker.controller;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.HyphaemaProgressTracker.entities.Patient;
import com.example.HyphaemaProgressTracker.entities.PatientRepository;
import com.example.HyphaemaProgressTracker.services.PatientService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller responsible for handling home page, user registration, and file serving.
 * <p>
 * This controller manages the application's homepage, processes user registration, 
 * and serves uploaded files for further analysis.
 * </p>
 * 
 * Features:
 * <ul>
 *     <li>Renders the homepage with login and registration forms</li>
 *     <li>Handles user registration and saves encrypted passwords</li>
 *     <li>Serves uploaded files for external Python-based analysis</li>
 * </ul>
 * 
 * Dependencies:
 * <ul>
 *     <li>{@link PatientRepository} for saving and retrieving patient data</li>
 *     <li>{@link BCryptPasswordEncoder} for securely encrypting passwords</li>
 *     <li>{@link PatientService} for creating new patient instances</li>
 * </ul>
 * 
 * Endpoints:
 * <ul>
 *     <li>{@code GET /} - Displays the homepage with login and registration forms</li>
 *     <li>{@code POST /register} - Processes user registration</li>
 *     <li>{@code GET /uploads/{filename}} - Serves uploaded files</li>
 * </ul>
 */
@Controller
public class HomeController {

    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PatientService patientService; 

    /**
     * Constructs a new {@code HomeController} with required dependencies.
     * 
     * @param patientRepository the repository for managing {@link Patient} data
     * @param passwordEncoder the password encoder for encrypting user passwords
     * @param patientService the service for creating new patient instances
     */
    public HomeController(PatientRepository patientRepository, BCryptPasswordEncoder passwordEncoder, PatientService patientService) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientService = patientService;
    }

    /**
     * Displays the homepage with login and registration forms.
     * <p>
     * Adds the current request URI and a new {@link Patient} instance to the model 
     * for rendering the registration form.
     * </p>
     * 
     * @param model the {@link Model} to pass attributes to the view
     * @param request the {@link HttpServletRequest} containing the current request details
     * @return the name of the view template to render
     */
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("patient", patientService.createNewPatient()); 
        return "index"; 
    }

    /**
     * Processes user registration by encrypting the password and saving the patient record.
     * 
     * @param patient the {@link Patient} instance populated from the registration form
     * @return a redirect to the homepage after successful registration
     */
    @PostMapping("/register")
    public String register(@ModelAttribute("patient") Patient patient) {
        String hashedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(hashedPassword);
        patientRepository.save(patient);
        return "redirect:/"; 
    }

    /**
     * Serves uploaded files for further analysis.
     * <p>
     * Validates the existence of the requested file and returns it as a {@link Resource}.
     * Throws a {@link ResponseStatusException} if the file is not found.
     * </p>
     * 
     * @param filename the name of the file to be served
     * @return the file wrapped in a {@link ResponseEntity} for downloading
     */
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {
        Path file = Paths.get("src/main/resources/static/uploads").resolve(filename);
        if (!Files.exists(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Datei nicht gefunden: " + filename);
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache().cachePrivate())
                .body(resource);
    }

}
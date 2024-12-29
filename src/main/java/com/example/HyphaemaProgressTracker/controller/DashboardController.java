package com.example.HyphaemaProgressTracker.controller;

import org.springframework.web.bind.annotation.*;

import com.example.HyphaemaProgressTracker.entities.EyeRepository;
import com.example.HyphaemaProgressTracker.entities.InjuryRepository;
import com.example.HyphaemaProgressTracker.entities.Patient;
import com.example.HyphaemaProgressTracker.entities.PatientRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


/**
 * Controller responsible for handling dashboard-related requests.
 * <p>
 * This controller provides a web-based interface for viewing and managing 
 * patient-specific data through a dashboard view.
 * </p>
 * 
 * Features:
 * <ul>
 *     <li>Displays the dashboard page for the logged-in user</li>
 *     <li>Retrieves and passes patient-specific information to the view</li>
 * </ul>
 * 
 * Dependencies:
 * <ul>
 *     <li>{@link PatientRepository} for accessing patient data</li>
 *     <li>{@link InjuryRepository} for accessing injury data (future extensions)</li>
 *     <li>{@link EyeRepository} for accessing eye data (future extensions)</li>
 * </ul>
 * 
 * Endpoint:
 * <ul>
 *     <li>{@code GET /dashboard} - Renders the dashboard page with relevant user data</li>
 * </ul>
 * 
 * View Template:
 * <ul>
 *     <li>Returns the {@code dashboard} template for rendering</li>
 * </ul>
 */
@Controller
public class DashboardController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private InjuryRepository injuryRepository;

    @Autowired
    private EyeRepository eyeRepository;

    /**
     * Handles requests to the dashboard page.
     * <p>
     * Retrieves information about the currently authenticated user, including their
     * username and patient ID, and passes it to the view for rendering the dashboard.
     * </p>
     * 
     * @param model the {@link Model} object used to pass attributes to the view
     * @param request the {@link HttpServletRequest} containing request details
     * @param principal the {@link Principal} object representing the authenticated user
     * @return the name of the view template for the dashboard
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpServletRequest request, Principal principal) {
        String username = principal.getName(); 
        Patient patient = patientRepository.findByLogin(username).get(); 
        model.addAttribute("username", username);
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("patientId", patient.getId()); 
        return "dashboard"; 
    }
}

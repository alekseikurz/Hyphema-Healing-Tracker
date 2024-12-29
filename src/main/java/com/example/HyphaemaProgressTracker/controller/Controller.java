package com.example.HyphaemaProgressTracker.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.HyphaemaProgressTracker.dto.EyeResultDTO;
import com.example.HyphaemaProgressTracker.dto.SaveResultRequest;
import com.example.HyphaemaProgressTracker.entities.Eye;
import com.example.HyphaemaProgressTracker.entities.EyeRepository;
import com.example.HyphaemaProgressTracker.entities.Injury;
import com.example.HyphaemaProgressTracker.entities.InjuryRepository;
import com.example.HyphaemaProgressTracker.entities.Patient;
import com.example.HyphaemaProgressTracker.entities.PatientRepository;
import com.example.HyphaemaProgressTracker.enums.EyeSide;
import com.example.HyphaemaProgressTracker.hyphaemaPythonIntegration.PythonIntegration;
import com.example.HyphaemaProgressTracker.services.EyeService;
import com.example.HyphaemaProgressTracker.services.InjuryService;
import com.example.HyphaemaProgressTracker.services.PatientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * REST Controller that provides endpoints for managing Patients, Injuries, and Eyes,
 * as well as analyzing and storing hyphema-related data.
 * <p>
 * This controller serves as the central API for handling CRUD operations and 
 * advanced functionalities such as hyphema analysis, linking injuries to patients, 
 * and linking eyes to injuries.
 * </p>
 * <p>
 * Mapped to the base path `/api`.
 * </p>
 * 
 * Endpoints include:
 * <ul>
 *     <li>CRUD operations for Patients, Injuries, and Eyes</li>
 *     <li>Linking Injuries to Patients</li>
 *     <li>Linking Eyes to Injuries</li>
 *     <li>Running hyphema analysis via an integrated Python application</li>
 *     <li>Storing analysis results as persistent Eye records</li>
 * </ul>
 * 
 * Dependencies:
 * <ul>
 *     <li>{@link PatientRepository} for Patient data management</li>
 *     <li>{@link InjuryRepository} for Injury data management</li>
 *     <li>{@link EyeRepository} for Eye data management</li>
 *     <li>{@link PatientService} for business logic related to Patients</li>
 *     <li>{@link InjuryService} for business logic related to Injuries</li>
 *     <li>{@link EyeService} for business logic related to Eyes</li>
 * </ul>
 */
@RestController
@RequestMapping("/api") 
public class Controller {

    private final PatientRepository patientRepository;
    private final InjuryRepository injuryRepository;
    private final EyeRepository eyeRepository;
    private final PatientService patientService;
    private final InjuryService injuryService;
    private final EyeService eyeService;

    /**
     * Constructor for the Controller.
     * Initializes the required repositories and services.
     *
     * @param patientRepository the repository for managing patients
     * @param injuryRepository the repository for managing injuries
     * @param eyeRepository the repository for managing eyes
     * @param patientService the service for patient-related operations
     * @param injuryService the service for injury-related operations
     * @param eyeService the service for eye-related operations
     */
    public Controller(PatientRepository patientRepository,
                      InjuryRepository injuryRepository,
                      EyeRepository eyeRepository,
                      PatientService patientService,
                      InjuryService injuryService, 
                      EyeService eyeService) {
        this.patientRepository = patientRepository;
        this.injuryRepository = injuryRepository;
        this.eyeRepository = eyeRepository;
        this.patientService = patientService;
        this.injuryService = injuryService;
        this.eyeService = eyeService;
    }


    // CRUD operations for Patient

    /**
     * Creates a new patient and saves it to the repository.
     *
     * @param patient the patient object to be created
     * @return a ResponseEntity containing the created patient
     */
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        return ResponseEntity.ok(patientRepository.save(patient));
    }

    /**
     * Retrieves a patient by its ID.
     *
     * @param patientId the ID of the patient to retrieve
     * @return a ResponseEntity containing the patient if found, or a 404 status if not found
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("patientId") Long patientId) {
        Optional<Patient> patient = patientRepository.findById(patientId);
        return patient.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the information of an existing patient.
     *
     * @param patientId the ID of the patient to update
     * @param updatedPatient the updated patient data
     * @return a ResponseEntity containing the updated patient, or a 404 status if not found
     */
    @PutMapping("/{patientId}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("patientId") Long patientId, @RequestBody Patient updatedPatient) {
        return patientRepository.findById(patientId)
                .map(patient -> {
                    patient.setLogin(updatedPatient.getLogin());
                    return ResponseEntity.ok(patientRepository.save(patient));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a patient by its ID.
     *
     * @param patientId the ID of the patient to delete
     * @return a ResponseEntity with a 204 status if the deletion was successful, or a 404 status if not found
     */
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable("patientId") Long patientId) {
        if (patientRepository.existsById(patientId)) {
            patientRepository.deleteById(patientId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all patients stored in the repository.
     *
     * @return a ResponseEntity containing the list of all patients, or a 204 status if no patients are found
     */
    @GetMapping("/getAllPatients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        System.out.println("- - - - ALL PATIENTS:");
        Iterable<Patient> iterableAllPatients = patientRepository.findAll();
        List<Patient> allPatients = new ArrayList<>();
        iterableAllPatients.forEach(allPatients::add);
        allPatients.forEach(System.out::println);
        if (allPatients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allPatients);
    }


    // CRUD operations for Injury

    /**
     * Adds a new injury to a specific patient.
     *
     * @param patientId the ID of the patient to whom the injury will be added
     * @param injury the injury object to be added
     * @return a ResponseEntity containing the saved injury if the patient exists, or a 404 status if the patient is not found
     */
    @PostMapping("/{patientId}/injuries")
    public ResponseEntity<Injury> addInjuryToPatient(@PathVariable("patientId") Long patientId, @RequestBody Injury injury) {
        return patientRepository.findById(patientId)
                .map(patient -> {
                    injury.setPatient(patient);
                    return ResponseEntity.ok(injuryRepository.save(injury));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all injuries associated with a specific patient.
     *
     * @param patientId the ID of the patient whose injuries are to be retrieved
     * @return a ResponseEntity containing the list of injuries, or a 404 status if the patient is not found
     */
    @GetMapping("/{patientId}/injuries")
    public ResponseEntity<List<Injury>> getInjuriesForPatient(@PathVariable("patientId") Long patientId) {
        return patientRepository.findById(patientId)
                .map(patient -> ResponseEntity.ok(patient.getInjuries()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves eyes affected by a specific injury for a specific patient, filtered by eye side.
     *
     * @param patientId the ID of the patient
     * @param injuryId the ID of the injury
     * @param eyeSide the side of the eye (e.g., LEFT, RIGHT)
     * @return a ResponseEntity containing the list of eye results, or a 204 status if no matching eyes are found
     */
    @GetMapping("/patients/{patientId}/injuries/{injuryId}/eyes/{eyeSide}")
    public ResponseEntity<List<EyeResultDTO>> getEyesByPatientAndInjury(
            @PathVariable("patientId") Long patientId, 
            @PathVariable("injuryId") Long injuryId, 
            @PathVariable("eyeSide") EyeSide eyeSide) {
        List<EyeResultDTO> eyes = patientRepository.findEyesByPatientAndInjuryAndSide(patientId, injuryId, eyeSide);

        if (eyes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eyes);
    }

    /**
     * Retrieves all injuries associated with a specific patient.
     *
     * @param patientId the ID of the patient
     * @return a ResponseEntity containing the list of injuries, or a 204 status if no injuries are found
     */
    @GetMapping("/{patientId}/allInjuries")
    public ResponseEntity<List<Injury>> findAllInjuriesByPatient(@PathVariable("patientId") Long patientId) {
        List<Injury> allInjuriesByPatient = injuryRepository.findByPatientId(patientId);
        if (allInjuriesByPatient.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allInjuriesByPatient);
    }


    // CRUD operations and analysis for Eye

    /**
     * Adds a new eye record to a specific injury.
     *
     * @param injuryId the ID of the injury to which the eye will be associated
     * @param eye the eye object to be added
     * @return a ResponseEntity containing the saved eye if the injury exists, or a 404 status if the injury is not found
     */
    @PostMapping("/injuries/{injuryId}/eyes")
    public ResponseEntity<Eye> addEyeToInjury(@PathVariable("injuryId") Long injuryId, @RequestBody Eye eye) {
        return injuryRepository.findById(injuryId)
                .map(injury -> {
                    eye.setInjury(injury);
                    return ResponseEntity.ok(eyeRepository.save(eye));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all eye records associated with a specific injury.
     *
     * @param injuryId the ID of the injury whose eye records are to be retrieved
     * @return a ResponseEntity containing the list of eyes, or a 404 status if the injury is not found
     */
    @GetMapping("/injuries/{injuryId}/eyes")
    public ResponseEntity<List<Eye>> getEyesForInjury(@PathVariable("injuryId") Long injuryId) {
        return injuryRepository.findById(injuryId)
                .map(injury -> ResponseEntity.ok(injury.getEyes()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Runs an analysis of a hyphema using a Python application.
     *
     * @param patientId the ID of the patient
     * @param injuryId the ID of the injury
     * @param eyeSide the side of the eye being analyzed (e.g., LEFT, RIGHT)
     * @param date the date of the analysis
     * @param photo the photo of the eye to be analyzed
     * @return a ResponseEntity containing the analysis result, including the percentage of the eye affected by hyphema,
     *         or an appropriate error status if an issue occurs
     */
    @PostMapping("/{patientId}/runAnalyseHyphema")
    public ResponseEntity<?> runAnalyseHyphema(
            @PathVariable("patientId") Long patientId,
            @RequestParam("injuryId") Long injuryId,
            @RequestParam("eye") String eyeSide,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestPart("photo") MultipartFile photo) {

        try {
            // 1. Check/create folder "uploads" in folder "static"
            Path uploadDir = Paths.get("src/main/resources/static/uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir); 
            }

            // 2. Save photo
            String fileName = photo.getOriginalFilename();
            Path photoPath = uploadDir.resolve(fileName);
            Files.write(photoPath, photo.getBytes());

            // 3. Start Python analysis
            String analyzedPhotoPath = photoPath.toAbsolutePath().toString();
            String analysisResult = PythonIntegration.runHyphaemaApp(analyzedPhotoPath);

            // 4. Parse JSON result from the analysis
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> analysisData = objectMapper.readValue(analysisResult, new TypeReference<>() {});

            if (analysisData.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(analysisData.get("error"));
            }

            // 5. Compile the result
            Map<String, Object> result = new HashMap<>();
            result.put("patientId", patientId);
            result.put("injuryId", injuryId);
            result.put("eye", eyeSide);
            result.put("date", date);

            // URL of the saved image relative to the application
            String relativePath = "/uploads/" + fileName;
            result.put("processedImage", relativePath); 
            result.put("percentageOfEyeAffectedByHyphema", analysisData.get("hyphema_area_percentage"));

            return ResponseEntity.ok(result); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler bei der Analyse");
        }
    }


    /**
     * Saves the result of a hyphema analysis as a new eye record.
     *
     * @param patientId the ID of the patient
     * @param request the request containing details of the eye record to be saved
     * @return a ResponseEntity with a success message if the save is successful,
     *         or an error status if an issue occurs
     */
    @PostMapping("/{patientId}/saveResult")
    public ResponseEntity<?> saveResult(
            @PathVariable("patientId") Long patientId,
            @RequestBody SaveResultRequest request) {

        try {
            Injury injury = injuryRepository.findById(request.getInjuryId())
                    .orElseThrow(() -> new RuntimeException("Injury nicht gefunden"));

            Eye eye = eyeService.createNewEye(); 
            eye.setPercentageOfEyeAffectedByHyphema(request.getPercentageOfEyeAffectedByHyphema());
            eye.setDate(LocalDate.parse(request.getDate()));
            eye.setSide(EyeSide.valueOf(request.getEye()));
            eye.setInjury(injury);

            eyeRepository.save(eye);

            return ResponseEntity.ok("Ergebnis erfolgreich gespeichert");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Speichern");
        }
    }

}


    


package com.example.HyphaemaProgressTracker.entities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.HyphaemaProgressTracker.dto.EyeResultDTO;
import com.example.HyphaemaProgressTracker.enums.EyeSide;

/**
 * Repository interface for managing Patient entities.
 * Extends CrudRepository to provide basic CRUD operations for Patient entities.
 */
public interface PatientRepository extends CrudRepository<Patient, Long> {

    /**
     * Finds a list of Eye entities by patient ID and injury ID.
     * This method joins the Eye, Injury, and Patient entities and retrieves Eyes based on the given patient and injury.
     * 
     * @param patientId the ID of the patient whose eyes are to be retrieved.
     * @param injuryId the ID of the injury associated with the eyes.
     * @return a list of Eye entities associated with the specified patient and injury.
     */
    @Query("SELECT e FROM Eye e JOIN e.injury i JOIN i.patient p WHERE p.id = :patientId AND i.id = :injuryId")
    List<Eye> findEyesByPatientAndInjury(@Param("patientId") Long patientId, @Param("injuryId") Long injuryId);

    /**
     * Finds a list of EyeResultDTO containing date and percentage of eye affected by hyphema for a specific patient, injury, and eye side.
     * This method joins the Eye, Injury, and Patient entities and returns a list of EyeResultDTO with specific details about the eye.
     * 
     * @param patientId the ID of the patient whose eye results are to be retrieved.
     * @param injuryId the ID of the injury associated with the eyes.
     * @param side the side of the eye (e.g., LEFT or RIGHT) to filter results.
     * @return a list of EyeResultDTO objects containing the date and percentage of eye affected by hyphema.
     */
    @Query("SELECT new com.example.HyphaemaProgressTracker.dto.EyeResultDTO(e.date, e.percentageOfEyeAffectedByHyphema) " +
        "FROM Eye e JOIN e.injury i JOIN i.patient p " +
        "WHERE p.id = :patientId AND i.id = :injuryId AND e.side = :side")
    List<EyeResultDTO> findEyesByPatientAndInjuryAndSide(@Param("patientId") Long patientId,
                                                        @Param("injuryId") Long injuryId,
                                                        @Param("side") EyeSide side);

    /**
     * Finds a Patient entity by login name.
     * This method is used for retrieving a patient based on their login credentials.
     * 
     * @param name the login name of the patient.
     * @return an Optional containing the Patient entity if found, or empty if no patient is found with the given login name.
     */
    Optional<Patient> findByLogin(String name);
}

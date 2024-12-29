package com.example.HyphaemaProgressTracker.entities;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing Injury entities.
 * Extends the CrudRepository interface to provide basic CRUD operations.
 */
public interface InjuryRepository extends CrudRepository<Injury, Long> {

    /**
     * Finds a list of injuries by the associated patient ID.
     * 
     * @param patientId the ID of the patient whose injuries are to be retrieved.
     * @return a list of Injury entities associated with the given patient ID.
     */
    List<Injury> findByPatientId(@Param("patientId") Long patientId);
}

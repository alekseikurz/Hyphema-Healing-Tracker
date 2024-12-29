package com.example.HyphaemaProgressTracker.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an injury associated with a patient, including diagnosis information
 * and a list of related eye conditions.
 */
@Entity
@Table(name = "injury") 
public class Injury {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the injury.

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnosis; // Detailed diagnosis description of the injury.

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient; // Reference to the patient who has this injury.

    @OneToMany(mappedBy = "injury", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Eye> eyes = new ArrayList<>(); // List of eye conditions associated with this injury.

    /**
     * Gets the unique identifier of the injury.
     * @return the ID of the injury.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the injury.
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the diagnosis of the injury.
     * @return the diagnosis description.
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Sets the diagnosis of the injury.
     * @param diagnosis the diagnosis to set.
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Gets the patient associated with this injury.
     * @return the patient.
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient associated with this injury.
     * @param patient the patient to associate.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Gets the list of eye conditions associated with this injury.
     * @return the list of related eye conditions.
     */
    public List<Eye> getEyes() {
        return eyes;
    }

    /**
     * Sets the list of eye conditions associated with this injury.
     * @param eyes the list of eye conditions to set.
     */
    public void setEyes(List<Eye> eyes) {
        this.eyes = eyes;
    }
}



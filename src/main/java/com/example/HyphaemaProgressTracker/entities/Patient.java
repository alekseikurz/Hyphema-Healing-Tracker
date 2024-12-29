package com.example.HyphaemaProgressTracker.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a patient entity, including login credentials, roles, 
 * and a list of associated injuries.
 */
@Entity
@Table(name = "patient") 
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the patient.

    @Column(nullable = false)
    private String login; // Login username for the patient.

    @Column(nullable = false)
    private String password; // Hashed password for the patient.

    @Column(nullable = false)
    private boolean enabled = true; // Indicates if the patient account is active.

    @Column
    private String roles = "USER"; // Roles assigned to the patient, default is "USER".

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Injury> injuries = new ArrayList<>(); // List of injuries associated with the patient.

    /**
     * Gets the unique identifier of the patient.
     * @return the ID of the patient.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the patient.
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the login username of the patient.
     * @return the login username.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login username of the patient.
     * @param login the username to set.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets the password of the patient.
     * @return the hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the patient.
     * @param password the hashed password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the enabled status of the patient account.
     * @return true if the account is active, false otherwise.
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the patient account.
     * @param enabled the status to set.
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the roles assigned to the patient.
     * @return the roles as a string.
     */
    public String getRoles() {
        return roles;
    }

    /**
     * Sets the roles assigned to the patient.
     * @param roles the roles to set.
     */
    public void setRoles(String roles) {
        this.roles = roles;
    }

    /**
     * Gets the list of injuries associated with the patient.
     * @return the list of injuries.
     */
    public List<Injury> getInjuries() {
        return injuries;
    }

    /**
     * Sets the list of injuries associated with the patient.
     * @param injuries the list of injuries to set.
     */
    public void setInjuries(List<Injury> injuries) {
        this.injuries = injuries;
    }

    /**
     * Provides a string representation of the patient object.
     * @return a string containing the patient ID and login username.
     */
    public String toString() {
        return "Patient Id: " + this.id + "\n"
                + this.login + "\n";
    }
}

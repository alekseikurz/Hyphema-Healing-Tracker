package com.example.HyphaemaProgressTracker.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.HyphaemaProgressTracker.enums.EyeSide;

/**
 * This class represents the state of an eye with information about the left/right side,
 * the percentage of the eye affected by hyphema, and related metadata.
 */
@Entity
@Table(name = "eye") 
public class Eye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the eye record.

    @Column(nullable = false)
    private int percentageOfEyeAffectedByHyphema; // Percentage of the eye area affected by hyphema.

    @Column(nullable = false)
    private LocalDate date; // Date when the eye condition was recorded.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EyeSide side; // Indicates whether the eye is the left or right side.

    @ManyToOne
    @JoinColumn(name = "injury_id", nullable = false)
    private Injury injury; // Reference to the associated injury.

    /**
     * Gets the unique identifier of the eye record.
     * @return the ID of the eye.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the eye record.
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the percentage of the eye area affected by hyphema.
     * @return the percentage of hyphema-affected area.
     */
    public int getPercentageOfEyeAffectedByHyphema() {
        return percentageOfEyeAffectedByHyphema;
    }

    /**
     * Sets the percentage of the eye area affected by hyphema.
     * @param percentageOfEyeAffectedByHyphema the percentage to set.
     */
    public void setPercentageOfEyeAffectedByHyphema(int percentageOfEyeAffectedByHyphema) {
        this.percentageOfEyeAffectedByHyphema = percentageOfEyeAffectedByHyphema;
    }

    /**
     * Gets the date when the eye condition was recorded.
     * @return the recorded date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date when the eye condition was recorded.
     * @param date the date to set.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the side of the eye (LEFT or RIGHT).
     * @return the side of the eye.
     */
    public EyeSide getSide() {
        return side;
    }

    /**
     * Sets the side of the eye (LEFT or RIGHT).
     * @param side the side to set.
     */
    public void setSide(EyeSide side) {
        this.side = side;
    }

    /**
     * Gets the associated injury of the eye.
     * @return the related injury.
     */
    public Injury getInjury() {
        return injury;
    }

    /**
     * Sets the associated injury of the eye.
     * @param injury the injury to associate.
     */
    public void setInjury(Injury injury) {
        this.injury = injury;
    }
}


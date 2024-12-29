package com.example.HyphaemaProgressTracker.dto;

import java.time.LocalDate;

import com.example.HyphaemaProgressTracker.entities.Eye;

/**
 * Data Transfer Object (DTO) for representing the results of an eye analysis.
 * <p>
 * The {@code EyeResultDTO} is used to encapsulate and transfer data related to an eye analysis,
 * including the date of the analysis and the percentage of the eye affected by hyphema.
 * This DTO serves as a lightweight representation of {@link Eye} for specific use cases, 
 * such as transferring data to the frontend or between application layers.
 * </p>
 */
public class EyeResultDTO {
    private LocalDate date;
    private double percentageOfEyeAffectedByHyphema;

    /**
     * Default constructor for {@code EyeResultDTO}.
     */
    public EyeResultDTO() {}

    /**
     * Parameterized constructor for {@code EyeResultDTO}.
     * 
     * @param date the date of the eye analysis
     * @param percentageOfEyeAffectedByHyphema the percentage of the eye affected by hyphema
     */
    public EyeResultDTO(LocalDate date, double percentageOfEyeAffectedByHyphema) {
        this.date = date;
        this.percentageOfEyeAffectedByHyphema = percentageOfEyeAffectedByHyphema;
    }

    /**
     * Gets the date of the eye analysis.
     * 
     * @return the date of the analysis
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the eye analysis.
     * 
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Sets the percentage of the eye affected by hyphema.
     * 
     * @param percentageOfEyeAffectedByHyphema the percentage to set
     */
    public double getPercentageOfEyeAffectedByHyphema() {
        return percentageOfEyeAffectedByHyphema;
    }

    public void setPercentageOfEyeAffectedByHyphema(double percentageOfEyeAffectedByHyphema) {
        this.percentageOfEyeAffectedByHyphema = percentageOfEyeAffectedByHyphema;
    }

    /**
     * Returns a string representation of the {@code EyeResultDTO}.
     */
    @Override
    public String toString() {
        return "EyeResultDTO{" +
                "date=" + date +
                ", percentageOfEyeAffectedByHyphema=" + percentageOfEyeAffectedByHyphema +
                '}';
    }
}

package com.example.HyphaemaProgressTracker.dto;


/**
 * A data transfer object (DTO) for saving the results of an eye injury analysis.
 * <p>
 * This class is used to encapsulate the data required to save the analysis result,
 * including details about the injury, the side of the eye, the date of the analysis,
 * and the percentage of the eye affected by hyphema.
 * </p>
 */
public class SaveResultRequest {
    private Long injuryId; // ID of the associated injury
    private String eye; // Side of the eye (e.g., "LEFT", "RIGHT")
    private String date; // Analysis date in string format
    private int percentageOfEyeAffectedByHyphema; // Percentage of the eye affected by hyphema

    /**
     * Gets the ID of the associated injury.
     * 
     * @return the ID of the injury
     */
    public Long getInjuryId() {
        return injuryId;
    }

    /**
     * Sets the ID of the associated injury.
     * 
     * @param injuryId the ID of the injury to set
     */
    public void setInjuryId(Long injuryId) {
        this.injuryId = injuryId;
    }

    /**
     * Gets the side of the eye being analyzed.
     * 
     * @return the side of the eye
     */
    public String getEye() {
        return eye;
    }

    /**
     * Sets the side of the eye being analyzed.
     * <p>
     * Expected values are strings such as "LEFT" or "RIGHT".
     * </p>
     * 
     * @param eye the side of the eye to set
     */
    public void setEye(String eye) {
        this.eye = eye;
    }

    /**
     * Gets the date of the analysis.
     * 
     * @return the date of the analysis
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the analysis.
     * 
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the percentage of the eye affected by hyphema.
     * 
     * @return the percentage of the eye affected by hyphema
     */
    public int getPercentageOfEyeAffectedByHyphema() {
        return percentageOfEyeAffectedByHyphema;
    }

    /**
     * Sets the percentage of the eye affected by hyphema.
     * 
     * @param percentageOfEyeAffectedByHyphema the percentage to set
     */
    public void setPercentageOfEyeAffectedByHyphema(int percentageOfEyeAffectedByHyphema) {
        this.percentageOfEyeAffectedByHyphema = percentageOfEyeAffectedByHyphema;
    }

}

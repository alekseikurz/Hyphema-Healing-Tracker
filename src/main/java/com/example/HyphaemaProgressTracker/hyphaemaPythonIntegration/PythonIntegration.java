package com.example.HyphaemaProgressTracker.hyphaemaPythonIntegration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * Represents a class for integrating Python code for hyphema analysis.
 * This class is responsible for running a Python script that calculates the percentage
 * of hyphema (blood in the eye) relative to the iris and pupil area from a photo.
 * The Python script uses OpenCV for image processing and analysis.
 */
public class PythonIntegration {

    /**
     * Executes the Python script for hyphema analysis.
     * This method runs the Python script that performs the analysis on a given photo
     * and returns the result in JSON format.
     * 
     * @param photoPath the path of the photo to be analyzed by the Python script.
     * @return the output of the Python script, which is expected to be in JSON format,
     *         or an error message if the script execution fails.
     */
    public static String runHyphaemaApp(String photoPath) {
        try {
            // Define the absolute path to the Python script
            String scriptPath = Paths.get("src/main/resources/analyze_hyphema.py").toAbsolutePath().toString();
            
            // Create a ProcessBuilder to execute the Python script with the photo path as an argument
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", // Command to run Python
                scriptPath, // Path to the Python script
                photoPath // Path of the photo to be analyzed
            );
            
            // Start the process to execute the script
            Process process = processBuilder.start();
    
            // Create a BufferedReader to read the standard output of the Python process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
    
            // Read the output of the Python script line by line
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString(); 
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line);
                }
                return "Fehler: " + errorOutput.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler bei der Analyse";
        }
    }
}




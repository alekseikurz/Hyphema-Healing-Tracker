package com.example.HyphaemaProgressTracker.services;

import org.springframework.stereotype.Service;

import com.example.HyphaemaProgressTracker.entities.Injury;

/**
 * This service provides methods for creating and managing Injury objects.
 */
@Service
public class InjuryService {
    public Injury createNewInjury() {
        Injury injury= new Injury();
        return injury;
    }
}

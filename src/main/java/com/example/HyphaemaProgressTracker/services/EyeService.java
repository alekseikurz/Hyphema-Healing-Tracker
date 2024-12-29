package com.example.HyphaemaProgressTracker.services;

import org.springframework.stereotype.Service;

import com.example.HyphaemaProgressTracker.entities.Eye;

/**
 * This service provides methods for creating and managing Eye objects.
 */
@Service
public class EyeService {
    public Eye createNewEye() {
        Eye eye = new Eye();
        return eye;
    }
}

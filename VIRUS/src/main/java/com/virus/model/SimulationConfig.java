package com.virus.model;

public class SimulationConfig {
    private final int healthyAgents;
    private final int infectedAgents;
    private final int hospitals;
    private final int immunityDuration;
    private final int movementDelay;
    private final int lifeDuration;
    private final int healingDuration;
    
    public SimulationConfig(
        int healthyAgents,
        int infectedAgents,
        int hospitals,
        int immunityDuration,
        int movementDelay,
        int lifeDuration,
        int healingDuration
    ) {
        this.healthyAgents = healthyAgents;
        this.infectedAgents = infectedAgents;
        this.hospitals = hospitals;
        this.immunityDuration = immunityDuration;
        this.movementDelay = movementDelay;
        this.lifeDuration = lifeDuration;
        this.healingDuration = healingDuration;
    }
    
    // Getters
    public int getHealthyAgents() { return healthyAgents; }
    public int getInfectedAgents() { return infectedAgents; }
    public int getHospitals() { return hospitals; }
    public int getImmunityDuration() { return immunityDuration; }
    public int getMovementDelay() { return movementDelay; }
    public int getLifeDuration() { return lifeDuration; }
    public int getHealingDuration() { return healingDuration; }
}
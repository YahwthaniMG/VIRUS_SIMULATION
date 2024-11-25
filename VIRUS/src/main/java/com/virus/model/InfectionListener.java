package com.virus.model;

public interface InfectionListener {
    void onInfection(Agent infector, Agent infected);
}

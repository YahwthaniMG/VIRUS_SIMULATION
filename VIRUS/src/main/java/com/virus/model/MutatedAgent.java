package com.virus.model;

import com.virus.model.enums.*;

public class MutatedAgent extends Agent {
    private static final int MUTATION_RADIUS = 40;
    
    
    public MutatedAgent(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight, "src/main/resources/images/mutated.png", AgentState.MUTATED);
    }
    
    @Override
    protected void handleCollision(Agent other) {
        // Mutar agentes sanos
        if (other.getState() == AgentState.HEALTHY && !other.isImmune()) {
            synchronized(other) {
                if (other.getState() == AgentState.HEALTHY && !other.isImmune()) {
                    other.setState(AgentState.MUTATED);
                    AgentManager.getInstance().notifyInfection(this, other);
                }
            }
        }
        // Mutar agentes infectados
        else if (other.getState() == AgentState.INFECTED) {
            synchronized(other) {
                if (other.getState() == AgentState.INFECTED) {
                    other.setState(AgentState.MUTATED);
                    AgentManager.getInstance().notifyInfection(this, other);
                }
            }
        }
    }
}
package com.virus.model;
import com.virus.model.enums.*;
import java.util.List;

public class InfectedAgent extends Agent {
    private static final int INFECTION_RADIUS = 20;
    
    public InfectedAgent(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight, "src/main/resources/images/infected.png", AgentState.INFECTED);
    }
    
    @Override
    public void run() {
        while (isRunning) {
            if (getState().canMove()) {
                move();
                handleInteractions();
            }
            
            try {
                Thread.sleep(MOVEMENT_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleInteractions() {
        int centerX = position.x + 15;
        int centerY = position.y + 15;
        
        List<Agent> nearbyAgents = AgentManager.getInstance().getNearbyAgentsFromCenter(
            centerX, centerY, INFECTION_RADIUS);
        
        for (Agent other : nearbyAgents) {
            if (other != this) {
                // Infectar agentes sanos
                if (other.getState() == AgentState.HEALTHY && !other.isImmune()) {
                    synchronized(other) {
                        if (other.getState() == AgentState.HEALTHY && !other.isImmune()) {
                            other.setState(AgentState.INFECTED);
                            AgentManager.getInstance().notifyInfection(this, other);
                        }
                    }
                }
                // Mutar al encontrarse con otro infectado
                else if (other.getState() == AgentState.INFECTED) {
                    synchronized(other) {
                        if (other.getState() == AgentState.INFECTED && 
                            this.getState() == AgentState.INFECTED) {
                            other.setState(AgentState.MUTATED);
                            this.setState(AgentState.MUTATED);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void handleCollision(Agent other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
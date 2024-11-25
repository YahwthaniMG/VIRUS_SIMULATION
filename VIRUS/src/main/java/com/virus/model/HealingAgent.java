package com.virus.model;
import com.virus.model.enums.*;

public class HealingAgent extends Agent {
    private final long healingStartTime; 
    
    public HealingAgent(int x, int y) {
        super(x, y, "src/main/resources/images/healing.png", AgentState.HEALING, 1);
        this.healingStartTime = System.currentTimeMillis();
    }
    
    @Override
    protected void move() {
        // No se mueve mientras está curándose
    }
    
    @Override
    public boolean isHealing() {
        return true;
    }

    public boolean isHealingComplete() {
        return System.currentTimeMillis() - healingStartTime >= HEALING_DURATION;
    }

    @Override
    protected void handleCollision(Agent other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
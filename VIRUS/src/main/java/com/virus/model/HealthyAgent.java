package com.virus.model;
import com.virus.model.enums.*;

public class HealthyAgent extends Agent {
    public HealthyAgent(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight, "src/main/resources/images/healthy.png", AgentState.HEALTHY);
    }

    public HealthyAgent(int mapWidth, int mapHeight, int op) {
        super(mapWidth, mapHeight, "src/main/resources/images/healthy.png", AgentState.HEALTHY, 1);
    }

    @Override
    protected void handleCollision(Agent other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

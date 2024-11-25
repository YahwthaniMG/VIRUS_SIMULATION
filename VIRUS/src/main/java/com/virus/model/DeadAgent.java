package com.virus.model;

import com.virus.model.enums.*;
import com.virus.network.TCPClient;
import java.util.concurrent.TimeUnit;

public class DeadAgent extends Agent {
    private final long totalLifeTime;
    public DeadAgent(int x, int y, long lifeTime) {
        super(x, y, "src/main/resources/images/dead.png", AgentState.DEAD, 1);
        this.totalLifeTime = lifeTime;
        sendDeathReport();
    }
    
    private void sendDeathReport() {
        // Convertir milisegundos a un formato más legible
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalLifeTime) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalLifeTime);

        String report = String.format(
            "DEATH_REPORT|Agent:%s|Position:(%d,%d)|LifeTime:%02dm:%02ds",
            Thread.currentThread().getName(),
            position.x, 
            position.y,
            minutes, 
            seconds
        );

        new Thread(() -> {
            try {
                TCPClient client = new TCPClient();
                client.sendMessage(report);
            } catch (Exception e) {
                System.err.println("DeadAgent: Error enviando reporte - " + e.getMessage());
            }
        }).start();
    }

    @Override
    protected void move() {}
    
    @Override
    public boolean isDead() {
        return true;
    }
    
    @Override
    protected void handleCollision(Agent other) {}
}
package com.virus.model;
import com.virus.model.enums.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Hospital extends Agent {
    private static final int HEALING_RADIUS = 30;
    private static int MAX_CAPACITY = 1;
    private final AtomicInteger currentPatients = new AtomicInteger(0);
    private final ConcurrentHashMap<HealingAgent, Long> healingAgents = new ConcurrentHashMap<>();
    
    public Hospital(int mapWidth, int mapHeight, int numCapacity) {
        super(mapWidth, mapHeight, "src/main/resources/images/hospital.png", AgentState.HOSPITAL);
        this.MAX_CAPACITY=numCapacity;
    }
    public Hospital(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight, "src/main/resources/images/hospital.png", AgentState.HOSPITAL);
    }

    @Override
    public void run() {
        while (isRunning) {
            checkForPatients();
            manageHealingProcess();
            
            try {
                Thread.sleep(MOVEMENT_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkForPatients() {
        if (currentPatients.get() >= MAX_CAPACITY) {
            return;
        }

        int centerX = position.x + 15;
        int centerY = position.y + 15;
        
        List<Agent> nearbyAgents = AgentManager.getInstance().getNearbyAgentsFromCenter(
            centerX, centerY, HEALING_RADIUS);
        
        for (Agent agent : nearbyAgents) {
            if (agent.getState() == AgentState.INFECTED) {
                tryToHealAgent(agent);
            }
        }
    }

    private void tryToHealAgent(Agent agent) {
        if (currentPatients.get() < MAX_CAPACITY) {
            synchronized(agent) {
                if (agent.getState() == AgentState.INFECTED && currentPatients.get() < MAX_CAPACITY) {
                    startHealingProcess(agent);
                }
            }
        }
    }

    private void startHealingProcess(Agent infectedAgent) {
        // Obtener la posición actual del agente infectado
        Point infectedPos = infectedAgent.getPosition();
        // Crear nuevo HealingAgent en la misma posición exacta
        HealingAgent healingAgent = new HealingAgent(
            infectedPos.x,  // Usar la posición exacta del infectado
            infectedPos.y
        );
        // Detener el agente infectado
        infectedAgent.stop();
        // Registrar el agente en curación
        currentPatients.incrementAndGet();
        healingAgents.put(healingAgent, System.currentTimeMillis());
        // Reemplazar el agente en el AgentManager
        AgentManager.getInstance().replaceAgent(infectedAgent, healingAgent);
        // Iniciar el proceso de curación
        AgentManager.getInstance().startAgent(healingAgent);
    }

    private void manageHealingProcess() {
        healingAgents.forEach((healingAgent, startTime) -> {
            if (healingAgent.isHealingComplete()) {
                completeHealing(healingAgent);
            }
        });
    }

    private void completeHealing(HealingAgent healingAgent) {
        // Crear nuevo agente sano en la misma posición
        HealthyAgent healthyAgent = new HealthyAgent(
            healingAgent.getPosition().x,
            healingAgent.getPosition().y,
            1
        );
        // Establecer inmunidad
        healthyAgent.setImmunityTime(System.currentTimeMillis());
        // Detener el agente en curación
        healingAgent.stop();
        // Reemplazar el agente en el AgentManager
        AgentManager.getInstance().replaceAgent(healingAgent, healthyAgent);
        // Iniciar el nuevo agente sano
        AgentManager.getInstance().startAgent(healthyAgent);
        // Limpiar registros
        healingAgents.remove(healingAgent);
        currentPatients.decrementAndGet();
    }

    void drawHealingRadius(Graphics2D g2d) {
        int centerX = position.x + 15;
        int centerY = position.y + 15;
        
        // Dibujar área de curación
        g2d.setColor(new Color(0, 191, 255, 50));
        g2d.fillOval(
            centerX - HEALING_RADIUS,
            centerY - HEALING_RADIUS,
            HEALING_RADIUS * 2,
            HEALING_RADIUS * 2
        );
        
        // Borde del área de curación
        g2d.setColor(new Color(0, 191, 255, 100));
        g2d.drawOval(
            centerX - HEALING_RADIUS,
            centerY - HEALING_RADIUS,
            HEALING_RADIUS * 2,
            HEALING_RADIUS * 2
        );
    }

    @Override
    protected void handleCollision(Agent other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
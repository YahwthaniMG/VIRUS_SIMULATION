package com.virus.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentManager {
    private List<Agent> agents;
    private ExecutorService executorService;
    private final int mapWidth;
    private final int mapHeight;
    private static AgentManager instance;
    private final List<InfectionListener> infectionListeners;
    private final Object agentsLock = new Object();
    
    public AgentManager(int mapWidth, int mapHeight) {
        this.agents = new ArrayList<>();
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.executorService = Executors.newCachedThreadPool();
        this.infectionListeners = new ArrayList<>();
        instance = this;
    }

    private AgentManager() {
        this.agents = new ArrayList<>();
        this.infectionListeners = new ArrayList<>();
        this.mapWidth = 40;
        this.mapHeight = 40;
    }

    public static AgentManager getInstance() {
        if (instance == null) {
            instance = new AgentManager();
        }
        return instance;
    }

    public List<Agent> getNearbyAgents(Agent source, int radius) {
        List<Agent> nearby = new ArrayList<>();
        synchronized (agentsLock) {
            for (Agent agent : agents) {
                if (agent != source && source.getDistanceTo(agent) <= radius) {
                    nearby.add(agent);
                }
            }
        }
        return nearby;
    }
    
    public void addInfectionListener(InfectionListener listener) {
        infectionListeners.add(listener);
    }

    public void removeInfectionListener(InfectionListener listener) {
        infectionListeners.remove(listener);
    }
    
    public void notifyInfection(Agent infector, Agent infected) {
        for (InfectionListener listener : infectionListeners) {
            listener.onInfection(infector, infected);
        }
    }

    public List<Agent> getNearbyAgentsFromCenter(int centerX, int centerY, int radius) {
        List<Agent> nearby = new ArrayList<>();
        synchronized (agentsLock) {
            for (Agent agent : agents) {
                int otherCenterX = agent.getPosition().x + 15;
                int otherCenterY = agent.getPosition().y + 15;
                
                double distance = Math.sqrt(
                    Math.pow(centerX - otherCenterX, 2) + 
                    Math.pow(centerY - otherCenterY, 2)
                );
                
                if (distance <= radius) {
                    nearby.add(agent);
                }
            }
        }
        return nearby;
    }
    
    public void drawInfectionRadius(Graphics2D g2d) {
        synchronized (agentsLock) {
            for (Agent agent : agents) {
                if (agent instanceof Hospital) {
                    ((Hospital) agent).drawHealingRadius(g2d);
                } else if (agent.getState().canInfect()) {
                    // Dibujar radio de infección para agentes infectados/mutados
                    int centerX = agent.getPosition().x + 15;
                    int centerY = agent.getPosition().y + 15;
                    int radius = agent.getState().getInteractionRange();
                    
                    g2d.setColor(new Color(255, 0, 0, 50));
                    g2d.fillOval(
                        centerX - radius,
                        centerY - radius,
                        radius * 2,
                        radius * 2
                    );
                    
                    g2d.setColor(new Color(255, 0, 0, 100));
                    g2d.drawOval(
                        centerX - radius,
                        centerY - radius,
                        radius * 2,
                        radius * 2
                    );
                }
            }
        }
    }

    public void initializeAgents(int totalAgents) {
        // Calcular números de cada tipo (mínimo 1 para hospitales y enfermos)
        int numHospitals = Math.max(1, (int)(totalAgents * 0.1));
        int numInfected = Math.max(1, (int)(totalAgents * 0.1));
        int numHealthy = totalAgents - numHospitals - numInfected;
        int numCapacity = Math.max(1,(int)(totalAgents*.2));
        
        // Crear agentes
        for (int i = 0; i < numHospitals; i++) {
            agents.add(new Hospital(mapWidth, mapHeight, numCapacity));
        }
        for (int i = 0; i < numInfected; i++) {
            agents.add(new InfectedAgent(mapWidth, mapHeight));
        }
        for (int i = 0; i < numHealthy; i++) {
            agents.add(new HealthyAgent(mapWidth, mapHeight));
        }

        // Iniciar todos los hilos
        for (Agent agent : agents) {
            executorService.submit(agent);
        }
    }

    public void initializeAgentsWithConfig(SimulationConfig config) {
        synchronized(agentsLock) {
            // Primero crear hospitales
            for (int i = 0; i < config.getHospitals(); i++) {
                agents.add(new Hospital(mapWidth, mapHeight));
            }
            // Luego crear agentes infectados
            for (int i = 0; i < config.getInfectedAgents(); i++) {
                InfectedAgent infectedAgent = new InfectedAgent(mapWidth, mapHeight);
                agents.add(infectedAgent);
            }
            // Finalmente crear agentes sanos
            for (int i = 0; i < config.getHealthyAgents(); i++) {
                agents.add(new HealthyAgent(mapWidth, mapHeight));
            }
            // Iniciar todos los hilos
            for (Agent agent : agents) {
                executorService.submit(agent);
            }
        }
    }

    public List<Agent> getAgents() {
        return new ArrayList<>(agents);
    }

    public void stopAllAgents() {
        agents.forEach(Agent::stop);
        executorService.shutdown();
    }

    public void replaceAgent(Agent oldAgent, Agent newAgent) {
        synchronized (agentsLock) {
            int index = agents.indexOf(oldAgent);
            if (index != -1) {
                // Detener el agente anterior
                oldAgent.stop();
                // Reemplazar en la lista
                agents.set(index, newAgent);
                // Los agentes muertos no necesitan ser ejecutados
                if (!(newAgent instanceof DeadAgent)) {
                    executorService.submit(newAgent);
                }
            }
        }
    }
    
    public void startAgent(Agent agent) {
        executorService.submit(agent);
    }

    
}
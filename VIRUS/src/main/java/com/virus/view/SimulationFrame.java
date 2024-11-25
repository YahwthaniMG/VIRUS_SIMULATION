package com.virus.view;

import com.virus.model.*;
import com.virus.model.enums.AgentState;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SimulationFrame extends JFrame implements InfectionListener {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private BufferedImage worldMap;
    private AgentManager agentManager;
    private Timer updateTimer;
    private final SimulationConfig config;
    private final int numberOfAgents;
    
    public SimulationFrame(int totalAgents) {
        this(new SimulationConfig(
            totalAgents - 2, // healthy
            1,              // infected
            1,              // hospitals
            3000,           // immunity duration
            50,             // movement delay
            10000,          // life duration
            5000            // healing duration
        ));
    }

    public SimulationFrame(SimulationConfig config) {
        this.config = config;
        this.numberOfAgents = config.getHealthyAgents() + 
                            config.getInfectedAgents() + 
                            config.getHospitals();
        setupWindow();
        loadWorldMap();
        initializeAgents();
        initializeComponents();
        startSimulation();
        this.setVisible(true);
    }
    
    private void initializeAgents() {
        agentManager = new AgentManager(WINDOW_WIDTH, WINDOW_HEIGHT);
        agentManager.addInfectionListener(this);
        agentManager.initializeAgentsWithConfig(config);
    }

    @Override
    public void onInfection(Agent infector, Agent infected) {
        SwingUtilities.invokeLater(this::repaint);
    }

    private void startSimulation() {
        updateTimer = new Timer(33, e -> repaint());
        updateTimer.start();
    }

    private void setupWindow() {
        this.setTitle("VIRUS Simulation");
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
    }
    
    private void loadWorldMap() {
        try {
            URL resourceUrl = getClass().getResource("/images/worldmap.png");
            if (resourceUrl != null) {
                worldMap = ImageIO.read(resourceUrl);
                return;
            }

            String[] possiblePaths = {
                "src/main/resources/images/worldmap.png",
                "resources/images/worldmap.png",
                "images/worldmap.png"
            };

            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    worldMap = ImageIO.read(file);
                    return;
                }
            }

            createDefaultMap();

        } catch (IOException e) {
            e.printStackTrace();
            createDefaultMap();
            JOptionPane.showMessageDialog(this,
                "Error loading world map image: " + e.getMessage(),
                "Error",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createDefaultMap() {
        worldMap = new BufferedImage(800, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = worldMap.createGraphics();
        
        g2d.setColor(new Color(135, 206, 235));
        g2d.fillRect(0, 0, 800, 400);
        
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(150, 50, 100, 300);
        g2d.fillRect(350, 50, 100, 300);
        g2d.fillRect(550, 50, 150, 250);
        
        g2d.dispose();
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new MapPanel();
        this.add(mainPanel);
        
        // Panel de control con botón de estado
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        
        JButton threadStatusButton = new JButton("Thread Status");
        threadStatusButton.addActionListener(e -> new ThreadStatusFrame(agentManager));
        controlPanel.add(threadStatusButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private class MapPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (worldMap != null) {
                g2d.drawImage(worldMap, 0, 0, getWidth(), getHeight(), null);
            }
            
            List<Agent> currentAgents = agentManager.getAgents();
            
            AgentManager.getInstance().drawInfectionRadius(g2d);
            
            for (Agent agent : currentAgents) {
                Point pos = agent.getPosition();
                Image img = agent.getImage();
                if (img != null) {
                    g2d.drawImage(img, pos.x, pos.y, null);
                }
            }
            
            drawSimulationInfo(g2d);
        }
    }

    private void drawSimulationInfo(Graphics2D g2d) {
        int panelWidth = 200;
        int panelHeight = 200;
        int marginX = 10;
        int startY = 30;
        int lineHeight = 20;
        
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(marginX, marginX, panelWidth, panelHeight);
        
        g2d.setColor(new Color(100, 100, 100, 180));
        g2d.drawRect(marginX, marginX, panelWidth, panelHeight);
        
        g2d.setColor(new Color(0, 255, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Simulation Status", marginX + 10, startY);
        
        g2d.drawLine(marginX + 10, startY + 5, marginX + panelWidth - 10, startY + 5);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        startY += 25;
        
        Color healthyColor = new Color(0, 255, 0);
        Color infectedColor = new Color(255, 0, 255);
        Color mutatedColor = new Color(255, 0, 0);
        Color hospitalColor = new Color(0, 191, 255);
        Color deadColor = new Color(128, 128, 128);
        Color healingColor = new Color(255, 255, 0);
        
        class StatLine {
            void draw(String label, int count, Color color, int y) {
                g2d.setColor(color);
                g2d.fillOval(marginX + 15, y - 10, 10, 10);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(label + ": " + count, marginX + 35, y);
            }
        }
        
        StatLine statLine = new StatLine();
        
        int totalAgents = getTotalAgents();
        int healthyCount = getHealthyCount();
        int infectedCount = getInfectedCount();
        int mutatedCount = getMutatedCount();
        int hospitalCount = getHospitalCount();
        int healingCount = getHealingCount();
        int deadCount = getDeadCount();
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("Total Agents: " + totalAgents, marginX + 15, startY);
        startY += lineHeight;
        
        statLine.draw("Healthy", healthyCount, healthyColor, startY += lineHeight);
        statLine.draw("Infected", infectedCount, infectedColor, startY += lineHeight);
        statLine.draw("Mutated", mutatedCount, mutatedColor, startY += lineHeight);
        statLine.draw("Hospitals", hospitalCount, hospitalColor, startY += lineHeight);
        statLine.draw("Healing", healingCount, healingColor, startY += lineHeight);
        statLine.draw("Dead", deadCount, deadColor, startY += lineHeight);
    }

    private int getTotalAgents() {
        return agentManager.getAgents().size();
    }


    private int getHospitalCount() {
        synchronized(agentManager.getAgents()) {
            return (int) agentManager.getAgents().stream()
                .filter(agent -> agent instanceof Hospital)
                .count();
        }
    }

    private int getHealthyCount() {
    return (int) agentManager.getAgents().stream()
        .filter(agent -> agent.getState() == AgentState.HEALTHY)
        .count();
    }

    private int getInfectedCount() {
        return (int) agentManager.getAgents().stream()
            .filter(agent -> agent.getState() == AgentState.INFECTED)
            .count();
    }

    private int getMutatedCount() {
        return (int) agentManager.getAgents().stream()
            .filter(agent -> agent.getState() == AgentState.MUTATED)
            .count();
    }

    private int getHealingCount() {
        return (int) agentManager.getAgents().stream()
            .filter(agent -> agent.getState() == AgentState.HEALING)
            .count();
    }

    private int getDeadCount() {
        return (int) agentManager.getAgents().stream()
            .filter(agent -> agent.getState() == AgentState.DEAD)
            .count();
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        if (agentManager != null) {
            agentManager.stopAllAgents();
        }
        super.dispose();
    }
}
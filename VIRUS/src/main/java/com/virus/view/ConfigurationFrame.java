package com.virus.view;

import com.virus.model.Agent;
import com.virus.model.SimulationConfig;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ConfigurationFrame extends JFrame {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 600;
    private final int totalAgents;
    private boolean configurationSaved = false;
    
    // Campos de configuración
    private JSpinner healthySpinner;
    private JSpinner infectedSpinner;
    private JSpinner hospitalSpinner;
    private JSpinner immunityDurationSpinner;
    private JSpinner movementDelaySpinner;
    private JSpinner lifeDurationSpinner;
    private JSpinner healingDurationSpinner;
    
    public ConfigurationFrame(int totalAgents) {
        this.totalAgents = totalAgents;
        setupWindow();
        initializeComponents();
        this.setVisible(true);
    }
    
    private void setupWindow() {
        setTitle("VIRUS Configuration");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30));
        
        // Manejar cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!configurationSaved) {
                    startSimulationWithDefaults();
                }
            }
        });
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Configuration");
        titleLabel.setForeground(new Color(0, 255, 0));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Panel de distribución de agentes
        JPanel agentsPanel = createAgentsPanel();
        mainPanel.add(agentsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Panel de timers
        JPanel timersPanel = createTimersPanel();
        mainPanel.add(timersPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Botón Save and Start
        JButton saveButton = new JButton("Save and Start");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(saveButton);
        saveButton.addActionListener(e -> validateAndStart());
        mainPanel.add(saveButton);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createAgentsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0)),
            "Agents Distribution",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            new Color(0, 255, 0)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Calcular valores por defecto
        int defaultHospitals = Math.max(1, (int)(totalAgents * 0.1));
        int defaultInfected = Math.max(1, (int)(totalAgents * 0.1));
        int defaultHealthy = totalAgents - defaultHospitals - defaultInfected;
        
        // Healthy Agents
        addLabelAndSpinner(panel, "Healthy Agents:", defaultHealthy, 0, gbc);
        healthySpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Infected Agents
        addLabelAndSpinner(panel, "Infected Agents:", defaultInfected, 1, gbc);
        infectedSpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Hospitals
        addLabelAndSpinner(panel, "Hospitals:", defaultHospitals, 2, gbc);
        hospitalSpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Total label
        JLabel totalLabel = new JLabel("Total Agents: " + totalAgents);
        totalLabel.setForeground(new Color(0, 255, 0));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(totalLabel, gbc);
        
        return panel;
    }
    
    private JPanel createTimersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0)),
            "Timers Configuration",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            new Color(0, 255, 0)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Immunity Duration
        addLabelAndSpinner(panel, "Immunity Duration (ms):", 3000, 0, gbc);
        immunityDurationSpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Movement Delay
        addLabelAndSpinner(panel, "Movement Delay (ms):", 50, 1, gbc);
        movementDelaySpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Life Duration
        addLabelAndSpinner(panel, "Life Duration (ms):", 10000, 2, gbc);
        lifeDurationSpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        // Healing Duration
        addLabelAndSpinner(panel, "Healing Duration (ms):", 5000, 3, gbc);
        healingDurationSpinner = (JSpinner)panel.getComponent(panel.getComponentCount() - 1);
        
        return panel;
    }
    
    private void addLabelAndSpinner(JPanel panel, String labelText, int defaultValue, int row, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(0, 255, 0));
        
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue, 0, Integer.MAX_VALUE, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(100, 25));
        styleSpinner(spinner);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        panel.add(spinner, gbc);
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setBackground(new Color(45, 45, 45));
        spinner.getEditor().getComponent(0).setForeground(Color.WHITE);
        ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setBackground(new Color(45, 45, 45));
        spinner.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0)));
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(0, 255, 0));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }
    
    private void validateAndStart() {
        int healthy = (int)healthySpinner.getValue();
        int infected = (int)infectedSpinner.getValue();
        int hospitals = (int)hospitalSpinner.getValue();
        
        if (healthy + infected + hospitals != totalAgents) {
            JOptionPane.showMessageDialog(this,
                "Total number of agents must equal " + totalAgents,
                "Invalid Configuration",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Guardar configuración y comenzar simulación
        SimulationConfig config = new SimulationConfig(
            healthy,
            infected,
            hospitals,
            (int)immunityDurationSpinner.getValue(),
            (int)movementDelaySpinner.getValue(),
            (int)lifeDurationSpinner.getValue(),
            (int)healingDurationSpinner.getValue()
        );
        
        configurationSaved = true;
        dispose();
        startSimulationWithConfig(config);
    }
    
    private void startSimulationWithDefaults() {
        // Comenzar simulación con valores por defecto
        SwingUtilities.invokeLater(() -> new SimulationFrame(totalAgents));
    }
    
    private void startSimulationWithConfig(SimulationConfig config) {
        // Actualizar configuraciones globales
        Agent.updateConfigurations(config);
        // Iniciar simulación
        SwingUtilities.invokeLater(() -> new SimulationFrame(config));
    }
}
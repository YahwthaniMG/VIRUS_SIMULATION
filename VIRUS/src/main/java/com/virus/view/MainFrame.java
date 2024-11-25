package com.virus.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private JTextField agentsField;
    private JButton startButton;
    
    public MainFrame() {
        setupWindow();
        initializeComponents();
        this.setVisible(true);
    }
    
    private void setupWindow() {
        this.setTitle("VIRUS Simulation");
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(30, 30, 30));
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior para logo y desarrolladores
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.setBorder(new EmptyBorder(20, 20, 0, 20));
        
        // Logo
        ImageIcon logoIcon = new ImageIcon("src/main/resources/images/logo.jpg");
        Image scaledImage = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        
        // Panel de desarrolladores
        JPanel devPanel = new JPanel();
        devPanel.setLayout(new BoxLayout(devPanel, BoxLayout.Y_AXIS));
        devPanel.setBackground(new Color(30, 30, 30));
        
        JLabel devTitle = new JLabel("Desarrolladores");
        devTitle.setForeground(new Color(0, 255, 0));
        devTitle.setFont(new Font("Arial", Font.BOLD, 16));
        devTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] developers = {
            "Brandon Mitzrael Magaña Avalos",
            "Eduardo Ulises Martínez Vaca",
            "Yahwthani Morales Gómez"
        };
        
        // Agregar componentes al panel de desarrolladores
        devPanel.add(devTitle);
        devPanel.add(Box.createVerticalStrut(10));
        for (String dev : developers) {
            JLabel devLabel = new JLabel(dev);
            devLabel.setForeground(new Color(0, 255, 0));
            devLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            devLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            devPanel.add(devLabel);
            devPanel.add(Box.createVerticalStrut(5));
        }
        
        // Agregar logo y desarrolladores al panel superior
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 400);
        topPanel.add(logoLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        topPanel.add(devPanel, gbc);
        
        // Panel central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Título VIRUS
        JLabel titleLabel = new JLabel("VIRUS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 80));
        titleLabel.setForeground(new Color(0, 255, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // Panel de agentes
        JPanel agentsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        agentsPanel.setBackground(new Color(30, 30, 30));
        
        JLabel agentsLabel = new JLabel("Agents:");
        agentsLabel.setForeground(Color.WHITE);
        agentsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        agentsField = new JTextField(10);
        agentsField.setFont(new Font("Arial", Font.PLAIN, 18));
        agentsField.setHorizontalAlignment(JTextField.CENTER);
        agentsField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        agentsPanel.add(agentsLabel);
        agentsPanel.add(agentsField);
        
        // Botón Start
        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setForeground(Color.BLACK);
        startButton.setBackground(new Color(0, 255, 0));
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0), 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Agregar componentes al panel central
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(50));
        centerPanel.add(agentsPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalGlue());
        
        // Panel inferior para información del curso
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(30, 30, 30));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel courseLabel = new JLabel("Fundamentos de Programación en Paralelo");
        courseLabel.setForeground(new Color(0, 255, 0));
        courseLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        courseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel professorLabel = new JLabel("Dr. Juan Carlos López Pimentel");
        professorLabel.setForeground(new Color(0, 255, 0));
        professorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        professorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bottomPanel.add(courseLabel);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(professorLabel);
        
        // Agregar todos los paneles al frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setupButtonActions();
    }
    
    private void setupButtonActions() {
        startButton.addActionListener(e -> {
            String agentsText = agentsField.getText();
            try {
                int numberOfAgents = Integer.parseInt(agentsText);
                if (numberOfAgents > 0 ) {
                    if(numberOfAgents >100){
                        showError("Please enter a number of agents lower than 100.");
                    }else{
                        if(numberOfAgents<3){
                            showError("Please enter a number of agents greater than 3.");
                        }else{
                            startSimulation(numberOfAgents);
                        }
                    }
                } else {
                    showError("Please enter a positive number of agents.");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });
    }
    
    private void startSimulation(int numberOfAgents) {
        System.out.println("Starting simulation with " + numberOfAgents + " agents");
        this.dispose();
        SwingUtilities.invokeLater(() -> new ConfigurationFrame(numberOfAgents));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
package com.virus.view;

import com.virus.model.*;
import com.virus.model.enums.AgentState;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ThreadStatusFrame extends JFrame {
    private JTable threadTable;
    private DefaultTableModel tableModel;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private Timer updateTimer;
    private final AgentManager agentManager;
    private String state;

    public ThreadStatusFrame(AgentManager agentManager) {
        this.agentManager = agentManager;
        setupWindow();
        initializeComponents();
        startUpdates();
        this.setVisible(true);
    }

    private void setupWindow() {
        setTitle("Thread Status");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));
    }

    private void initializeComponents() {
        String[] columnNames = {"Thread Name", "Agent", "State", "Position"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        threadTable = new JTable(tableModel);
        setupTableAppearance();
        
        JScrollPane scrollPane = new JScrollPane(threadTable);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTableAppearance() {
        threadTable.setBackground(new Color(45, 45, 45));
        threadTable.setForeground(Color.WHITE);
        threadTable.setGridColor(new Color(60, 60, 60));
        threadTable.getTableHeader().setBackground(new Color(40, 40, 40));
        threadTable.getTableHeader().setForeground(Color.black);
        
        threadTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        threadTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        threadTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        threadTable.setShowGrid(true);
        threadTable.setIntercellSpacing(new Dimension(1, 1));
    }

    private void updateThreadStatus() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }

        Thread[] threads = new Thread[rootGroup.activeCount() * 2];
        int count = rootGroup.enumerate(threads, true);
        
        List<Agent> agents = agentManager.getAgents();
        tableModel.setRowCount(0);
        
        int agentIndex = 1;
        for (Agent agent : agents) {
            for (int i = 0; i < count; i++) {
                if (threads[i] != null) {
                    AgentState enumstate = agent.getState();
                    switch(enumstate){
                        case HOSPITAL:
                            state = "RUNNABLE";
                            break;
                        case HEALTHY:
                            state = "RUNNABLE";
                            break;
                        case HEALING:
                            state = "TIMED_WAITING";
                            break;
                        case INFECTED:
                            state = "RUNNABLE";
                            break;
                        case MUTATED:
                            state = "RUNNABLE";
                            break;
                        case DEAD:
                            state = "TERMINATED";
                            break;
                        default: 
                            state = "ERROR";
                    }
                    Point position = agent.getPosition();
                    String positionStr = position.x + "," + position.y;
                    tableModel.addRow(new Object[]{"Agent " + agentIndex, enumstate.toString(), state, positionStr});
                    break;
                }
            }
            agentIndex++;
        }
    }

    private void startUpdates() {
        updateTimer = new Timer(100, e -> updateThreadStatus());
        updateTimer.start();
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        super.dispose();
    }
}
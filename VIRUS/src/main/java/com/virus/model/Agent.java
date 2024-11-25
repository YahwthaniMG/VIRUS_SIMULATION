package com.virus.model;

import com.virus.model.enums.AgentState;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

public abstract class Agent implements Runnable {
    protected Point position;
    protected Image image;
    protected boolean isRunning;
    protected static final Random random = new Random();
    protected static final int MOVE_SPEED = 2;
    protected static int MOVEMENT_DELAY = 50;          // Default value
    protected static int IMMUNITY_DURATION = 3000;     // Default value
    protected static int LIFE_DURATION = 10000;        // Default value
    protected static int HEALING_DURATION = 5000;      // Default value
    private long immunityTime = 0;
    private boolean deathTimerStarted = false;
    protected  long creationTime = 0;
    
    // Usar AtomicReference para garantizar thread safety en cambios de estado
    protected final AtomicReference<AgentState> state;
    protected final Object stateLock = new Object();
    
    public Agent(int mapWidth, int mapHeight, String imagePath, AgentState initialState) {
        this.position = new Point(
            random.nextInt(mapWidth - 30),
            random.nextInt(mapHeight - 30)
        );
        this.isRunning = true;
        this.state = new AtomicReference<>(initialState);
        loadImage(imagePath);
        this.creationTime = System.currentTimeMillis();
    }

    // Constructor para DeadAgent y otros que necesiten posición específica
    public Agent(int x, int y, String imagePath, AgentState initialState, int op) {
        this.position = new Point(x, y);
        this.isRunning = true;
        this.state = new AtomicReference<>(initialState);
        loadImage(imagePath);
        //this.creationTime = System.currentTimeMillis();
    }

    public long getLifeTime() {
        return System.currentTimeMillis() - creationTime;
    }

    private void loadImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Image imagen = ImageIO.read(file);
                this.image = imagen.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                return;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
        }
    }

    public static void updateConfigurations(SimulationConfig config) {
        MOVEMENT_DELAY = config.getMovementDelay();
        IMMUNITY_DURATION = config.getImmunityDuration();
        LIFE_DURATION = config.getLifeDuration();
        HEALING_DURATION = config.getHealingDuration();
    }

    @Override
    public void run() {
        while (isRunning) {
            if (getState().canMove()) {
                move();
            }
            
            if (getState().needsTimer()) {
                handleStateTimer();
            }
            
            try {
                Thread.sleep(MOVEMENT_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected void move() {
        int newX = 0;
        int newY = 0;
        
        int direction = random.nextInt(4);
        int dx = random.nextInt(10); 
        int dy = random.nextInt(10); 
        
        switch (direction) {
            case 0 -> { 
                newX = position.x + (dx * MOVE_SPEED);
                newY = position.y + (dy * MOVE_SPEED);
            }
            case 1 -> {
                newX = position.x - (dx * MOVE_SPEED);
                newY = position.y - (dy * MOVE_SPEED);
            }
            case 2 -> {
                newX = position.x + (dx * MOVE_SPEED);
                newY = position.y - (dy * MOVE_SPEED);
            }
            case 3 -> {
                newX = position.x - (dx * MOVE_SPEED);
                newY = position.y + (dy * MOVE_SPEED);
            }
        }
        
        // Mantener dentro de los límites del mapa
        newX = (newX > 1160) ? 40 : newX;
        newX = (newX < 40) ? 1160 : newX;
        newY = (newY < 40) ? 740 : newY;
        newY = (newY > 740) ? 40 : newY;
        position.setLocation(newX, newY);
    }

    public void setState(AgentState newState) {
        synchronized (stateLock) {
            AgentState oldState = state.get();
            state.set(newState);
            
            // Si cambia a MUTATED, iniciar timer de muerte
            if (newState == AgentState.MUTATED) {
                startDeathTimer();
            }
            
            // Si cambia a DEAD, crear DeadAgent
            if (newState == AgentState.DEAD) {
                createDeadAgent();
            }
            
            // Actualizar imagen según el nuevo estado
            updateImageForState(newState);
        }
    }

    private void startDeathTimer() {
        if (!deathTimerStarted) {
            deathTimerStarted = true;
            new Thread(() -> {
                try {
                    Thread.sleep(LIFE_DURATION);
                    if (getState() == AgentState.MUTATED) {
                        setState(AgentState.DEAD);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private void handleStateTimer() {
        if (getState() == AgentState.MUTATED) {
            startDeathTimer();
        } else if (getState() == AgentState.HEALING) {// Si está en HEALING, reiniciar el timer
        }
    }

    private void createDeadAgent() {
        DeadAgent deadAgent = new DeadAgent(position.x, position.y, getLifeTime());
        AgentManager.getInstance().replaceAgent(this, deadAgent);
    }

    public AgentState getState() {
        return state.get();
    }

    protected void updateImageForState(AgentState newState) {
        String imagePath = switch (newState) {
            case HEALTHY -> "src/main/resources/images/healthy.png";
            case INFECTED -> "src/main/resources/images/infected.png";
            case MUTATED -> "src/main/resources/images/mutated.png";
            case DEAD -> "src/main/resources/images/dead.png";
            case HEALING -> "src/main/resources/images/healing.png";
            case HOSPITAL -> "src/main/resources/images/hospital.png";
        };
        loadImage(imagePath);
    }

    public Point getPosition() {
        return new Point(position);
    }

    public Image getImage() {
        return image;
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isDead() {
        return false;
    }

    public boolean isHealing() {
        return false;
    }

    protected double getDistanceTo(Agent other) {
        return position.distance(other.getPosition());
    }

    public boolean isInRangeOf(Agent other) {
        double distance = getDistanceTo(other);
        return distance <= other.getState().getInteractionRange();
    }

    protected abstract void handleCollision(Agent other);

    public void setImmunityTime(long time) {
        this.immunityTime = time;
    }
    
    public boolean isImmune() {
        return System.currentTimeMillis() - immunityTime < IMMUNITY_DURATION;
    }
    
    protected boolean canBeInfected() {
        return getState() == AgentState.HEALTHY && !isImmune();
    }
}
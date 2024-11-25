package com.virus.model.enums;

import java.awt.Color;

public enum AgentState {
    HEALTHY("Healthy Agent", new Color(0, 255, 0), true),      // Verde
    HOSPITAL("Hospital", new Color(0, 191, 255), false),       // Azul cielo
    INFECTED("Infected Agent", new Color(255, 0, 0), true),    // Púrpura
    MUTATED("Mutated Agent", new Color(255, 0, 255), true),    // Rojo
    DEAD("Dead Agent", new Color(128, 128, 128), false),       // Gris
    HEALING("Healing Agent", new Color(255, 255, 0), false);   // Amarillo

    private final String description;
    private final Color color;
    private final boolean canMove;

    AgentState(String description, Color color, boolean canMove) {
        this.description = description;
        this.color = color;
        this.canMove = canMove;
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public boolean canMove() {
        return canMove;
    }

    // Métodos útiles para la lógica del juego
    public boolean canBeInfected() {
        return this == HEALTHY;
    }

    public boolean canInfect() {
        return this == INFECTED || this == MUTATED;
    }

    public boolean canMutate() {
        return this == INFECTED;
    }

    public boolean canHeal() {
        return this == INFECTED;
    }

    public boolean isActive() {
        return this != DEAD && this != HEALING;
    }

    // Método para obtener el siguiente estado basado en una interacción
    public AgentState getNextState(AgentState otherState) {
        if (this == HEALTHY && otherState.canInfect()) {
            return INFECTED;
        }
        if (this == INFECTED && otherState == INFECTED) {
            return MUTATED;
        }
        if (this == MUTATED) {
            // Los mutados eventualmente mueren
            return DEAD;
        }
        if (this == INFECTED && otherState == HOSPITAL) {
            return HEALING;
        }
        if (this == HEALING) {
            // Después de curarse, vuelve a estar sano
            return HEALTHY;
        }
        // Si no hay cambio de estado
        return this;
    }

    // Método para verificar si puede interactuar con otro estado
    public boolean canInteractWith(AgentState otherState) {
        return this.isActive() && otherState.isActive() && 
                ((this.canBeInfected() && otherState.canInfect()) ||
                (this == INFECTED && otherState == INFECTED) ||
                (this == INFECTED && otherState == HOSPITAL));
    }

    // Método para obtener el rango de interacción basado en el estado
    public int getInteractionRange() {
        switch (this) {
            case MUTATED:
                return 30;  // Mayor rango para mutados
            case INFECTED:
                return 20;  // Rango estándar para infectados
            case HOSPITAL:
                return 40;  // Mayor rango para hospitales
            default:
                return 15;  // Rango base para otros estados
        }
    }

    // Método para obtener la duración del estado (en milisegundos)
    public long getStateDuration() {
        return switch (this) {
            case HEALING -> 5000;  // 5 segundos para curarse
            case MUTATED -> 10000; // 10 segundos antes de morir
            default -> -1;    // Estados sin duración específica
        };
    }

    // Método para verificar si el estado necesita temporizador
    public boolean needsTimer() {
        return this == HEALING || this == MUTATED;
    }

    @Override
    public String toString() {
        return description;
    }
}
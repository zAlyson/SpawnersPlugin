package com.alysonsantos.aspect.models;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.entity.Entity;

@Builder
@Data
public class EntityModel {

    @NonNull
    private String entityType;

    @NonNull
    private double spawners;

    @NonNull
    private double stack;

    @NonNull
    private boolean activated;

    private Entity entity;

    private double generator;

    public void addSpawner(double quantity) {
        this.setSpawners(this.getSpawners() + quantity);
    }

    public void setStack() {
        this.stack = generator;
    }

    public void addStack(double amount) {
        this.stack = stack + amount;
    }
}

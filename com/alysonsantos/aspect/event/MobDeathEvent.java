package com.alysonsantos.aspect.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class MobDeathEvent extends EntityEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private Player player;
    @Getter
    private double stackSize;
    @Getter
    private double bonusDrop;

    public MobDeathEvent(Entity entity, Player player, double stackSize, double bonusDrop) {
        super(entity);
        this.player = player;
        this.bonusDrop = bonusDrop;
        this.stackSize = stackSize;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

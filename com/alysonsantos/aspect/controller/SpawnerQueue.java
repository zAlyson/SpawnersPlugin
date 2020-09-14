package com.alysonsantos.aspect.controller;

import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.utils.cache.CacheQueue;

public class SpawnerQueue extends CacheQueue<Spawner> {

    public SpawnerQueue() {
        super();
    }

    public void removeItem(Spawner spawner) {
        removeItem(it -> it.getPosition().equals(spawner.getPosition()));
    }

    public final int getInQueue() {
        return this.getQueue().size();
    }
}

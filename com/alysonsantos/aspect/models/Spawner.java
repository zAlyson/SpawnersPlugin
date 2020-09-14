package com.alysonsantos.aspect.models;


import com.alysonsantos.aspect.utils.Formats;
import de.tr7zw.nbtinjector.NBTInjector;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import lombok.var;
import me.lucko.helper.serialize.Position;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class Spawner {

    private String owner;

    private Set<EntityModel> entity;

    private Position position;

    private boolean activated;

    private double bonusDrop;

    private int bonusDropLevel,
            activationLimitMobSpawner,
            activationLimitLevel,
            delaysSpawnLevel,
            delaySpawnCount,
            delaySpawn,
            upgrades;

    public final double getAmountAllSpawners() {
        double amount = 0;
        for (EntityModel entityModel : this.getEntity()) {
            amount = amount + entityModel.getSpawners();
        }

        return amount;
    }

    public EntityModel getEntity(String entityType) {
        for (EntityModel entityModel : this.getEntity()) {
            if (entityModel.getEntityType().equals(entityType)) {
                return entityModel;
            }
        }

        return null;
    }

    public final double getAllTheStackMobs() {
        var amount = 0.0;
        for (EntityModel entityModel : this.getEntity()) {
            amount = entityModel.getGenerator() + amount;
        }

        return amount;
    }

    public void spawnEntity(final Player player, final String entityName) {

        val entityModel = getEntity(entityName);
        var entity = entityModel.getEntity();

        if (entity == null || entity.isDead()) {

            val location = player.getLocation();
            entity = NBTInjector.patchEntity(location
                    .getWorld()
                    .spawnEntity(
                            location,
                            EntityType.valueOf(entityName)
                    ));

            entity.setCustomName("§e" + Formats.apply(entityModel.getGenerator()));

            val nbtCompound = NBTInjector.getNbtData(entity);
            nbtCompound.setDouble("amount", entityModel.getGenerator());
            nbtCompound.setDouble("bonusDrop", bonusDrop);

            entityModel.setStack();
            entityModel.setGenerator(0);

            entityModel.setEntity(entity);
            return;
        }

        val nbtCompound = NBTInjector.getNbtData(entity);

        entityModel.addStack(entityModel.getGenerator());

        nbtCompound.setDouble("amount", entityModel.getStack());

        entityModel.setGenerator(0);

        entity.setCustomName("§e" + Formats.apply(entityModel.getStack()));
    }

    public void addUpgradeCount() {
        this.setUpgrades(this.getUpgrades() + 1);
    }

    public List<String> getHologramText() {
        return Arrays.asList(
                "§6§lESTRUTURA DE SPAWNERS",
                "§bForam gerados §a" + Formats.apply(this.getAllTheStackMobs()) + "§b mobs.",
                "§eAcesse a estrutura para sumoná-los."
        );
    }

    public void ifTime(String entityName) {
        val entity = getEntity(entityName);

        if (delaySpawnCount == 0) {
            delaySpawnCount = delaySpawn;

            entity.setGenerator(entity.getSpawners() + entity.getGenerator());
        }

        delaySpawnCount--;
    }
}

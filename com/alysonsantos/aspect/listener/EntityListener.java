package com.alysonsantos.aspect.listener;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.event.MobDeathEvent;
import com.alysonsantos.aspect.utils.Formats;
import de.tr7zw.nbtinjector.NBTInjector;
import lombok.val;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class EntityListener implements Listener {

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        val entity = event.getEntity();
        if (entity instanceof Player)
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

            val nbtCompound = NBTInjector.getNbtData(entity);
            if (nbtCompound == null)
                return;

            if (!nbtCompound.hasKey("amount"))
                return;

            val stackEntity = nbtCompound.getDouble("amount");

            val damage = event.getDamage();
            val health = ((LivingEntity) entity).getHealth();

            if (damage >= health) {
                double deathStep = Math.min(stackEntity, calculateStep(stackEntity));

                if (deathStep == 0.0)
                    return;

                if (deathStep > stackEntity)
                    return;

                val location = entity.getLocation();
                val newEntity = NBTInjector.patchEntity(location
                        .getWorld()
                        .spawnEntity(
                                location,
                                entity.getType()
                        ));

                newEntity.setCustomName("§e" + Formats.apply(stackEntity - deathStep));

                val newNbtCompound = NBTInjector.getNbtData(newEntity);
                newNbtCompound.setDouble("amount", stackEntity - deathStep);
                newNbtCompound.setDouble("bonusDrop", nbtCompound.getDouble("bonusDrop"));

                val mobDeathEvent = new MobDeathEvent(entity, (Player) event.getDamager(), deathStep, nbtCompound.getDouble("bonusDrop"));
                AspectSpawners.getPlugin().getServer().getPluginManager().callEvent(mobDeathEvent);
            }
        }
    }

    private double calculateStep(double maxStep) {
        double minStep = 1000 > maxStep ? maxStep : 1000;
        return ThreadLocalRandom.current().nextDouble(minStep, maxStep + 1);
    }

}

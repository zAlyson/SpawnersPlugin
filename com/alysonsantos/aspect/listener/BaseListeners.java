package com.alysonsantos.aspect.listener;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.controller.StorageController;
import com.alysonsantos.aspect.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.lucko.helper.Schedulers;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.scheduler.Scheduler;
import me.lucko.helper.serialize.Position;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashSet;

@RequiredArgsConstructor
public class BaseListeners implements Listener {

    private final static Scheduler SCHEDULERS = Schedulers.async();
    private final static Gson GSON = GsonProvider.standard();

    private final AspectSpawners plugin;
    private final StorageController storageController;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        val player = event.getPlayer();
        val usersCache = storageController.getUsersCache();

        if (usersCache.getIfPresent(player.getName()) == null) {

            val gsonStorageHandler = plugin.getGsonStorageHandler();
            if (gsonStorageHandler.get(player.getName()) == JsonNull.INSTANCE) {
                usersCache.put(player.getName(), new User(player.getName(), new HashSet<>()));
                return;
            }

            SCHEDULERS.run(() -> {
//                long tempoInicial = System.currentTimeMillis();
//                System.out.println(gsonStorageHandler.get(player.getName()));
                val newUser = GSON.fromJson(gsonStorageHandler.get(player.getName()), User.class);
                storageController.getUsersCache().put(player.getName(), newUser);

                process(newUser.getPositions());
//
//                long tempoFinal = System.currentTimeMillis();
//
//                System.out.printf("%.3f ms%n", (tempoFinal - tempoInicial) / 1000d);
            });

        }
    }

    private void process(final Collection<Position> positions) {
        if (positions.isEmpty())
            return;

        val spawnerCache = plugin.getStorageController()
                .getSpawnerCache();

        for (final Position position : positions) {
            if (spawnerCache.getIfPresent(position) != null)
                continue;

            spawnerCache.put(
                    position.toLocation(),
                    plugin.getRepository().selectOne(position)
            );
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!(action.equals(Action.RIGHT_CLICK_BLOCK))) return;

        final Block clickedBlock = event.getClickedBlock();
        if (!(clickedBlock.getType() == Material.MOB_SPAWNER)) return;

        plugin.getMenus().openStructureMenu(
                storageController.getSpawner(clickedBlock.getLocation()),
                event.getPlayer()
        );
    }

    @EventHandler
    public void spawnerSpawn(final SpawnerSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void entitySpawn(final EntitySpawnEvent event) {
        noAI(event.getEntity());
    }

    private void noAI(Entity entity) {
        final net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbtTag = nmsEntity.getNBTTag();

        if (nbtTag == null)
            nbtTag = new NBTTagCompound();

        nmsEntity.c(nbtTag);
        nbtTag.setInt("NoAI", 1);
        nmsEntity.f(nbtTag);
    }
}

package com.alysonsantos.aspect.listener;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.controller.StorageController;
import com.alysonsantos.aspect.models.EntityModel;
import com.alysonsantos.aspect.models.Spawner;
import com.google.gson.Gson;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Schedulers;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.scheduler.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class BlockBreakListener implements Listener {

    private final static Scheduler SCHEDULERS = Schedulers.async();
    private static final Gson GSON = GsonProvider.standard();
    private final AspectSpawners plugin;

    @EventHandler
    public void blockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (block.getType() != Material.MOB_SPAWNER)
            return;

        final StorageController storageController = plugin.getStorageController();

        final Spawner spawner = storageController.getSpawner(block.getLocation());
        if (spawner == null)
            return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (!spawner.getOwner().equals(player.getName())) {
            player.sendMessage(
                    "\n " +
                            "§e Ops! Essa estrutura não é sua." +
                            "\n "
            );
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(new String[]{"", "§c Ops! Seu inventário está cheio.", ""});
            return;
        }

        final NBTItem nbtItem = new NBTItem(
                ItemStackBuilder.of(Material.MOB_SPAWNER)
                        .name("§aEstrutura de spawner")
                        .lore
                                (
                                        "§7Proprietário: §f" + spawner.getOwner(),
                                        "",
                                        "§7 Bônus drop: §f" + spawner.getBonusDrop(),
                                        "§7 Tempo para spawnar: §f" + spawner.getDelaySpawn() + "s",
                                        "§7 Limite de ativação: §f" + spawner.getActivationLimitMobSpawner()
                                )
                        .build()
        );

        for (EntityModel model : spawner.getEntity()) {
            model.setEntity(null);
        }

        nbtItem.setString("spawner", GSON.toJson(spawner));
        player.getInventory().addItem(nbtItem.getItem());

        storageController.delete(player, spawner);
        player.sendMessage("§aEstrutura removida com sucesso!");

        block.setType(Material.AIR);
    }
}

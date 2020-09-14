package com.alysonsantos.aspect.commands;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.util.command.Execution;
import com.alysonsantos.aspect.util.command.annotations.Command;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;

@RequiredArgsConstructor
public class CommandSpawners {

    private final AspectSpawners plugin;

    @Command(
            name = "spawner"
    )
    public void commandSpawner(Execution execution) {

    }

    @Command(
            name = "spawner.info"
    )
    public void commandInfo(Execution execution) {

        val spawnerQueue = plugin.getSpawnerQueue();
        val storageController = plugin.getStorageController();

        val stringBuilder = new StringBuilder();
        stringBuilder
                .append("\n ")
                .append("§a§l SPAWNERS INFO:")
                .append("\n ")
                .append("\n ")
                .append("  §aSpawners na fila para atualização no")
                .append("\n ").append("   §abanco de dados: §f§l").append(spawnerQueue.getInQueue())
                .append("\n ")
                .append("\n ").append("§a  Total de spawners ativo no momento: §f§n").append(storageController.getSpawnerCache().estimatedSize())
                .append("§f\n ");

        execution.sendMessage(stringBuilder.toString());
    }

    @Command(
            name = "spawner.give"
    )
    public void commandGive(Execution execution) {

        final NBTItem nbtItem = new NBTItem(
                ItemStackBuilder.of(Material.MOB_SPAWNER)
                        .name("§aEstrutura de spawner")
                        .lore
                                (
                                        "",
                                        "§7 Bônus drop: §f" + 0,
                                        "§7 Tempo para spawnar: §f40s",
                                        "§7 Limite de ativação: §f1",
                                        ""
                                )
                        .build()
        );

        nbtItem.setBoolean("newSpawner", true);
        execution.getPlayer().getInventory().addItem(nbtItem.getItem());
    }
}

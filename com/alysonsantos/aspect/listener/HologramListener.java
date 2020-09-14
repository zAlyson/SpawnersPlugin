package com.alysonsantos.aspect.listener;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.controller.StorageController;
import com.alysonsantos.aspect.models.Spawner;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;

@RequiredArgsConstructor
public class HologramListener implements Listener {
    private static final Set<Material> transparentBlocks = Collections.singleton(Material.AIR);

    private final StorageController storageController;
    private final List<Player> viewers = new ArrayList<>();

    @EventHandler
    private void onPlayerMove(final PlayerMoveEvent e) {
        if (e.isCancelled() || viewers.contains(e.getPlayer())) {
            return;
        }

        try {
            val spawnerBlock = getTargetSpawner(e.getPlayer());
            if (spawnerBlock == null) {
                return;
            }

            val spawner = storageController.getSpawner(spawnerBlock.getLocation());
            if (spawner == null) {
                return;
            }

            setSpawnerHologram(e.getPlayer(), spawner);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private Block getTargetSpawner(final Player player) {
        BlockIterator itr = new BlockIterator(player, 3);

        Block block = null;
        try {
            while (true) {
                val nextBlock = itr.next();
                if (nextBlock.getType() != Material.AIR) {
                    block = nextBlock;
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            // BlockIterator has no more elements
        }

        if (block != null && block.getType() != Material.MOB_SPAWNER) {
            return null;
        }

        return block;
    }

    private void setSpawnerHologram(final Player player, final Spawner spawner) {
        val loc = spawner.getPosition().add(0.5, 1.8 + 0.2, 0.5).toLocation();
        val hologram = createHologramFor(player, loc);

        val lines = new ArrayList<HologramLine>();
        for (String line : spawner.getHologramText()) {
            lines.add(hologram.appendTextLine(line));
        }

        viewers.add(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()
                        || !viewers.contains(player)
                        || !spawner.getPosition().toLocation().equals(player.getTargetBlock(transparentBlocks, 3).getLocation())) {
                    cancel();
                    hologram.delete();
                    viewers.remove(player);
                    return;
                }

                // Update the hologram if needed
                val newLines = spawner.getHologramText();
                for (int i = 0; i < newLines.size(); i++) {
                    val currentLine = lines.get(i);
                    if (currentLine instanceof TextLine) {
                        TextLine textLine = (TextLine) currentLine;
                        String newValue = newLines.get(i);

                        if (!textLine.getText().equals(newValue)) {
                            textLine.setText(newValue);
                        }
                    }
                }
            }
        }.runTaskTimer(AspectSpawners.getPlugin(), 5L, 5L);
    }

    private Hologram createHologramFor(final Player player, final Location location) {
        val hologram = HologramsAPI.createHologram(AspectSpawners.getPlugin(), location);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        hologram.getVisibilityManager().showTo(player);
        return hologram;
    }
}
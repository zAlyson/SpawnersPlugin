package com.alysonsantos.aspect.listener;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.controller.StorageController;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.models.User;
import com.google.gson.Gson;
import de.tr7zw.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.lucko.helper.Schedulers;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.serialize.Position;
import net.minecraft.server.v1_8_R3.MobSpawnerAbstract;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

@RequiredArgsConstructor
public class BlockPlaceListener implements Listener {

    private static final Gson GSON = GsonProvider.standard();
    private final AspectSpawners plugin;

    @EventHandler
    public void placeBlock(final BlockPlaceEvent event) {
        ItemStack inHand = event.getItemInHand();

        if (inHand == null || inHand.getType() != Material.MOB_SPAWNER) return;

        final NBTItem nbtItem = new NBTItem(inHand);

        if (!nbtItem.hasNBTData())
            return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final Position spawnerPosition = Position.of(block.getLocation());

        Spawner spawner;
        if (nbtItem.hasKey("spawner")) {
            spawner = GSON.fromJson(nbtItem.getString("spawner"), Spawner.class);

            spawner.setPosition(spawnerPosition);
            spawner.setActivated(false);
            spawner.setOwner(player.getName());
        } else {
            spawner = Spawner.builder()
                    .entity(new HashSet<>())
                    .owner(player.getName())
                    .activated(false)
                    .activationLimitMobSpawner(1)
                    .activationLimitLevel(0)
                    .bonusDrop(0)
                    .bonusDropLevel(0)
                    .delaySpawn(40)
                    .delaysSpawnLevel(0)
                    .position(spawnerPosition)
                    .upgrades(0)
                    .delaySpawnCount(40)
                    .build();
        }

        apply(player, spawner, event);
    }

    private void apply(final Player player, final Spawner spawner, final BlockPlaceEvent event) {
        final StorageController storageController = plugin.getStorageController();
        final User user = storageController.getUsersCache().getIfPresent(player.getName());

        final Block block = event.getBlock();
        final BlockState blockState = block.getState();
        final CreatureSpawner creatureSpawner = ((CreatureSpawner) blockState);

        if (user != null) {

            if (user.getPositions().isEmpty()) {
                user.getPositions().add(spawner.getPosition());

                storageController.getSpawnerCache()
                        .put(
                                block.getLocation(),
                                spawner
                        );

                Schedulers.async()
                        .run(() -> {
                            plugin.getRepository().insert(spawner.getPosition(), spawner);

                            val storageHandler = plugin.getGsonStorageHandler();
                            storageHandler.insert(player.getName(), GSON.toJsonTree(user))
                                    .save();
                        });

                change(creatureSpawner);
                return;
            }

            for (final Position position : user.getPositions()) {

                final Spawner userSpawner = storageController.getSpawner(position.toLocation());
                if (userSpawner.getOwner().equals(player.getName())) {
                    player.sendMessage("§c\n Ops! Você já possui uma estrutura.\n");
                    event.setCancelled(true);
                    return;
                } else {
                    user.getPositions().add(spawner.getPosition());

                    storageController.getSpawnerCache()
                            .put(
                                    block.getLocation(),
                                    spawner
                            );

                    val storageHandler = plugin.getGsonStorageHandler();
                    storageHandler.insert(player.getName(), GSON.toJsonTree(user))
                            .save();

                    change(creatureSpawner);
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void change(CreatureSpawner creatureSpawner) {
        MobSpawnerAbstract spawnerAbstract = ((CraftCreatureSpawner) creatureSpawner).getTileEntity().getSpawner();

        NBTTagCompound spawnData = new NBTTagCompound();
        NBTTagCompound item = new NBTTagCompound();
        NBTTagCompound itemData = new NBTTagCompound();

        spawnerAbstract.b(spawnData);

        ItemStack itemStack = new ItemStack(Material.EMERALD);
        net.minecraft.server.v1_8_R3.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);

        itemStackNMS.save(itemData);
        item.set("Item", itemData);

        spawnData.remove("SpawnPotentials");
        spawnData.set("EntityId", new NBTTagString("Item"));
        spawnData.set("SpawnData", item);

        spawnerAbstract.a(spawnData);
        creatureSpawner.update();
    }
}

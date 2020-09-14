package com.alysonsantos.aspect.controller;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.models.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import me.lucko.helper.Schedulers;
import me.lucko.helper.gson.GsonProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class StorageController {

    private static final Gson GSON = GsonProvider.standard();

    private final Cache<String, User> usersCache = Caffeine.newBuilder().build();
    private final Cache<Location, Spawner> spawnerCache = Caffeine.newBuilder()
            .build();

    public final Spawner getSpawner(final Location location) {
        return spawnerCache.getIfPresent(location);
    }

    public void delete(final Player player, final Spawner spawner) {
        val plugin = AspectSpawners.getPlugin();
        val repository = plugin.getRepository();

        Schedulers.async()
                .run(() -> {
                    spawnerCache.asMap().remove(spawner.getPosition().toLocation());
                    repository.delete(spawner.getPosition());

                    val user = usersCache.getIfPresent(player.getName());
                    user.getPositions().removeIf(position -> position.equals(spawner.getPosition()));

                    val storageHandler = plugin.getGsonStorageHandler();
                    storageHandler.insert(player.getName(), GSON.toJsonTree(user))
                            .save();

                    plugin.getRepository().delete(spawner.getPosition());
                });
    }
}

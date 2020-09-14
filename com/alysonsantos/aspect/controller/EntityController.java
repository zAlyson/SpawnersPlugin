package com.alysonsantos.aspect.controller;

import com.alysonsantos.aspect.models.EntityModel;
import com.alysonsantos.aspect.models.Spawner;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.lucko.helper.scheduler.builder.TaskBuilder;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class EntityController {

    private final StorageController storageController;

    public void initController() {

        TaskBuilder.newBuilder()
                .async()
                .every(20)
                .run(
                        () -> {

                            val values = storageController.getSpawnerCache()
                                    .asMap()
                                    .values();

                            for (final Spawner spawner : values) {

                                if (!spawner.isActivated())
                                    continue;

                                if (spawner.getEntity().isEmpty())
                                    continue;

                                for (final EntityModel entityModel : spawner.getEntity()) {

                                    if (!entityModel.isActivated())
                                        continue;

                                    spawner.ifTime(entityModel.getEntityType());

                                }
                            }
                        }
                );
    }
}

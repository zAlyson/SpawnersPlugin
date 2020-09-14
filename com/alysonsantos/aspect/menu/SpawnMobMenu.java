package com.alysonsantos.aspect.menu;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.models.EntityModel;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.utils.Formats;
import com.alysonsantos.aspect.utils.HeadGenerator;
import lombok.val;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SpawnMobMenu extends Gui {

    private final HeadGenerator headGenerator = HeadGenerator.getInstance();
    private final AspectSpawners plugin = AspectSpawners.getPlugin();
    private final Spawner spawner;

    private final int[] slots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};

    public SpawnMobMenu(Player player, Spawner spawner) {
        super(player, 5, "§8Mobs stackados");
        this.spawner = spawner;
    }

    @Override
    public void redraw() {

        setItem(36,
                Item.builder(
                        ItemStackBuilder.of(Material.ARROW)
                                .name("§aVoltar")
                                .lore
                                        (
                                                "§fClique aqui para voltar."
                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    plugin.getMenus().openStructureMenu(spawner, getPlayer());

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build());

        setItem(39,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromURL("http://textures.minecraft.net/texture/4b599c618e914c25a37d69f541a22bebbf751615263756f2561fab4cfa39e"))
                                .name("§aSumonar mobs")
                                .lore
                                        (
                                                "",
                                                "§7 Este é um recurso §6VIP§7. Você sabia que por menos",
                                                "§7 de§6 R$ 0,50§7 por dia você consegue se tornar um vip?",
                                                "",
                                                "§aClique para sumonar todos os mobs."
                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build());

        setItem(41,
                Item.builder(
                        ItemStackBuilder.of(Material.WATCH)
                                .name("§bAtualizar")
                                .lore
                                        (
                                                "",
                                                "§7 Próxima geração em §b" + spawner.getDelaySpawnCount() + "§7 segundos.",
                                                "§7 Clique aqui para atualizar.",
                                                ""
                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    open();

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build());

        if (spawner.getAllTheStackMobs() <= 0) {

            setItem(13,
                    Item.builder(
                            ItemStackBuilder.of(Material.WEB)
                                    .name("§cNão há mobs para sumonar agora...")
                                    .build()
                    ).build());

            return;
        }

        val entityModels = spawner.getEntity();
        int index = 0;
        for (EntityModel entity : entityModels) {
            if (entity.getGenerator() <= 0)
                continue;

            setItem(slots[index],
                    Item.builder(
                            ItemStackBuilder.of(getHead(entity.getEntityType()))
                                    .name("§a" + getName(entity.getEntityType()))
                                    .lore
                                            (
                                                    "§7Clique aqui para sumonar §e" + Formats.apply(entity.getGenerator()) + "§7 " + getName(entity.getEntityType()).toLowerCase() + "."
                                            )
                                    .build()
                    )
                            .bind(
                                    () -> {

                                        getPlayer().sendMessage("\n  §a§lESTRUTURA:\n §7 → Um total de §e" + Formats.apply(entity.getGenerator()) + " " + getName(entity.getEntityType()).toLowerCase() + " §7foram sumodas. \n");
                                        spawner.spawnEntity(getPlayer(), entity.getEntityType());

                                        getPlayer().closeInventory();

                                        plugin.getSpawnerQueue().addItem(spawner);
                                    }, ClickType.RIGHT, ClickType.LEFT
                            )
                            .build());

            index++;
        }
    }

    private final ItemStack getHead(String entityType) {

        switch (entityType) {
            case "CHICKEN": {
                return headGenerator.fromEntityType(EntityType.CHICKEN);
            }

            case "RABBIT": {
                return headGenerator.fromEntityType(EntityType.RABBIT);
            }

            case "WOLF": {
                return headGenerator.fromEntityType(EntityType.WOLF);
            }

            case "SHEEP": {
                return headGenerator.fromEntityType(EntityType.SHEEP);
            }

            default:
                return headGenerator.fromEntityType(EntityType.CHICKEN);

        }
    }

    private final String getName(String entityType) {

        switch (entityType) {
            case "CHICKEN": {
                return "Galinha";
            }

            case "RABBIT": {
                return "Coelho";
            }

            case "WOLF": {
                return "Lobo";
            }

            case "SHEEP": {
                return "Ovelha";
            }

            default:
                return "null";

        }
    }

}

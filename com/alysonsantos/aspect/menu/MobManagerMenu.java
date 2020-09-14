package com.alysonsantos.aspect.menu;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.controller.SpawnerQueue;
import com.alysonsantos.aspect.models.EntityModel;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.utils.Formats;
import com.alysonsantos.aspect.utils.HeadGenerator;
import de.tr7zw.nbtapi.NBTItem;
import lombok.val;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MobManagerMenu extends Gui {

    private final SpawnerQueue queue = AspectSpawners.getPlugin().getSpawnerQueue();
    private final HeadGenerator headGenerator = HeadGenerator.getInstance();
    private final Spawner spawner;

    private final int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31};

    public MobManagerMenu(Player player, Spawner spawner) {
        super(player, 6, "§8Gerenciar Mobs");
        this.spawner = spawner;
    }

    @Override
    public void redraw() {

        for (int slot : slots) {
            setItem(
                    slot,
                    Item.builder(
                            ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                                    .durability(7)
                                    .name("§cVocê ainda não possui")
                                    .lore("§ceste gereador.")
                                    .build()
                    )
                            .build()
            );

        }

        val entity = spawner.getEntity();
        int index = 0;
        for (EntityModel entityModel : entity) {

            setItem(
                    slots[index],
                    Item.builder(
                            ItemStackBuilder.of(getHead(entityModel.getEntityType()))
                                    .name("§a" + getName(entityModel.getEntityType()))
                                    .lore
                                            (

                                                    "",
                                                    " §f▪ Quantidade: §7" + Formats.apply(entityModel.getSpawners()),
                                                    " §7▪ Status: " + (entityModel.isActivated() ? "§aAtivado" : "§cDesativado"),
                                                    "",
                                                    (entityModel.isActivated() ? "§eClique para desativar." : "§aClique para ativar.")

                                            )
                                    .build()
                    )
                            .bind(
                                    () -> {

                                        if (!entityModel.isActivated()) {
                                            final int limit = spawner.getActivationLimitMobSpawner();

                                            int activated = 0;
                                            for (EntityModel model : spawner.getEntity()) {
                                                if (model.isActivated()) {
                                                    activated++;
                                                }
                                            }

                                            if (activated >= limit) {
                                                getPlayer().sendMessage("§cNão é possível ativar mais spawners.");
                                                return;
                                            }
                                        }

                                        entityModel.setActivated(!entityModel.isActivated());
                                        queue.addItem(spawner);

                                        getPlayer().closeInventory();
                                        open();

                                    }, ClickType.RIGHT, ClickType.LEFT
                            )
                            .build()
            );

            index++;
        }

        setItem(45,
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
                                    AspectSpawners.getPlugin()
                                            .getMenus()
                                            .openStructureMenu
                                                    (
                                                            spawner,
                                                            getPlayer()
                                                    );

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build());

        setItem(
                3,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromURL("http://textures.minecraft.net/texture/db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd"))
                                .name("§aInformações")
                                .lore
                                        (
                                                "",
                                                " §f∙ §7Total de spawners: §a" + Formats.apply(spawner.getAmountAllSpawners()),
                                                "",
                                                "§7Clique com §8Shift§7 + §8Esquerdo",
                                                "§7para remover spawners."
                                        )
                                .build()
                )
                        .bind(() -> {

                            for (final EntityModel model : spawner.getEntity()) {
                                final NBTItem nbtItem = new NBTItem(ItemStackBuilder.of(getHead(model.getEntityType()))
                                        .name("§eGerador de Monstros")
                                        .lore
                                                (
                                                        "§7Tipo: §f" + getName(model.getEntityType()),
                                                        "§7Quantidade: §f" + Formats.apply(model.getSpawners()),
                                                        "",
                                                        "§7Para usar este gerador você",
                                                        "§7deve ativá-lo em uma estrutura.",
                                                        "§7Para isso, clique com o botão direito."
                                                )
                                        .build());

                                nbtItem.setString("entityType", model.getEntityType());
                                nbtItem.setDouble("amount", model.getSpawners());

                                getPlayer().getInventory().addItem(nbtItem.getItem());
                            }

                            spawner.getEntity().clear();
                            getPlayer().closeInventory();

                        }, ClickType.SHIFT_RIGHT)
                        .build()
        );

        setItem(
                5,
                Item.builder(
                        ItemStackBuilder.of(Material.REDSTONE_TORCH_ON)
                                .name("§cClique para desativar todos os mobs!")
                                .lore
                                        (
                                                "§7Desative todos os mobs com apenas um click."
                                        )
                                .build()
                )
                        .bind(() -> {

                            if (spawner.getEntity().isEmpty()) {
                                getPlayer().sendMessage("§cA estrutura não possui spawners.");
                                return;
                            }

                            for (EntityModel model : spawner.getEntity()) {
                                model.setActivated(false);
                            }

                            queue.addItem(spawner);

                        }, ClickType.LEFT, ClickType.RIGHT)
                        .build()
        );
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
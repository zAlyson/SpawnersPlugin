package com.alysonsantos.aspect.menu;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.api.EconomyApi;
import com.alysonsantos.aspect.api.EconomyProvider;
import com.alysonsantos.aspect.controller.SpawnerQueue;
import com.alysonsantos.aspect.models.EntityModel;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.utils.ChatValue;
import com.alysonsantos.aspect.utils.Formats;
import com.alysonsantos.aspect.utils.HeadGenerator;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ShopMenu extends Gui {

    private final SpawnerQueue queue = AspectSpawners.getPlugin().getSpawnerQueue();
    private final HeadGenerator headGenerator = HeadGenerator.getInstance();
    private final EconomyApi economyApi = EconomyProvider.get();
    private final Spawner spawner;

    public ShopMenu(Player player, Spawner spawner) {
        super(player, 5, "Comprar spawner");
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
                10,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromURL("http://textures.minecraft.net/texture/1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893"))
                                .name("§aGalinha")
                                .lore
                                        (

                                                "",
                                                " §7▪ Preço do spawner: §a$ 250 mil",
                                                " §7▪ Seu limite de compra: §e 1000",
                                                "",
                                                "§aClique para consseguir."

                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    getPlayer().sendMessage("\n §a Digite a quantia de spawners que deseja comprar.\n ");

                                    ChatValue.getValueFrom(getPlayer(), "cancelar", true, gettingValue -> {

                                        try {

                                            double value = Double.parseDouble(gettingValue.getValueString());

                                            if (value <= 0) {
                                                getPlayer().sendMessage("§cDigite um valor válido.");
                                                return;
                                            }

                                            if ((value * 250000) > economyApi.getBalance(getPlayer().getName())) {
                                                getPlayer().sendMessage("§cVocê não possuí dinheiro suficiente.");
                                                return;
                                            }

                                            //economyApi.remove(getPlayer().getName(), (value * 250000));

                                            getPlayer().sendMessage("\n §aYeah! Você comprou §7" + Formats.apply(value) + "§a spawners de galinha por §7$" + Formats.apply((value * 250000)) + "§a.\n ");

                                            if (spawner.getEntity("CHICKEN") != null) {
                                                spawner.getEntity("CHICKEN").addSpawner(value);
                                            } else {
                                                spawner.getEntity().add(
                                                        EntityModel.builder()
                                                                .activated(false)
                                                                .stack(0)
                                                                .spawners(value)
                                                                .entityType("CHICKEN")
                                                                .build()
                                                );
                                            }

                                            queue.addItem(spawner);

                                        } catch (NumberFormatException e) {
                                            getPlayer().sendMessage("§cDigite um valor válido.");
                                        }

                                    }, gettingValue -> getPlayer().sendMessage("\n §cAção cancelada com êxito. \n"));

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build()
        );


        setItem(
                11,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromURL("http://textures.minecraft.net/texture/cec242e667aee44492413ef461b810cac356b74d8718e5cec1f892a6b43e5e1"))
                                .name("§aCoelho")
                                .lore
                                        (

                                                "",
                                                " §7▪ Preço do spawner: §a$ 300 mil",
                                                " §7▪ Seu limite de compra: §e 1000",
                                                "",
                                                "§aClique para consseguir."

                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    getPlayer().sendMessage("\n §a Digite a quantia de spawners que deseja comprar.\n ");

                                    ChatValue.getValueFrom(getPlayer(), "cancelar", true, gettingValue -> {

                                        try {

                                            final double value = Double.parseDouble(gettingValue.getValueString());

                                            if (value <= 0) {
                                                getPlayer().sendMessage("§cDigite um valor válido.");
                                                return;
                                            }

                                            if ((value * 300000) > economyApi.getBalance(getPlayer().getName())) {
                                                getPlayer().sendMessage("§cVocê não possuí dinheiro suficiente.");
                                                return;
                                            }

                                            //economyApi.remove(getPlayer().getName(), (value * 250000));

                                            getPlayer().sendMessage("\n §aYeah! Você comprou §7" + Formats.apply(value) + "§a spawners de coelho por §7$" + Formats.apply((value * 300000)) + "§a.\n ");

                                            if (spawner.getEntity("RABBIT") != null) {
                                                spawner.getEntity("RABBIT").addSpawner(value);
                                            } else {
                                                spawner.getEntity().add(
                                                        EntityModel.builder()
                                                                .activated(false)
                                                                .stack(0)
                                                                .spawners(value)
                                                                .entityType("RABBIT")
                                                                .build()
                                                );
                                            }

                                            queue.addItem(spawner);

                                        } catch (NumberFormatException e) {
                                            getPlayer().sendMessage("§cDigite um valor válido.");
                                        }

                                    }, gettingValue -> getPlayer().sendMessage("\n §cAção cancelada com êxito. \n"));

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build()
        );

        setItem(
                12,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromEntityType(EntityType.SHEEP))
                                .name("§aOvelha")
                                .lore
                                        (

                                                "",
                                                " §7▪ Preço do spawner: §a$ 350 mil",
                                                " §7▪ Seu limite de compra: §e 1000",
                                                "",
                                                "§aClique para consseguir."

                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    getPlayer().sendMessage("\n §a Digite a quantia de spawners que deseja comprar.\n ");

                                    ChatValue.getValueFrom(getPlayer(), "cancelar", true, gettingValue -> {

                                        try {

                                            final double value = Double.parseDouble(gettingValue.getValueString());

                                            if (value <= 0) {
                                                getPlayer().sendMessage("§cDigite um valor válido.");
                                                return;
                                            }

                                            if ((value * 350000) > economyApi.getBalance(getPlayer().getName())) {
                                                getPlayer().sendMessage("§cVocê não possuí dinheiro suficiente.");
                                                return;
                                            }

                                            //economyApi.remove(getPlayer().getName(), (value * 250000));

                                            getPlayer().sendMessage("\n §aYeah! Você comprou §7" + Formats.apply(value) + "§a spawners de ovelha por §7$" + Formats.apply((value * 350000)) + "§a.\n ");

                                            if (spawner.getEntity("SHEEP") != null) {
                                                spawner.getEntity("SHEEP").addSpawner(value);
                                            } else {
                                                spawner.getEntity().add(
                                                        EntityModel.builder()
                                                                .activated(false)
                                                                .stack(0)
                                                                .spawners(value)
                                                                .entityType("SHEEP")
                                                                .build()
                                                );
                                            }

                                            queue.addItem(spawner);

                                        } catch (NumberFormatException e) {
                                            getPlayer().sendMessage("§cDigite um valor válido.");
                                        }

                                    }, gettingValue -> getPlayer().sendMessage("\n §cAção cancelada com êxito. \n"));

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build()
        );

        setItem(
                13,
                Item.builder(
                        ItemStackBuilder.of(headGenerator.fromEntityType(EntityType.WOLF))
                                .name("§aLobo")
                                .lore
                                        (

                                                "",
                                                " §7▪ Preço do spawner: §a$ 400 mil",
                                                " §7▪ Seu limite de compra: §e 1000",
                                                "",
                                                "§aClique para consseguir."

                                        )
                                .build()
                )
                        .bind(
                                () -> {

                                    getPlayer().closeInventory();
                                    getPlayer().sendMessage("\n §a Digite a quantia de spawners que deseja comprar.\n ");

                                    ChatValue.getValueFrom(getPlayer(), "cancelar", true, gettingValue -> {

                                        try {

                                            final double value = Double.parseDouble(gettingValue.getValueString());

                                            if (value <= 0) {
                                                getPlayer().sendMessage("§cDigite um valor válido.");
                                                return;
                                            }

                                            if ((value * 400000) > economyApi.getBalance(getPlayer().getName())) {
                                                getPlayer().sendMessage("§cVocê não possuí dinheiro suficiente.");
                                                return;
                                            }

                                            //economyApi.remove(getPlayer().getName(), (value * 250000));

                                            getPlayer().sendMessage("\n §aYeah! Você comprou §7" + Formats.apply(value) + "§a spawners de lobo por §7$" + Formats.apply((value * 400000)) + "§a.\n ");

                                            if (spawner.getEntity("WOLF") != null) {
                                                spawner.getEntity("WOLF").addSpawner(value);
                                            } else {
                                                spawner.getEntity().add(
                                                        EntityModel.builder()
                                                                .activated(false)
                                                                .stack(0)
                                                                .spawners(value)
                                                                .entityType("WOLF")
                                                                .build()
                                                );
                                            }

                                            queue.addItem(spawner);

                                        } catch (NumberFormatException e) {
                                            getPlayer().sendMessage("§cDigite um valor válido.");
                                        }

                                    }, gettingValue -> getPlayer().sendMessage("\n §cAção cancelada com êxito. \n"));

                                }, ClickType.RIGHT, ClickType.LEFT
                        )
                        .build()
        );
    }
}
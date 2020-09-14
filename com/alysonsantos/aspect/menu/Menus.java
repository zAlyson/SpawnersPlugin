package com.alysonsantos.aspect.menu;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.utils.HeadGenerator;
import com.aspectmania.core.utils.MakeItem;
import com.aspectmania.core.utils.TextUtil;
import dev.arantes.inventorymenulib.buttons.ItemButton;
import dev.arantes.inventorymenulib.menus.InventoryGUI;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public class Menus extends InventoryGUI implements Listener {

    private final HeadGenerator headGenerator = HeadGenerator.getInstance();
    private final AspectSpawners plugin;
    private InventoryGUI structure;
    private InventoryGUI upgradeDropBonus;
    private InventoryGUI upgradeDelay;
    private InventoryGUI upgradeLimit;
    private ItemButton onOffStructureButton;

    public Menus(AspectSpawners plugin) {
        super("&7Estrutura", 9 * 5);
        this.plugin = plugin;
    }

    public void createStructureMenu(Spawner spawner, Player player) {
        structure = new InventoryGUI("§8Estrutura", 9 * 5);
        structure.setDefaultCancell(true);

        //Informacoes basicas da estrutura
        MakeItem spawnerStructure = new MakeItem(52);
        spawnerStructure.setName("&aEstrutura de Spawner").addLoreList(
                "",
                "§7 ∙ &fDono da estrutura: &a" + spawner.getOwner(),
                "§7 ∙ &fLimite de ativação: &7" + spawner.getActivationLimitMobSpawner(),
                "§7 ∙ &fDelay de geração: &7" + spawner.getDelaySpawn(),
                "§7 ∙ §fBônus drop: §7" + spawner.getBonusDrop() + "%"
        );

        ItemButton spawnerStructureButton = new ItemButton(spawnerStructure.build());
        spawnerStructureButton.glow(true);
        structure.setButton(13, spawnerStructureButton);
        structure.setDefaultCancell(true);

        //Botao para habilitar e desabilitar a estrutura
        updateOnOffStructure(spawner);

        //Status da estrutura
        MakeItem buySpawner = new MakeItem(408);

        //Gerenciamento de spawners
        buySpawner.setName(TextUtil.color("&aComprar Spawners"));
        buySpawner.addLoreList
                (
                        "",
                        "&7 Estoque spawners! Seja",
                        "&7 o mais rico!",
                        "",
                        "§eClique para prosseguir."
                );
        ItemButton buySpawnerButton = new ItemButton(buySpawner.build()).setDefaultAction(clickEvent -> {
            clickEvent.getWhoClicked().closeInventory();
            new ShopMenu((Player) clickEvent.getWhoClicked(), spawner).open();
        });

        //Gerenciamento de melhorias
        MakeItem improvement = new MakeItem(399);
        improvement.setName(TextUtil.color("&aMelhorias"));
        improvement.addLoreList
                (
                        "",
                        "§7 As melhorias irão lhe ajudar em",
                        "§7 sua jornada para torna-se",
                        "§7 o mais rico.",
                        "",
                        "§eClique para prosseguir."
                );

        ItemButton improvementButton = new ItemButton(improvement.build());
        improvementButton.setDefaultAction(ClickAction -> {
            menuUpgrades(spawner, player);
        });


        //Gerenciamento de mobs
        MakeItem managerMonsters = new MakeItem(headGenerator.fromEntityType(EntityType.CHICKEN));
        managerMonsters.setName(TextUtil.color("&aGerenciar Mobs"));
        managerMonsters.addLoreList
                (
                        "",
                        "§7 Gerencia seus mobs!",
                        "§7 Retire spawners, ative e",
                        "§7 desative mobs.",
                        "",
                        "§eClique para prosseguir."
                );
        ItemButton managerMonstersButton = new ItemButton(managerMonsters.build());
        managerMonstersButton.setDefaultAction(inventoryClickEvent -> {
            if (spawner.getEntity().isEmpty()) {
                player.sendMessage("\n §cVocê ainda não possui geradores... \n");
                return;
            }

            new MobManagerMenu((Player) inventoryClickEvent.getWhoClicked(), spawner).open();
        });


        MakeItem summonMonsters = new MakeItem(416);
        summonMonsters.setName(TextUtil.color("&aSumonar entidades"));
        summonMonsters.addLoreList
                (
                        "",
                        "§7 Para poder matar seus mobs",
                        "§7 antes é necessário sumoná-los.",
                        "",
                        "§eClique para prosseguir."
                );

        ItemButton summonMonstersButton = new ItemButton(summonMonsters.build());
        summonMonstersButton.setDefaultAction(ClickAction -> {
            new SpawnMobMenu(player, spawner).open();
        });

        structure.setButton(13, spawnerStructureButton);

        structure.setButton(29, buySpawnerButton);

        structure.setButton(32, improvementButton);

        structure.setButton(30, managerMonstersButton);

        structure.setButton(33, summonMonstersButton);

        structure.setButton(14,
                new ItemButton(ItemStackBuilder.of(Material.REDSTONE_COMPARATOR)
                        .name("§aPreferências")
                        .lore
                                (
                                        "",
                                        "§7 Personalize sua estrutura!",
                                        "§7 Deixe do seu gosto.",
                                        "",
                                        "§eClique para prosseguir."
                                )
                        .build())
                        .setDefaultAction(inventoryClickEvent -> {
                            inventoryClickEvent.setCancelled(true);
                        }));

        structure.show(player);
    }

    public void menuUpgrades(Spawner spawner, Player p) {

        InventoryGUI menuUpgrades = new InventoryGUI(TextUtil.color("&8Melhorias"), 9 * 3);
        menuUpgrades.setDefaultCancell(true);

        MakeItem upgradeLimit = new MakeItem(390).setName("&aAprimorar limite de ativação")
                .addLoreList("",
                        "&7O limite de ativação é utilizado na aba de",
                        "&aGerenciamento de Mobs&7, através dele é possivel",
                        "&7realizar a ativação de mobs simultaneamente.",
                        "",
                        "&aClique para gerenciar.");
        MakeItem upgradeDelay = new MakeItem(347).setName("&eAprimorar cooldown de spawn")
                .addLoreList("",
                        "&7Cooldown é o intervalo de tempo que a estrutura",
                        "&7leva para gerar outro mob. Diminua este tempo e",
                        "&7deixe sua estrutura mais &eeficaz&7!",
                        "",
                        "&eClique para gerenciar.");
        MakeItem upgradeBonus = new MakeItem(265).setName("&3Aprimorar bônus drop")
                .addLoreList("",
                        "&7Os drops dos mobs são uma ótima fonte de renda",
                        "&7Faça melhorias do bônus drop da sua estrutura",
                        "&7e deixea mais &3eficaz &7e &3rentável&7!",
                        "",
                        "&3Clique para gerenciar.");

        ItemButton buttonLimit = new ItemButton(upgradeLimit.build()).setDefaultAction(ClickType -> {
            if (p.getName().equals(spawner.getOwner())) {
                createUpgradeLimitActivation(p, spawner);
            } else {
                p.sendMessage(TextUtil.color("&cApenas o dono pode gerenciar as melhorias"));
            }
        });

        ItemButton buttonDelay = new ItemButton(upgradeDelay.build()).setDefaultAction(ClickType -> {
            if (p.getName().equals(spawner.getOwner())) {
                createUpgradeSpawnDelay(p, spawner);
            } else {
                p.sendMessage(TextUtil.color("&cApenas o dono pode gerenciar as melhorias"));
            }
        });
        ItemButton buttonBonus = new ItemButton(upgradeBonus.build()).setDefaultAction(ClickType -> {
            if (p.getName().equals(spawner.getOwner())) {
                createUpgradeBonusDrop(p, spawner);
            } else {
                p.sendMessage(TextUtil.color("&cApenas o dono pode gerenciar as melhorias"));
            }
        });

        menuUpgrades.setButton(11, buttonLimit);
        menuUpgrades.setButton(13, buttonDelay);
        menuUpgrades.setButton(15, buttonBonus);

        menuUpgrades.show(p);

    }

    public void menuAccept(ItemStack item, Player p) {
        InventoryGUI invConfirmacao = new InventoryGUI(TextUtil.color("&8Confirmação"), 9 * 5);
        ItemButton itemUpgrade = new ItemButton(item);
        invConfirmacao.setDefaultCancell(true);

        AtomicReference<Boolean> opc = new AtomicReference<>(false);

        ItemButton confirm = new ItemButton(new MakeItem(35, (byte) 5).build()).setDefaultAction(ClickType -> {
            opc.set(true);
            return;
        });
        ItemButton cancel = new ItemButton(new MakeItem(35, (byte) 14).build()).setDefaultAction(ClickType -> {
            opc.set(false);
            return;
        });

        invConfirmacao.setButton(13, itemUpgrade);
        invConfirmacao.setButton(29, confirm);
        invConfirmacao.setButton(33, cancel);

        invConfirmacao.show(p);
    }

    public void createUpgradeBonusDrop(Player player, Spawner spawner) {

        upgradeDropBonus = new InventoryGUI(TextUtil.color("&8Melhoria de drops"), 9 * 3);
        upgradeDropBonus.setDefaultCancell(true);


        plugin.getUpgradesController().getBonusDrops().forEach((level, bonusDrop) -> {

            MakeItem itemButton = new MakeItem(265);
            itemButton.setName("&3Aprimorar drops");
            itemButton.setAmount(level);
            ItemButton button = null;

            itemButton.addLoreList("", "&7Com esta melhoria o drop dos mobs gerados por",
                    "&7essa estrutura terão um acrescimo de &f" +
                            "" + bonusDrop.getBoost() * 100 + "%",
                    "");
            if (spawner.getBonusDropLevel() + 1 == level) {

                itemButton.addLoreList("&fBônus drop atual: &3" + spawner.getBonusDrop() * 100 + "%.",
                        "&fCusto: &d " + bonusDrop.getPriceCoins() + " Tokens &f+ &a $ " + bonusDrop.getPriceTokens() + " coins&f.",
                        "", "&aClique para prosseguir.");
                itemButton.addGlow();
                button = new ItemButton(itemButton.build());
                button.setDefaultAction(ClickType -> {
                    if (spawner.getOwner().equals(player.getName())) {
                        plugin.getUpgradesController().upgradeDrop(player, spawner, level);
                        createUpgradeBonusDrop(player, spawner);
                    } else {
                        player.sendMessage("&cApenas o dono da estrutura pode fazer isso.");
                    }
                });

            } else {
                button = new ItemButton(itemButton.build());
                if (spawner.getBonusDropLevel() + 1 < level) {
                    itemButton.addLoreList("&fBônus drop atual: &3" + spawner.getBonusDrop() * 100 + "%.",
                            "&fCusto: &d " + bonusDrop.getPriceTokens() + " Tokens &f+ &a $ " + bonusDrop.getPriceCoins() + " coins&f.",
                            "", "&cMelhoria anterior necessaria.");
                } else {
                    itemButton.addLoreList("&fBônus drop atual: &3" + spawner.getBonusDrop() * 100 + "%.",
                            "&fCusto: &d " + bonusDrop.getPriceTokens() + " Tokens &f+ &a $ " + bonusDrop.getPriceCoins() + " coins&f.",
                            "", "&eYeah! você já fez essa melhoria.");
                }
            }
            upgradeDropBonus.setButton(10 + level, button);
        });


        upgradeDropBonus.show(player);
    }

    public void createUpgradeSpawnDelay(Player player, Spawner spawner) {

        upgradeDelay = new InventoryGUI(TextUtil.color("&8Aprimorar Tempo"), 9 * 3);
        upgradeDelay.setDefaultCancell(true);

        plugin.getUpgradesController().getStackDelays().forEach((level, stackDelay) -> {
            MakeItem itemButton = new MakeItem(347);
            itemButton.setName("&3Aprimorar tempo");
            itemButton.setAmount(level);
            ItemButton button = null;

            itemButton.addLoreList("",
                    "&7Com esta melhoria o intervalo de tempo de geração",
                    "&7de mobs da sua estrutura será reajustado para &e " + stackDelay.getQuantity() + "s",
                    "");
            if (spawner.getDelaysSpawnLevel() + 1 == level) {

                itemButton.addLoreList("&fFuncionamento atual: &e" + spawner.getDelaySpawn() + "&7s.",
                        "&fCusto: &d " + stackDelay.getPriceTokens() + " Tokens &f+ &a $ " + stackDelay.getPriceCoins() + " coins&f.",
                        "", "&aClique para prosseguir.");
                itemButton.addGlow();
                button = new ItemButton(itemButton.build());
                button.setDefaultAction(ClickType -> {
                    if (spawner.getOwner().equals(player.getName())) {
                        plugin.getUpgradesController().upgradeDelay(player, spawner, level);
                        createUpgradeSpawnDelay(player, spawner);
                    } else {
                        player.sendMessage("&cApenas o dono da estrutura pode fazer isso.");
                    }
                });

            } else {
                button = new ItemButton(itemButton.build());
                if (spawner.getDelaysSpawnLevel() + 1 < level) {
                    itemButton.addLoreList("&fFuncionamento atual: &e" + spawner.getDelaySpawn() + "&7s.",
                            "&fCusto: &d " + stackDelay.getPriceTokens() + " Tokens &f+ &a $ " + stackDelay.getPriceCoins() + " coins&f.",
                            "", "&cMelhoria anterior necessaria.");
                } else {
                    itemButton.addLoreList("&fFuncionamento atual: &e" + spawner.getDelaySpawn() + "&7s.",
                            "&fCusto: &d " + stackDelay.getPriceTokens() + " Tokens &f+ &a $ " + stackDelay.getPriceCoins() + " coins&f.",
                            "", "&eYeah! você já fez essa melhoria.");
                }
            }
            upgradeDelay.setButton(10 + level, button);
        });


        upgradeDelay.show(player);
    }

    public void createUpgradeLimitActivation(Player player, Spawner spawner) {

        upgradeLimit = new InventoryGUI(TextUtil.color("&8Melhoria Ativação"), 9 * 3);
        upgradeLimit.setDefaultCancell(true);

        plugin.getUpgradesController().getActivationLimits().forEach((level, activationLimit) -> {
            MakeItem itemButton = new MakeItem(390);
            itemButton.setName("&3Aprimorar drops");
            itemButton.setAmount(level);

            ItemButton button = null;

            itemButton.addLoreList("", "&7Com esta melhoria você poderá ativar nessa",
                    "&7estrutura " + activationLimit.getQuantity() + " &f mobs diferentes ao mesmo tempo!" +
                            "");
            if (spawner.getActivationLimitLevel() + 1 == level) {

                itemButton.addLoreList("&fLimite atual: &3" + spawner.getActivationLimitMobSpawner() + ".",
                        "&fCusto: &d " + activationLimit.getPriceTokens() + " Tokens &f+ &a $ " + activationLimit.getPriceCoins() + " coins&f.",
                        "", "&aClique para prosseguir.");
                itemButton.addGlow();
                button = new ItemButton(itemButton.build());
                button.setDefaultAction(ClickType -> {

                    if (spawner.getOwner().equals(player.getName())) {
                        plugin.getUpgradesController().upgradeLimitStack(player, spawner, level);
                        createUpgradeLimitActivation(player, spawner);
                    } else {
                        player.sendMessage("&cApenas o dono da estrutura pode fazer isso.");
                    }


                });

            } else {
                button = new ItemButton(itemButton.build());
                if (spawner.getActivationLimitLevel() + 1 < level) {
                    itemButton.addLoreList("&fLimite atual: &3" + spawner.getActivationLimitMobSpawner() + ".",
                            "&fCusto: &d " + activationLimit.getPriceTokens() + " Tokens &f+ &a $ " + activationLimit.getPriceCoins() + " coins&f.",
                            "", "&cMelhoria anterior necessaria.");
                } else {
                    itemButton.addLoreList("&fLimite atual: &3" + spawner.getActivationLimitMobSpawner() + ".",
                            "&fCusto: &d " + activationLimit.getPriceTokens() + " Tokens &f+ &a $ " + activationLimit.getPriceCoins() + " coins&f.",
                            "", "&eYeah! você já fez essa melhoria.");
                }
            }
            upgradeLimit.setButton(10 + level, button);
        });


        upgradeLimit.show(player);
    }

    public void updateOnOffStructure(Spawner spawner) {

        MakeItem deactive;

        if (spawner.isActivated()) {
            deactive = new MakeItem(351, (byte) 10).setName("&aEstrutura ativada!").addLoreList(
                    "&eClique para desativar.");
        } else {
            deactive = new MakeItem(351, (byte) 8).setName("&cEstrutura desativada!").addLoreList(
                    "&eClique para ativar.");
        }

        onOffStructureButton = new ItemButton(deactive.build()).setDefaultAction(ClickAction -> {
                    Player player = (Player) ClickAction.getWhoClicked();
                    if (player.getName().equals(spawner.getOwner())) {
                        if (spawner.isActivated()) {
                            spawner.setActivated(false);
                            updateOnOffStructure(spawner);
                            plugin.getSpawnerQueue().addItem(spawner);
                        } else {
                            spawner.setActivated(true);
                            updateOnOffStructure(spawner);
                            plugin.getSpawnerQueue().addItem(spawner);
                        }
                    } else {
                        player.sendMessage("&cOps, parace que você não pode ativar isso.");
                        player.closeInventory();
                    }
                }
        );
        structure.setButton(12, onOffStructureButton);
        updateStructure(structure);
    }

    private void updateStructure(InventoryGUI structure) {
        this.structure = structure;
    }

    public void openStructureMenu(Spawner spawner, Player player) {
        createStructureMenu(spawner, player);
    }

}
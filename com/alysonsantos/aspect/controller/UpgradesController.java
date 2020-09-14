package com.alysonsantos.aspect.controller;

import com.alysonsantos.aspect.AspectSpawners;
import com.alysonsantos.aspect.api.EconomyApi;
import com.alysonsantos.aspect.api.EconomyProvider;
import com.alysonsantos.aspect.models.Spawner;
import com.alysonsantos.aspect.models.upgrades.ActivationLimit;
import com.alysonsantos.aspect.models.upgrades.BonusDrop;
import com.alysonsantos.aspect.models.upgrades.StackDelay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class UpgradesController {

    private final EconomyApi economyApi = EconomyProvider.get();

    private final HashMap<Integer, BonusDrop> bonusDrops;
    private final HashMap<Integer, StackDelay> stackDelays;
    private final HashMap<Integer, ActivationLimit> activationLimits;

    public void upgradeDrop(Player player, Spawner spawner, int level) {
        if (spawner.getBonusDropLevel() < level) {
            final double balance = economyApi.getAccount(player).getBalance();
            final double tokens = economyApi.getAccount(player).getTokens();

            final BonusDrop bonusDrop = bonusDrops.get(level);

            if (balance >= bonusDrop.getPriceCoins() &&
                    tokens >= bonusDrop.getPriceTokens()) {

                economyApi.getAccount(player).removeBalance(bonusDrop.getPriceCoins());
                economyApi.getAccount(player).removeTokens(bonusDrop.getPriceTokens());

                spawner.setBonusDrop(bonusDrop.getBoost());
                spawner.setBonusDropLevel(level);
                spawner.addUpgradeCount();
                player.sendMessage("§aYeah! Upgrade feito com sucesso");

                val queue = AspectSpawners.getPlugin().getSpawnerQueue();
                queue.addItem(spawner);

            } else {
                player.sendMessage("§cSaldo insuficiente para fazer melhoria.");
            }


        } else {
            player.sendMessage("§cVocê atingiu o limite de upgrades do §eBonus Drop");
        }
    }

    public void upgradeDelay(Player player, Spawner spawner, int level) {
        if (spawner.getDelaysSpawnLevel() < level) {

            final double balance = economyApi.getAccount(player).getBalance();
            final double tokens = economyApi.getAccount(player).getTokens();

            final StackDelay stackDelay = stackDelays.get(level);

            if (balance >= stackDelay.getPriceCoins() &&
                    tokens >= stackDelay.getPriceTokens()) {

                economyApi.getAccount(player).removeBalance(stackDelay.getPriceCoins());
                economyApi.getAccount(player).removeTokens(stackDelay.getPriceTokens());

                spawner.setDelaysSpawnLevel(level);
                spawner.setDelaySpawn(stackDelay.getQuantity());
                spawner.setDelaySpawnCount(stackDelay.getQuantity());
                spawner.addUpgradeCount();

                player.sendMessage("§aYeah! Upgrade feito com sucesso");

                val queue = AspectSpawners.getPlugin().getSpawnerQueue();
                queue.addItem(spawner);

            } else {
                player.sendMessage("§cSaldo insuficiente para fazer melhoria.");
            }

        } else {
            player.sendMessage("§c Você atingiu o limite de upgrades do §eTempo de Spawn");
        }

    }

    public void upgradeLimitStack(Player player, Spawner spawner, int level) {
        if (spawner.getActivationLimitLevel() < level) {
            final double balance = economyApi.getAccount(player).getBalance();
            final double tokens = economyApi.getAccount(player).getTokens();

            final ActivationLimit activationLimit = activationLimits.get(level);

            if (balance >= activationLimit.getPriceCoins() &&
                    tokens >= activationLimit.getPriceTokens()) {

                economyApi.getAccount(player).removeBalance(activationLimit.getPriceCoins());
                economyApi.getAccount(player).removeTokens(activationLimit.getPriceTokens());

                spawner.setActivationLimitLevel(level);
                spawner.setActivationLimitMobSpawner(activationLimit.getQuantity());
                spawner.addUpgradeCount();

                val queue = AspectSpawners.getPlugin().getSpawnerQueue();
                queue.addItem(spawner);

                player.sendMessage("§aYeah! Upgrade feito com sucesso");

            } else {
                player.sendMessage("§cSaldo insuficiente para fazer melhoria.");
            }

        } else {
            player.sendMessage("§c Você atingiu o limite de upgrades do §eLimite de Ativação");
        }
    }
}

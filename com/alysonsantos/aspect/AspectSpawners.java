package com.alysonsantos.aspect;

import com.alysonsantos.aspect.commands.CommandSpawners;
import com.alysonsantos.aspect.controller.EntityController;
import com.alysonsantos.aspect.controller.SpawnerQueue;
import com.alysonsantos.aspect.controller.StorageController;
import com.alysonsantos.aspect.controller.UpgradesController;
import com.alysonsantos.aspect.database.Database;
import com.alysonsantos.aspect.database.MySQLDatabase;
import com.alysonsantos.aspect.listener.*;
import com.alysonsantos.aspect.menu.Menus;
import com.alysonsantos.aspect.models.upgrades.ActivationLimit;
import com.alysonsantos.aspect.models.upgrades.BonusDrop;
import com.alysonsantos.aspect.models.upgrades.StackDelay;
import com.alysonsantos.aspect.repository.SpawnersRepository;
import com.alysonsantos.aspect.storage.GsonStorageHandler;
import com.alysonsantos.aspect.util.command.CommandFrame;
import com.alysonsantos.aspect.utils.ChatValue;
import de.tr7zw.nbtinjector.NBTInjector;
import dev.arantes.inventorymenulib.listeners.InventoryListener;
import lombok.Getter;
import me.lucko.helper.internal.HelperImplementationPlugin;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

@HelperImplementationPlugin
@Getter
public class AspectSpawners extends ExtendedJavaPlugin {

    private final UpgradesController upgradesController;
    private final GsonStorageHandler gsonStorageHandler;
    private final StorageController storageController;
    private final SpawnersRepository repository;
    private final Database mySQLDatabase;
    private final SpawnerQueue spawnerQueue;

    private Menus menus;

    public AspectSpawners() {
        this.upgradesController = new UpgradesController(
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );
        this.storageController = new StorageController();
        this.mySQLDatabase = new MySQLDatabase(this);
        this.repository = new SpawnersRepository(mySQLDatabase.getJdbcProvider());
        this.gsonStorageHandler = new GsonStorageHandler(getDataFolder(), "user_spawners");
        this.spawnerQueue = new SpawnerQueue();
    }

    public static AspectSpawners getPlugin() {
        return getPlugin(AspectSpawners.class);
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        loadUpgrades();

        menus = new Menus(this);

        new InventoryListener(this);
        new EntityController(storageController).initController();

        registerListeners();
        registerCommands();

        gsonStorageHandler.initialize();
        spawnerQueue.setRemovalAction(spawner -> repository.update(spawner.getPosition(), spawner));

        NBTInjector.inject();
    }

    private void loadUpgrades() {

        final ConfigurationSection bonusDrop = getConfig().getConfigurationSection("bonusDrop");
        for (final String key : bonusDrop.getKeys(false)) {

            final BonusDrop build = BonusDrop.builder()
                    .level(Integer.parseInt(key))
                    .boost(getConfig().getDouble("bonusDrop." + key + ".boost"))
                    .priceCoins(getConfig().getDouble("bonusDrop." + key + ".priceCoins"))
                    .priceTokens(getConfig().getDouble("bonusDrop." + key + ".priceToken"))
                    .build();

            upgradesController.getBonusDrops().put(build.getLevel(), build);

        }

        final ConfigurationSection activationLimit = getConfig().getConfigurationSection("activationLimit");
        for (final String key : activationLimit.getKeys(false)) {

            final ActivationLimit build = ActivationLimit.builder()
                    .level(Integer.parseInt(key))
                    .quantity(getConfig().getInt("activationLimit." + key + ".quantity"))
                    .priceCoins(getConfig().getDouble("activationLimit." + key + ".priceCoins"))
                    .priceTokens(getConfig().getDouble("activationLimit." + key + ".priceToken"))
                    .build();

            upgradesController.getActivationLimits().put(build.getLevel(), build);

        }

        final ConfigurationSection stackDelay = getConfig().getConfigurationSection("stackDelay");
        for (final String key : stackDelay.getKeys(false)) {

            final StackDelay build = StackDelay.builder()
                    .level(Integer.parseInt(key))
                    .quantity(getConfig().getInt("stackDelay." + key + ".quantity"))
                    .priceCoins(getConfig().getDouble("stackDelay." + key + ".priceCoins"))
                    .priceTokens(getConfig().getDouble("stackDelay." + key + ".priceToken"))
                    .build();

            upgradesController.getStackDelays().put(build.getLevel(), build);
        }
    }

    public void registerCommands() {
        CommandFrame commandFrame = new CommandFrame(this);

        commandFrame.registerType(OfflinePlayer.class, Bukkit::getOfflinePlayer);
        commandFrame.setUsageMessage("");
        commandFrame.register(new CommandSpawners(this));
    }

    public void registerListeners() {
        registerListener(new ChatValue());
        registerListener(new EntityListener());
        registerListener(new BlockPlaceListener(this));
        registerListener(new BlockBreakListener(this));
        registerListener(new HologramListener(storageController));
        registerListener(new BaseListeners(this, storageController));
    }


    @Override
    protected void disable() {
        storageController.getSpawnerCache().asMap().clear();
        storageController.getUsersCache().asMap().clear();
        upgradesController.getStackDelays().clear();
        upgradesController.getActivationLimits().clear();
        upgradesController.getBonusDrops().clear();
    }
}

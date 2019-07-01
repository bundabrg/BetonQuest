/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.citizens;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Displays a hologram relative to an npc
 * <p>
 * Some care is taken to optimize how holograms are displayed. They are destroyed when not needed, shared between players and
 * we only have a fast update when needed to ensure they are relative to the NPC position
 */

public class CitizensHologram extends BukkitRunnable implements Listener {

    private static CitizensHologram instance;

    private int interval;
    // Hologram Config
    private List<HologramConfig> hologramConfigs = new ArrayList<>();
    private boolean enabled;
    // Updater
    private BukkitRunnable updater;

    public CitizensHologram() {
        instance = this;

        // Start this when all plugins loaded
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BetonQuest.getInstance().getJavaPlugin(), () -> {
            // Get default interval
            interval = Math.max(1, Config.getCustom().getConfig().getInt("npc_holograms.check_interval", 100));

            // loop across all packages
            for (ConfigPackage pack : Config.getPackages().values()) {
                // npc_holograms contains all holograms for NPCs
                ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("npc_holograms");
                if (section == null) {
                    section = Config.getCustom().getConfig().getConfigurationSection("npc_holograms");
                }

                if (section == null) {
                    continue;
                }

                // there's a setting to disable npc holograms altogether
                if ("true".equalsIgnoreCase(section.getString("disabled"))) {
                    continue;
                }

                // load the condition check interval
                if (section.contains("check_interval")) {
                    int packageInterval = section.getInt("check_interval");
                    if (packageInterval < interval && packageInterval > 0) {
                        interval = packageInterval;
                    }
                }

                // loading hologram config
                for (String key : section.getKeys(false)) {
                    ConfigurationSection settings = section.getConfigurationSection(key);

                    // if the key is not a configuration section then it's not a hologram
                    if (settings == null) {
                        continue;
                    }

                    HologramConfig hologramConfig = new HologramConfig();
                    hologramConfig.settings = settings;

                    // load all conditions
                    hologramConfig.conditions = new ArrayList<>();
                    String rawConditions = settings.getString("conditions");
                    if (rawConditions != null) {
                        for (String part : rawConditions.split(",")) {
                            try {
                                hologramConfig.conditions.add(new ConditionID(pack, part));
                            } catch (ObjectNotFoundException e) {
                                Debug.error("Error while loading " + part + " condition for hologram " + pack.getName() + "."
                                        + key + ": " + e.getMessage());
                            }
                        }
                    }

                    try {
                        String[] vectorParts = Objects.requireNonNull(settings.getString("vector", "0;3;0")).split(";");
                        hologramConfig.vector = new Vector(
                                Double.parseDouble(vectorParts[0]),
                                Double.parseDouble(vectorParts[1]),
                                Double.parseDouble(vectorParts[2])
                        );
                    } catch (NumberFormatException | NullPointerException e) {
                        Debug.error(pack.getName() + ": Invalid vector: " + settings.getString("vector"));
                        continue;
                    }

                    // load all NPCs for which this effect can be displayed
                    hologramConfig.npcs = new HashMap<>();
                    if (settings.contains("npcs")) {
                        for (int npcId : settings.getIntegerList("npcs")) {
                            hologramConfig.npcs.put(npcId, null);
                        }
                    } else {
                        // No npcs listed so add all npcs in package
                        for (String npcId : pack.getMain().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                            try {
                                hologramConfig.npcs.put(Integer.valueOf(npcId), null);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                    hologramConfigs.add(hologramConfig);
                }
            }

            Bukkit.getPluginManager().registerEvents(instance, BetonQuest.getInstance().getJavaPlugin());

            runTaskTimer(BetonQuest.getInstance().getJavaPlugin(), 4, interval);
            enabled = true;
        }, 3);
    }

    // Reloads the particle effect
    public static void reload() {
        if (instance != null) {
            if (instance.enabled) {
                instance.cleanUp();

                instance.cancel();
            }
            new CitizensHologram();
        }
    }

    private void cleanUp() {
        // Cancel Updater
        if (updater != null) {
            updater.cancel();
            updater = null;
        }

        // Delete all holograms
        for (HologramConfig hologramConfig : hologramConfigs) {
            for (int npcId : hologramConfig.npcs.keySet()) {
                Hologram hologram = hologramConfig.npcs.getOrDefault(npcId, null);
                if (hologram != null) {
                    hologramConfig.npcs.get(npcId).delete();
                    hologramConfig.npcs.put(npcId, null);
                }
            }
        }
    }

    @Override
    public void run() {
        updateHolograms();
    }

    /**
     * Update hologram visibility
     * <p>
     * This will ensure that holograms that are visible to at least 1 player exist and clean up holograms that have
     * no visibility.
     */
    private void updateHolograms() {
        // If we need to update hologram positions
        boolean npcUpdater = false;

        // Handle updating each NPC
        for (HologramConfig hologramConfig : hologramConfigs) {
            boolean isVisibleToAnyone = false;
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean visible = true;

                // Check that ALL conditions pass for the player to have a hologram
                for (ConditionID condition : hologramConfig.conditions) {
                    if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                        visible = false;
                        break;
                    }
                }

                // Check all affected NPC's
                for (int npcId : hologramConfig.npcs.keySet()) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);

                    if (npc == null) {
                        continue;
                    }

                    if (visible) {
                        isVisibleToAnyone = true;
                        npcUpdater = true;
                        // If no hologram yet we create one
                        if (hologramConfig.npcs.get(npcId) == null) {
                            // Create a new hologram
                            Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance().getJavaPlugin(),
                                    npc.getEntity().getLocation().add(hologramConfig.vector));

                            hologram.getVisibilityManager().setVisibleByDefault(true);
                            for (String line : hologramConfig.settings.getStringList("lines")) {
                                if (line.startsWith("item:")) {
                                    Material material = Material.matchMaterial(line.substring(5));
                                    if (material != null) {
                                        hologram.appendItemLine(new ItemStack(material));
                                    }
                                } else {
                                    hologram.appendTextLine(line.replace('&', '§'));
                                }
                            }
                            hologramConfig.npcs.put(npcId, hologram);
                        }

                        // Set visible to player next tick
                        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BetonQuest.getInstance().getJavaPlugin(), () -> {
                            Hologram hologram = hologramConfig.npcs.getOrDefault(npcId, null);
                            if (hologram != null) {
                                hologram.getVisibilityManager().showTo(player);
                            }
                        }, 10);
                    } else {
                        Hologram hologram = hologramConfig.npcs.getOrDefault(npcId, null);

                        if (hologram != null) {
                            hologram.getVisibilityManager().hideTo(player);
                        }
                    }
                }
            }

            // If not visible to anyone we can safely remove all holograms referencing this config
            if (!isVisibleToAnyone) {

                for (int npcId : hologramConfig.npcs.keySet()) {
                    Hologram hologram = hologramConfig.npcs.getOrDefault(npcId, null);
                    if (hologram != null) {
                        hologramConfig.npcs.get(npcId).delete();
                        hologramConfig.npcs.put(npcId, null);
                    }
                }
            }
        }

        if (npcUpdater) {
            if (updater == null) {
                // The updater only runs when at least one hologram is visible to ensure it stays relative to npc location
                updater = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (HologramConfig hologramConfig : hologramConfigs) {
                            for (int npcId : hologramConfig.npcs.keySet()) {
                                Hologram hologram = hologramConfig.npcs.getOrDefault(npcId, null);
                                if (hologram != null) {
                                    NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
                                    hologram.teleport(npc.getEntity().getLocation().add(hologramConfig.vector));
                                }
                            }
                        }
                    }
                };
                updater.runTaskTimer(BetonQuest.getInstance().getJavaPlugin(), 1L, 1L);
            }
        } else {
            if (updater != null) {
                updater.cancel();
                updater = null;
            }
        }
    }

    private class HologramConfig {
        List<ConditionID> conditions;
        ConfigurationSection settings;
        Vector vector;
        Map<Integer, Hologram> npcs = new HashMap<>();
    }

}

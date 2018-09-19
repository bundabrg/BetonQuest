/**
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
package pl.betoncraft.betonquest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.version.MultiVersionLoader;
import pl.betoncraft.betonquest.version.VersionPluginInterface;

import java.io.File;

public final class BetonQuestPlugin extends JavaPlugin {
    private static BetonQuestPlugin instance;

    private final static String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
    private VersionPluginInterface version;

    /**
     * Return Current BetonQuestPlugin instance
     */
    public static BetonQuestPlugin getInstance() {
        return instance;
    }


    /**
     * Return the plugin for the server version
     */
    public VersionPluginInterface getVersion() {
        if (version == null) {
            try {
                switch (serverVersion) {
                    case "1_8_R1":
                    case "1_8_R2":
                    case "1_8_R3":
                        System.err.println("[BetonQuest]: Loading Version 1.8-R3 (Detected: " + serverVersion + ")");
                        MultiVersionLoader loader = new MultiVersionLoader(
                                this.getClass().getClassLoader(),
                                "pl.betoncraft.betonquest",
                                new String[]{
                                        "v1_8_R3"
                                });
                        version = (VersionPluginInterface) loader.loadClass("pl.betoncraft.betonquest.v1_8_R3.BetonQuest").newInstance();
                        break;
                    // By default, return the Core which represents the latest version
                    default:
                        System.err.println("[BetonQuest]: Loading Latest Version (Detected: " + serverVersion + ")");
                        version = (VersionPluginInterface) Class.forName("pl.betoncraft.betonquest.BetonQuest")
                                .newInstance();

                }
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        return version;
    }

    public BetonQuestPlugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getVersion().onEnable();
    }

    @Override
    public void onDisable() {
        getVersion().onDisable();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

}

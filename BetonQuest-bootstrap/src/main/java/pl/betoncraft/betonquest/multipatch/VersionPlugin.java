package pl.betoncraft.betonquest.multipatch;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public abstract class VersionPlugin {


    static public MultiVersionPlugin getPlugin() {
        return MultiVersionPlugin.getInstance();
    }

    // Abstract Methods
    public abstract void onEnable();
    public abstract void onDisable();

    // Proxy Methods

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    public void saveConfig() {
        getPlugin().saveConfig();
    }

    protected File getFile() {
        return getPlugin().getFile();
    }

    public File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    public InputStream getResource(String filename) {
        return getPlugin().getResource(filename);
    }

    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    public PluginDescriptionFile getDescription() {
        return getPlugin().getDescription();
    }

    public void saveDefaultConfig() {
        getPlugin().saveDefaultConfig();
    }

    public void reloadConfig() {
        getPlugin().reloadConfig();
    }

    public PluginCommand getCommand(String name) {
        return getPlugin().getCommand(name);
    }
}

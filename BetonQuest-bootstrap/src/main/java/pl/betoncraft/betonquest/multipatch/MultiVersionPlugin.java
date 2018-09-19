package pl.betoncraft.betonquest.multipatch;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public abstract class MultiVersionPlugin extends JavaPlugin {
    private static MultiVersionPlugin instance;

    private final static String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

    // Base package to version
    private String base;

    // List of versions available, in ascending order
    private String[] versions;

    // Name of plugin to load
    private String pluginName;

    // Plugin Instance
    private VersionPlugin plugin;

    // Classloader
    private ClassLoader loader;

    /**
     * Return Current Plugin instance
     */
    public static MultiVersionPlugin getInstance() {
        return instance;
    }

    /**
     * Constructor
     */
    public MultiVersionPlugin(String base, String pluginName, String[] versions) {
        instance = this;
        this.base = base;
        this.pluginName = pluginName;
        this.versions = versions;

        // Configure ClassLoader
        loader = this.getClass().getClassLoader();
        for (int i=0; i<versions.length; i++) {
            if (serverVersion.equals(versions[i])) {
                loader = new MultiVersionLoader(
                        loader,
                        base,
                        Arrays.copyOfRange(versions, i, versions.length)
                );
                break;
            }
        }

        // Load Plugin
        try {
            plugin = (VersionPlugin) loader.loadClass(String.join(".", base, pluginName)).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public VersionPlugin getPlugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        getPlugin().onEnable();
    }

    @Override
    public void onDisable() {
        getPlugin().onDisable();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

}

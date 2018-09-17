package pl.betoncraft.betonquest.version;

import org.bukkit.Bukkit;

public class VersionMatcher {
    /**
     * The server's version
     */
    private final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

    /**
     * Matches the server version to it's {@link VersionWrapper}
     * @return The {@link VersionWrapper} for this server
     * @throws RuntimeException If we don't support this server version
     */
    public VersionWrapper match() {
        try {
            switch(serverVersion) {
                // List versions we know don't work (so far)
                case "1_9_R1":
                case "1_9_R2":
                case "1_10_R1":
                case "1_11_R1":
                    throw new RuntimeException("Your server version isn't supported! (" + serverVersion + ")");

                // List Supported versions and what wrapper to use
                case "1_12_R1":
                    return (VersionWrapper) Class.forName("pl.betoncraft.betonquest.version.Wrapper1_12_R1").newInstance();

                case "1_13_R1":
                case "1_13_R2":
                    return (VersionWrapper) Class.forName("pl.betoncraft.betonquest.version.Wrapper1_13_R1").newInstance();

                // Fallback to latest version with warning in case we don't know. That way newer versions should hopefully still work unofficially.
                default:
                    System.err.println("[BetonQuest]: Unknown Server Version (" + serverVersion + ") so assuming latest will work.");
                    return (VersionWrapper) Class.forName("pl.betoncraft.betonquest.version.Wrapper1_13_R1").newInstance();
            }

        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}

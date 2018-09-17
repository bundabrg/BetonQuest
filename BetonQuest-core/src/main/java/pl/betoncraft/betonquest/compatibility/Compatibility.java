package pl.betoncraft.betonquest.compatibility;

import java.util.List;

public abstract class Compatibility {
    private static Compatibility instance;

    public static Compatibility create(Class<? extends Compatibility> clazz) {
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static Compatibility getInstance() {
        return instance;
    }

    public abstract List<String> getHooked();
    public abstract void reload();
    public abstract void disable();

}

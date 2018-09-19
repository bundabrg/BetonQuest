package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.multipatch.MultiVersionPlugin;

public class BetonQuestPlugin extends MultiVersionPlugin {

    public BetonQuestPlugin() {
        super("pl.betoncraft.betonquest",
                "BetonQuest",
                new String[] {
                        "1_8_R3",
                        "1_9_R2",
                        "1_10_R1",
                        "1_11_R1",
                        "1_12_R1"
                });
    }

}

package pl.betoncraft.betonquest.version;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.v1_13_R1.compatibility.LocalCompatibility;


public class Wrapper1_13_R1 extends VersionWrapper {

    public Class<? extends Condition> getArmorRatingCondition() {
        return pl.betoncraft.betonquest.v1_13_R1.conditions.ArmorRatingCondition.class;
    }

    public Class<? extends Objective> getStepObjective() {
        return pl.betoncraft.betonquest.v1_13_R1.objectives.StepObjective.class;
    }

    public Class<? extends ConversationIO> getInventoryConvIO() {
        return pl.betoncraft.betonquest.v1_13_R1.conversation.InventoryConvIO.class;
    }

    public Class<? extends ConversationIO> getInventoryConvIO_Combined() {
        return pl.betoncraft.betonquest.v1_13_R1.conversation.InventoryConvIO.Combined.class;
    }

    public Class<? extends Compatibility> getCompatibility() {
        return LocalCompatibility.class;
    }

    public Class<? extends QuestEvent> getLeverEvent() {
        return pl.betoncraft.betonquest.v1_13_R1.events.LeverEvent.class;
    }

    public Class<? extends QuestEvent> getSetBlockEvent() {
        return pl.betoncraft.betonquest.v1_13_R1.events.SetBlockEvent.class;
    }

    public Class<?> getCubeNPCListener() {
        return pl.betoncraft.betonquest.v1_13_R1.conversation.CubeNPCListener.class;
    }

}

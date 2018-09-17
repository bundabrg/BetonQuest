package pl.betoncraft.betonquest.version;

import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.compatibility.Compatibility;

/**
 * Wraps versions to be able to easily use different NMS server versions
 * @author Wesley Smith
 * @since 1.0
 */
public abstract class VersionWrapper {

    // Classes

    public abstract Class<? extends Condition> getArmorRatingCondition();
    public abstract Class<? extends Objective> getStepObjective();
    public abstract Class<? extends ConversationIO> getInventoryConvIO();
    public abstract Class<? extends ConversationIO> getInventoryConvIO_Combined();

    public abstract Class<? extends Compatibility> getCompatibility();
    public abstract Class<? extends QuestEvent> getLeverEvent();
    public abstract Class<? extends QuestEvent> getSetBlockEvent();

    public abstract Class<?> getCubeNPCListener();
}
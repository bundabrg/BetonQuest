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
package pl.betoncraft.betonquest.compatibility.worldedit;


import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Pastes a schematic at a given location.
 *
 * @author Jakub Sapalski
 */
public class PasteSchematicEvent extends QuestEvent {

    private File file;
    private LocationData loc;
    private boolean noAir;

    public PasteSchematicEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        loc = instruction.getLocation();
        File folder = new File(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder(), "schematics");
        if (!folder.exists() || !folder.isDirectory()) {
            throw new InstructionParseException("Schematic folder does not exist");
        }
        String schemName = instruction.next();
        file = new File(folder, schemName + ".schem");
        if (!file.exists()) {
            file = new File(folder, schemName + ".schematic");
        } else if (!file.exists()) {
            file = new File(folder, schemName + ".nbt");
        } else if (!file.exists()) {
            file = new File(folder, schemName + ".png");
        } else if (!file.exists()) {
            throw new InstructionParseException("Schematic " + schemName + " does not exist (" + folder.toPath().resolve(schemName + ".schem/.schematic/.nbt/.png") + ")");
        }
        noAir = instruction.hasArgument("noair");
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        try {
            Location location = loc.getLocation(playerID);
            ClipboardFormat format = ClipboardFormat.findByFile(file);
            if (format == null) {
                throw new IOException("Unknown Schematic Format");
            }

            format.load(file).paste(BukkitAdapter.adapt(location.getWorld()), BukkitAdapter.adapt(location).toVector(), false, !noAir, null);

        } catch (IOException | WorldEditException e) {
            Debug.error("Error while pasting a schematic: " + e.getMessage());
        }
    }

}

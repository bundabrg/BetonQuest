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
package pl.betoncraft.betonquest.v1_13_R1.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class PlaceholderVariable extends Variable {
	
	private String placeholder;

	public PlaceholderVariable(Instruction instruction) throws InstructionParseException {
		super(instruction);
		placeholder = instruction.next();
	}

	@Override
	public String getValue(String playerID) {
		return PlaceholderAPI.setPlaceholders(PlayerConverter.getPlayer(playerID), '%' + placeholder + '%');
	}

}

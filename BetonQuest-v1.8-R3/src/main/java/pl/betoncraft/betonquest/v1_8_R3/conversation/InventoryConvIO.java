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
package pl.betoncraft.betonquest.v1_8_R3.conversation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.ConversationColors;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.v1_8_R3.BetonQuest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Inventory GUI for conversations
 * 
 * @author Jakub Sapalski
 */
public class InventoryConvIO extends pl.betoncraft.betonquest.conversation.InventoryConvIO implements Listener, ConversationIO {

	public InventoryConvIO(Conversation conv, String playerID) {
		super(conv, playerID);
	}

	@SuppressWarnings("deprecation")
    @Override
	public void display() {
		// prevent displaying anything if the player closed the conversation
		// in the meantime
		if (conv.isEnded()) {
			return;
		}
		if (response == null) {
			end();
			player.closeInventory();
			return;
		}
		if (options.isEmpty()) {
			end();
		}
		// each row contains 7 options, so get amount of rows
		int rows = (int) Math.floor(options.size() / 7);
		rows++;
		// this itemstack represents slots in the inventory
		inv = Bukkit.createInventory(null, 9 * rows, "NPC");
		inv.setContents(new ItemStack[9 * rows]);
		ItemStack[] buttons = new ItemStack[9 * rows];
		// set the NPC head
		ItemStack npc = new ItemStack(Material.SKULL);
		npc.setDurability((short) 3);
		SkullMeta npcMeta = (SkullMeta) npc.getItemMeta();
		npcMeta.setOwner(npcName);
		npcMeta.setDisplayName(npcNameColor + npcName);
		npcMeta.setLore(stringToLines(response, npcTextColor, null));
		npc.setItemMeta(npcMeta);
		buttons[0] = npc;
		// this is the number of an option
		int next = 0;
		// now fill the slots
		for (int j = 0; j < 9 * rows; j++) {
			// skip first and second slots of each row
			if (j % 9 == 0 || j % 9 == 1)
				continue;
			// count option numbers, starting with 1
			next++;
			// break if all options are set
			String option = options.get(next);
			if (option == null) {
				break;
			}
			// generate an itemstack for this option
			Material material = Material.ENDER_PEARL;
			short data = 0;
			// get the custom material
			if (option.matches("^\\{[a-zA-Z0-9_: ]+\\}(?s:.*)$")) {
				String fullMaterial = option.substring(1, option.indexOf('}'));
				String materialName = fullMaterial;
				if (materialName.contains(":")) {
					int colonIndex = materialName.indexOf(':');
					try {
						data = Short.valueOf(materialName.substring(colonIndex + 1));
					} catch (NumberFormatException e) {
						data = 0;
					}
					materialName = materialName.substring(0, colonIndex);
				}
				Material m = Material.matchMaterial(materialName);
				option = option.replace("{" + fullMaterial + "}", "");
				if (m == null) {
					material = Material.ENDER_PEARL;
				} else {
					material = m;
				}
			}
			// remove custom material prefix from the option
			options.put(next, option);
			// set the display name and lore of the option
			ItemStack item = new ItemStack(material);
			item.setDurability(data);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(numberFormat.replace("%number%", Integer.toString(next)));
			ArrayList<String> lines = stringToLines(response, npcTextColor,
					npcNameColor + npcName + ChatColor.RESET + ": ");
			StringBuilder string = new StringBuilder();
			for (ChatColor color : ConversationColors.getColors().get("number")) {
				string.append(color);
			}
			lines.addAll(stringToLines(option, optionColor, string.toString() + "- "));
			meta.setLore(lines);
			item.setItemMeta(meta);
			buttons[j] = item;
		}
		if (printMessages) player.sendMessage(npcNameColor + npcName + ChatColor.RESET + ": " + npcTextColor + response);
		inv.setContents(buttons);
		new BukkitRunnable() {
			@Override
			public void run() {
				switching = true;
				player.openInventory(inv);
				switching = false;
			}
		}.runTask(BetonQuest.getPlugin());
	}
}

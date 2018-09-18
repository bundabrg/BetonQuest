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
package pl.betoncraft.betonquest.v1_8_R3.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.commands.SimpleTabCompleter;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigAccessor;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.v1_8_R3.item.QuestItem;


/**
 * Main admin command for quest editing.
 *
 * @author Jakub Sapalski
 */
public class QuestCommand extends pl.betoncraft.betonquest.commands.QuestCommand implements CommandExecutor, SimpleTabCompleter {


	/**
	 * Adds item held in hand to items.yml file
	 */
	@SuppressWarnings("deprecation")
    private void handleItems(CommandSender sender, String[] args) {
		// sender must be a player
		if (!(sender instanceof Player)) {
			Debug.info("Cannot continue, sender must be player");
			return;
		}
		// and the item name must be specified
		if (args.length < 2) {
			Debug.info("Cannot continue, item's name must be supplied");
			sendMessage(sender, "specify_item");
			return;
		}
		String itemID = args[1];
		String pack;
		String name;
		if (itemID.contains(".")) {
			String[] parts = itemID.split("\\.");
			pack = parts[0];
			name = parts[1];
		} else {
			pack = defaultPack;
			name = itemID;
		}
		Player player = (Player) sender;
		ItemStack item = null;
		item = player.getItemInHand();

		// if item is air then there is nothing to add to items.yml
		if (item == null || item.getType() == Material.AIR) {
			Debug.info("Cannot continue, item must not be air");
			sendMessage(sender, "no_item");
			return;
		}
		// define parts of the final string
		ConfigPackage configPack = Config.getPackages().get(pack);
		if (configPack == null) {
			Debug.info("Cannot continue, package does not exist");
			sendMessage(sender, "specify_package");
			return;
		}
		ConfigAccessor config = configPack.getItems();
		String instructions = QuestItem.itemToString(item);
		// save it in items.yml
		Debug.info("Saving item to configuration as " + args[1]);
		config.getConfig().set(name, instructions.trim());
		config.saveConfig();
		// done
		sendMessage(sender, "item_created", new String[] { args[1] });

	}
}

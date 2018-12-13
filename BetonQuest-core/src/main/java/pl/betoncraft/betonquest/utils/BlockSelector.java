/*
 *  BetonQuest - advanced quests for Bukkit
 *  Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A method of selectinig blocks using a quasi similar method as Majong. 1.13+ version.
 * <p>
 * Block selector format:
 * prefix:type[state=value,...]
 * <p>
 * Where:
 * - prefix - (optional) Prefix of type. If missing will assume to be minecraft
 * - type - Type of block. IE: stone
 * - state flags - (optional) Comma separate list of states contained in brackets
 * <p>
 * Type can contain the wildcard character '*'. For example:
 * - *_LOG - Match everything ending with _LOG
 * - * - Match everything
 * <p>
 * State flags:
 * - If a state flag is missing, then it will not be compared against. If it is set then that state
 * must be set.
 * <p>
 * Example:
 * - redstone_wird[power=5] - Tests for all redstone_wire of power 5, irregardless of direction
 */
public class BlockSelector {

    private String prefix;
    private String string;
    private Map<String, String> states;
    private Pattern typePattern;

    public BlockSelector(String string) {
        this.string = string;
        if (string.contains(":")) {
            prefix = string.substring(0, string.indexOf(":")).toLowerCase().trim();
            string = string.substring(string.indexOf(":") + 1);
        } else {
            prefix = "minecraft";
        }

        String type;
        if (string.contains("[")) {
            type = string.substring(0, string.indexOf("[")).toLowerCase();

            string = string.substring(string.indexOf("[") + 1);

            if (string.contains("]")) {
                states = new HashMap<>();
                for (String raw : string.substring(0, string.indexOf("]")).split(",")) {
                    if (raw.contains("=")) {
                        String[] kv = raw.split("=", 2);

                        states.put(kv[0].toLowerCase().trim(), kv[1].toLowerCase().trim());
                    }
                }
            }
        } else {
            type = string.toLowerCase().trim();
        }

        // Create typePattern from type. We replace '*' with a non-greedy regex match. We also strip
        // out invalid characters just in case.
        type = type.replaceAll("[^a-z0-9_*?]", "");

        typePattern = Pattern.compile("^" + type.replace("*", ".*?").replace("?", ".") + "$");
    }

    public String toString() {
        return string;
    }

    /**
     * Return true if the selector matches at least one valid block
     */
    public boolean isValid() {
        for (Material m : Material.values()) {
            if (match(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if material matches our selector. State is ignored
     */
    public boolean match(Material material) {
        NamespacedKey materialKey;
        try {
            materialKey = material.getKey();
        } catch (IllegalArgumentException e) {
            return false;
        }

        // Starts with our prefix?
        if (!materialKey.getNamespace().equalsIgnoreCase(prefix)) {
            return false;
        }

        Matcher matcher = typePattern.matcher(materialKey.getKey());

        return matcher.find();
    }

    /**
     * Return true if block matches our selector
     *
     * @param block Block to test
     * @return boolean True if a match occurred
     */
    public boolean match(Block block) {
        String blockString = block.getBlockData().getAsString().toLowerCase();

        // Starts with our prefix?
        if (!blockString.startsWith(prefix + ":")) {
            return false;
        }

        String blockType;
        Map<String, String> blockStates = null;
        if (blockString.contains("[")) {
            blockType = blockString.substring(blockString.indexOf(":") + 1, blockString.indexOf("["));
            blockStates = new HashMap<>();
            for (String raw : blockString.substring(blockString.indexOf("[") + 1, blockString.indexOf("]")).split(",")) {
                String[] kv = raw.split("=", 2);
                blockStates.put(kv[0].trim(), kv[1].trim());
            }
        } else {
            blockType = blockString.substring(blockString.indexOf(":") + 1);
        }

        Matcher matcher = typePattern.matcher(blockType);

        if (!matcher.find()) {
            return false;
        }

        if (states != null) {
            if (blockStates == null) {
                return false;
            }

            for (String state : states.keySet()) {
                if (!blockStates.containsKey(state)) {
                    return false;
                }

                if (!blockStates.get(state).equals(states.get(state))) {
                    return false;
                }
            }
        }

        return true;
    }

}

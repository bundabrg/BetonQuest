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
package pl.betoncraft.betonquest.v1_8_R3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.AnswerFilter;
import pl.betoncraft.betonquest.CustomDropListener;
import pl.betoncraft.betonquest.GlobalObjectives;
import pl.betoncraft.betonquest.JoinQuitListener;
import pl.betoncraft.betonquest.MobKillListener;
import pl.betoncraft.betonquest.StaticEvents;
import pl.betoncraft.betonquest.commands.BackpackCommand;
import pl.betoncraft.betonquest.commands.CancelQuestCommand;
import pl.betoncraft.betonquest.commands.CompassCommand;
import pl.betoncraft.betonquest.commands.JournalCommand;
import pl.betoncraft.betonquest.commands.LangCommand;
import pl.betoncraft.betonquest.conditions.AchievementCondition;
import pl.betoncraft.betonquest.conditions.AlternativeCondition;
import pl.betoncraft.betonquest.conditions.ArmorCondition;
import pl.betoncraft.betonquest.conditions.BiomeCondition;
import pl.betoncraft.betonquest.conditions.CheckCondition;
import pl.betoncraft.betonquest.conditions.ChestItemCondition;
import pl.betoncraft.betonquest.conditions.ConjunctionCondition;
import pl.betoncraft.betonquest.conditions.DayOfWeekCondition;
import pl.betoncraft.betonquest.conditions.EffectCondition;
import pl.betoncraft.betonquest.conditions.ExperienceCondition;
import pl.betoncraft.betonquest.conditions.GameModeCondition;
import pl.betoncraft.betonquest.conditions.GlobalPointCondition;
import pl.betoncraft.betonquest.conditions.GlobalTagCondition;
import pl.betoncraft.betonquest.conditions.HealthCondition;
import pl.betoncraft.betonquest.conditions.HeightCondition;
import pl.betoncraft.betonquest.conditions.ItemCondition;
import pl.betoncraft.betonquest.conditions.JournalCondition;
import pl.betoncraft.betonquest.conditions.LocationCondition;
import pl.betoncraft.betonquest.conditions.MonstersCondition;
import pl.betoncraft.betonquest.conditions.ObjectiveCondition;
import pl.betoncraft.betonquest.conditions.PartialDateCondition;
import pl.betoncraft.betonquest.conditions.PartyCondition;
import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.conditions.PointCondition;
import pl.betoncraft.betonquest.conditions.RandomCondition;
import pl.betoncraft.betonquest.conditions.RealTimeCondition;
import pl.betoncraft.betonquest.conditions.ScoreboardCondition;
import pl.betoncraft.betonquest.conditions.SneakCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.conditions.TestForBlockCondition;
import pl.betoncraft.betonquest.conditions.TimeCondition;
import pl.betoncraft.betonquest.conditions.VariableCondition;
import pl.betoncraft.betonquest.conditions.VehicleCondition;
import pl.betoncraft.betonquest.conditions.WeatherCondition;
import pl.betoncraft.betonquest.conditions.WorldCondition;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.conversation.ConversationColors;
import pl.betoncraft.betonquest.conversation.ConversationResumer;
import pl.betoncraft.betonquest.conversation.SimpleConvIO;
import pl.betoncraft.betonquest.conversation.TellrawConvIO;
import pl.betoncraft.betonquest.database.GlobalData;
import pl.betoncraft.betonquest.database.MySQL;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.SQLite;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.events.CancelEvent;
import pl.betoncraft.betonquest.events.ChestClearEvent;
import pl.betoncraft.betonquest.events.ChestGiveEvent;
import pl.betoncraft.betonquest.events.ChestTakeEvent;
import pl.betoncraft.betonquest.events.ClearEvent;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.CompassEvent;
import pl.betoncraft.betonquest.events.ConversationEvent;
import pl.betoncraft.betonquest.events.DamageEvent;
import pl.betoncraft.betonquest.events.DoorEvent;
import pl.betoncraft.betonquest.events.EXPEvent;
import pl.betoncraft.betonquest.events.EffectEvent;
import pl.betoncraft.betonquest.events.ExplosionEvent;
import pl.betoncraft.betonquest.events.FolderEvent;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.events.GiveJournalEvent;
import pl.betoncraft.betonquest.events.GlobalPointEvent;
import pl.betoncraft.betonquest.events.GlobalTagEvent;
import pl.betoncraft.betonquest.events.IfElseEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.KillEvent;
import pl.betoncraft.betonquest.events.KillMobEvent;
import pl.betoncraft.betonquest.events.LanguageEvent;
import pl.betoncraft.betonquest.events.LeverEvent;
import pl.betoncraft.betonquest.events.LightningEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.OpSudoEvent;
import pl.betoncraft.betonquest.events.PartyEvent;
import pl.betoncraft.betonquest.events.PickRandomEvent;
import pl.betoncraft.betonquest.events.PointEvent;
import pl.betoncraft.betonquest.events.RunEvent;
import pl.betoncraft.betonquest.events.ScoreboardEvent;
import pl.betoncraft.betonquest.events.SetBlockEvent;
import pl.betoncraft.betonquest.events.SudoEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.events.TeleportEvent;
import pl.betoncraft.betonquest.events.TimeEvent;
import pl.betoncraft.betonquest.events.TitleEvent;
import pl.betoncraft.betonquest.events.VariableEvent;
import pl.betoncraft.betonquest.events.WeatherEvent;
import pl.betoncraft.betonquest.objectives.ArrowShootObjective;
import pl.betoncraft.betonquest.objectives.BlockObjective;
import pl.betoncraft.betonquest.objectives.ChestPutObjective;
import pl.betoncraft.betonquest.objectives.ConsumeObjective;
import pl.betoncraft.betonquest.objectives.CraftingObjective;
import pl.betoncraft.betonquest.objectives.DelayObjective;
import pl.betoncraft.betonquest.objectives.EnchantObjective;
import pl.betoncraft.betonquest.objectives.EntityInteractObjective;
import pl.betoncraft.betonquest.objectives.ExperienceObjective;
import pl.betoncraft.betonquest.objectives.FishObjective;
import pl.betoncraft.betonquest.objectives.KillPlayerObjective;
import pl.betoncraft.betonquest.objectives.LocationObjective;
import pl.betoncraft.betonquest.objectives.LogoutObjective;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.objectives.PasswordObjective;
import pl.betoncraft.betonquest.objectives.PotionObjective;
import pl.betoncraft.betonquest.objectives.RespawnObjective;
import pl.betoncraft.betonquest.objectives.ShearObjective;
import pl.betoncraft.betonquest.objectives.SmeltingObjective;
import pl.betoncraft.betonquest.objectives.TameObjective;
import pl.betoncraft.betonquest.objectives.VariableObjective;
import pl.betoncraft.betonquest.objectives.VehicleObjective;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Updater;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.v1_8_R3.commands.QuestCommand;
import pl.betoncraft.betonquest.v1_8_R3.compatibility.Compatibility;
import pl.betoncraft.betonquest.v1_8_R3.conditions.ArmorRatingCondition;
import pl.betoncraft.betonquest.v1_8_R3.conditions.EmptySlotsCondition;
import pl.betoncraft.betonquest.v1_8_R3.conditions.HandCondition;
import pl.betoncraft.betonquest.v1_8_R3.config.Config;
import pl.betoncraft.betonquest.v1_8_R3.config.ConfigUpdater;
import pl.betoncraft.betonquest.v1_8_R3.conversation.CubeNPCListener;
import pl.betoncraft.betonquest.v1_8_R3.conversation.InventoryConvIO;
import pl.betoncraft.betonquest.v1_8_R3.events.PlaysoundEvent;
import pl.betoncraft.betonquest.v1_8_R3.events.SpawnMobEvent;
import pl.betoncraft.betonquest.v1_8_R3.item.QuestItemHandler;
import pl.betoncraft.betonquest.v1_8_R3.objectives.ActionObjective;
import pl.betoncraft.betonquest.v1_8_R3.objectives.DieObjective;
import pl.betoncraft.betonquest.v1_8_R3.objectives.StepObjective;
import pl.betoncraft.betonquest.v1_8_R3.variables.GlobalPointVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.ItemAmountVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.LocationVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.MathVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.NpcNameVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.ObjectivePropertyVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.PlayerNameVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.PointVariable;
import pl.betoncraft.betonquest.v1_8_R3.variables.VersionVariable;

import java.sql.Connection;

/**
 * Represents BetonQuest plugin
 * 
 * @author Jakub Sapalski
 */
public class BetonQuest extends pl.betoncraft.betonquest.BetonQuest {

	@Override
	public void onEnable() {

		// initialize debugger
		new Debug();

		// load configuration
		new Config();

		// try to connect to database
		Debug.info("Connecting to MySQL database");
		this.database = new MySQL(getPlugin(), getConfig().getString("mysql.host"), getConfig().getString("mysql.port"),
				getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
				getConfig().getString("mysql.pass"));

		// try to connect to MySQL
		Connection con = database.getConnection();
		if (con != null) {
			Debug.broadcast("Using MySQL for storing data!");
			isMySQLUsed = true;
			// if it fails use SQLite
		} else {
			this.database = new SQLite(getPlugin(), "database.db");
			Debug.broadcast("Using SQLite for storing data!");
			isMySQLUsed = false;
		}

		// create tables in the database
		database.createTables(isMySQLUsed);

		// create and start the saver object, which handles correct asynchronous
		// saving to the database
		saver = new Saver();
		saver.start();

		// load database backup
		Utils.loadDatabaseFromBackup();

		// update configuration if needed
		new ConfigUpdater();

		// if it's a first start of the plugin, debug option is not there
		// add it so debug option is turned off after first start
		if (getConfig().getString("debug", null) == null) {
			getConfig().set("debug", "false");
			saveConfig();
		}

		// instantiating of these important things
		new JoinQuitListener();

		// instantiate default conversation start listener
		new CubeNPCListener();

		// instantiate journal handler
		new QuestItemHandler();

		// initialize static events
		new StaticEvents();

		//initialize global objectives
		new GlobalObjectives();

		// initialize combat tagging
		new CombatTagger();

		// load colors for conversations
		new ConversationColors();

		// start mob kill listener
		new MobKillListener();

		// start custom drop listener
		new CustomDropListener();

		// register commands
		new QuestCommand();
		new JournalCommand();
		new BackpackCommand();
		new CancelQuestCommand();
		new CompassCommand();
		new LangCommand();

		// register conditions
		registerConditions("health", HealthCondition.class);
		registerConditions("permission", PermissionCondition.class);
		registerConditions("experience", ExperienceCondition.class);
		registerConditions("tag", TagCondition.class);
		registerConditions("globaltag", GlobalTagCondition.class);
		registerConditions("point", PointCondition.class);
		registerConditions("globalpoint", GlobalPointCondition.class);
		registerConditions("and", ConjunctionCondition.class);
		registerConditions("or", AlternativeCondition.class);
		registerConditions("time", TimeCondition.class);
		registerConditions("weather", WeatherCondition.class);
		registerConditions("height", HeightCondition.class);
		registerConditions("item", ItemCondition.class);
		registerConditions("hand", HandCondition.class);
		registerConditions("location", LocationCondition.class);
		registerConditions("armor", ArmorCondition.class);
		registerConditions("effect", EffectCondition.class);
		registerConditions("rating", ArmorRatingCondition.class);
		registerConditions("sneak", SneakCondition.class);
		registerConditions("random", RandomCondition.class);
		registerConditions("journal", JournalCondition.class);
		registerConditions("testforblock", TestForBlockCondition.class);
		registerConditions("empty", EmptySlotsCondition.class);
		registerConditions("party", PartyCondition.class);
		registerConditions("monsters", MonstersCondition.class);
		registerConditions("objective", ObjectiveCondition.class);
		registerConditions("check", CheckCondition.class);
		registerConditions("chestitem", ChestItemCondition.class);
		registerConditions("score", ScoreboardCondition.class);
		registerConditions("riding", VehicleCondition.class);
		registerConditions("world", WorldCondition.class);
		registerConditions("gamemode", GameModeCondition.class);
		registerConditions("achievement", AchievementCondition.class);
		registerConditions("variable", VariableCondition.class);
//		registerConditions("fly", FlyingCondition.class);
		registerConditions("biome", BiomeCondition.class);
		registerConditions("dayofweek", DayOfWeekCondition.class);
		registerConditions("partialdate", PartialDateCondition.class);
		registerConditions("realtime", RealTimeCondition.class);

		// register events
		registerEvents("message", MessageEvent.class);
		registerEvents("objective", ObjectiveEvent.class);
		registerEvents("command", CommandEvent.class);
		registerEvents("tag", TagEvent.class);
		registerEvents("globaltag", GlobalTagEvent.class);
		registerEvents("journal", JournalEvent.class);
		registerEvents("teleport", TeleportEvent.class);
		registerEvents("explosion", ExplosionEvent.class);
		registerEvents("lightning", LightningEvent.class);
		registerEvents("point", PointEvent.class);
		registerEvents("globalpoint", GlobalPointEvent.class);
		registerEvents("give", GiveEvent.class);
		registerEvents("take", TakeEvent.class);
		registerEvents("conversation", ConversationEvent.class);
		registerEvents("kill", KillEvent.class);
		registerEvents("effect", EffectEvent.class);
		registerEvents("spawn", SpawnMobEvent.class);
		registerEvents("killmob", KillMobEvent.class);
		registerEvents("time", TimeEvent.class);
		registerEvents("weather", WeatherEvent.class);
		registerEvents("folder", FolderEvent.class);
		registerEvents("setblock", SetBlockEvent.class);
		registerEvents("damage", DamageEvent.class);
		registerEvents("party", PartyEvent.class);
		registerEvents("clear", ClearEvent.class);
		registerEvents("run", RunEvent.class);
		registerEvents("givejournal", GiveJournalEvent.class);
		registerEvents("sudo", SudoEvent.class);
		registerEvents("opsudo", OpSudoEvent.class);
		registerEvents("chestgive", ChestGiveEvent.class);
		registerEvents("chesttake", ChestTakeEvent.class);
		registerEvents("chestclear", ChestClearEvent.class);
		registerEvents("compass", CompassEvent.class);
		registerEvents("cancel", CancelEvent.class);
		registerEvents("score", ScoreboardEvent.class);
		registerEvents("lever", LeverEvent.class);
		registerEvents("door", DoorEvent.class);
		registerEvents("if", IfElseEvent.class);
		registerEvents("variable", VariableEvent.class);
		registerEvents("title", TitleEvent.class);
		registerEvents("language", LanguageEvent.class);
		registerEvents("playsound", PlaysoundEvent.class);
		registerEvents("pickrandom", PickRandomEvent.class);
		registerEvents("xp", EXPEvent.class);

		// register objectives
		registerObjectives("location", LocationObjective.class);
		registerObjectives("block", BlockObjective.class);
		registerObjectives("mobkill", MobKillObjective.class);
		registerObjectives("action", ActionObjective.class);
		registerObjectives("die", DieObjective.class);
		registerObjectives("craft", CraftingObjective.class);
		registerObjectives("smelt", SmeltingObjective.class);
		registerObjectives("tame", TameObjective.class);
		registerObjectives("delay", DelayObjective.class);
		registerObjectives("arrow", ArrowShootObjective.class);
		registerObjectives("experience", ExperienceObjective.class);
		registerObjectives("step", StepObjective.class);
		registerObjectives("logout", LogoutObjective.class);
		registerObjectives("password", PasswordObjective.class);
		registerObjectives("fish", FishObjective.class);
		registerObjectives("enchant", EnchantObjective.class);
		registerObjectives("shear", ShearObjective.class);
		registerObjectives("chestput", ChestPutObjective.class);
		registerObjectives("potion", PotionObjective.class);
		registerObjectives("vehicle", VehicleObjective.class);
		registerObjectives("consume", ConsumeObjective.class);
		registerObjectives("variable", VariableObjective.class);
		registerObjectives("kill", KillPlayerObjective.class);
//		registerObjectives("breed", BreedObjective.class);
		registerObjectives("interact", EntityInteractObjective.class);
		registerObjectives("respawn", RespawnObjective.class);

		// register conversation IO types
		registerConversationIO("simple", SimpleConvIO.class);
		registerConversationIO("tellraw", TellrawConvIO.class);
		registerConversationIO("chest", InventoryConvIO.class);
		registerConversationIO("combined", InventoryConvIO.Combined.class);

		// register variable types
		registerVariable("player", PlayerNameVariable.class);
		registerVariable("npc", NpcNameVariable.class);
		registerVariable("objective", ObjectivePropertyVariable.class);
		registerVariable("point", PointVariable.class);
		registerVariable("globalpoint", GlobalPointVariable.class);
		registerVariable("item", ItemAmountVariable.class);
		registerVariable("version", VersionVariable.class);
		registerVariable("location", LocationVariable.class);
		registerVariable("math", MathVariable.class);

        // initialize compatibility with other plugins
        new Compatibility();

		// schedule quest data loading on the first tick, so all other
		// plugins can register their types
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
			public void run() {
				// Load all events and conditions
				loadData();
				// Load global tags and points
				globalData = new GlobalData();
				// load data for all online players
				for (Player player : Bukkit.getOnlinePlayers()) {
					String playerID = PlayerConverter.getID(player);
					PlayerData playerData = new PlayerData(playerID);
					playerDataMap.put(playerID, playerData);
					playerData.startObjectives();
					playerData.getJournal().update();
					if (playerData.getConversation() != null)
						new ConversationResumer(playerID, playerData.getConversation());
				}
			}
		});

		// block betonquestanswer logging (it's just a spam)
		try {
			Class.forName("org.apache.logging.log4j.core.Filter");
			Logger coreLogger = (Logger) LogManager.getRootLogger();
			coreLogger.addFilter(new AnswerFilter());
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			Debug.info("Could not disable /betonquestanswer logging");
		}

		// metrics
		new Metrics(getPlugin());

		// updater
		updater = new Updater(this.getFile());

		// done
		Debug.broadcast("BetonQuest succesfully enabled!");
	}
}

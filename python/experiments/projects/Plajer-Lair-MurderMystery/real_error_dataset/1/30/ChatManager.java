/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.murdermystery.handlers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.utils.MessageUtils;
import pl.plajer.murdermystery.utils.services.exception.ReportedException;
import pl.plajerlair.commonsbox.string.StringFormatUtils;

/**
 * @author Plajer
 * <p>
 * Created at 03.08.2018
 */
@Deprecated //remove static usage
public class ChatManager {

  public static String PLUGIN_PREFIX;

  public ChatManager(String prefix) {
    PLUGIN_PREFIX = prefix;
  }

  public static String colorRawMessage(String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public static String colorMessage(String message) {
      return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
  }

  public static void broadcast(Arena arena, String message) {
    for (Player p : arena.getPlayers()) {
      p.sendMessage(PLUGIN_PREFIX + message);
    }
  }

  public static String formatMessage(Arena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%NUMBER%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public static String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  private static String formatPlaceholders(String message, Arena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public static void broadcastAction(Arena a, Player p, ActionType action) {
    String message;
    switch (action) {
      case JOIN:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Join"), p);
        break;
      case LEAVE:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Leave"), p);
        break;
      case DEATH:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Death"), p);
        break;
      default:
        return; //likely won't ever happen
    }
    for (Player player : a.getPlayers()) {
      player.sendMessage(PLUGIN_PREFIX + message);
    }
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}

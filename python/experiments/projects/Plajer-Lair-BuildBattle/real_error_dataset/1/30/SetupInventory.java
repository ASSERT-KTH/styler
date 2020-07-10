/*
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and contributors
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

package pl.plajer.buildbattle.handlers.setup;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.buildbattle.ConfigPreferences;
import pl.plajer.buildbattle.Main;
import pl.plajer.buildbattle.arena.impl.BaseArena;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.ItemBuilder;
import pl.plajerlair.core.utils.LocationUtils;
import pl.plajerlair.core.utils.XMaterial;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plajer.xyz";
  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private Inventory inventory;

  public SetupInventory(BaseArena arena) {
    this.inventory = Bukkit.createInventory(null, 9 * 2, "BB Arena: " + arena.getID());

    inventory.setItem(ClickPosition.SET_ENDING.getPosition(), new ItemBuilder(new ItemStack(Material.REDSTONE_BLOCK))
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " ending " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be teleported")
        .lore(ChatColor.DARK_GRAY + "after the game)")
        .lore(isOptionDoneBool("instances." + arena.getID() + ".Endlocation"))
        .build());
    inventory.setItem(ClickPosition.SET_LOBBY.getPosition(), new ItemBuilder(new ItemStack(Material.LAPIS_BLOCK))
        .name(ChatColor.GOLD + "► Set" + ChatColor.WHITE + " lobby " + ChatColor.GOLD + "location")
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore(isOptionDoneBool("instances." + arena.getID() + ".lobbylocation"))
        .build());

    int min = ConfigUtils.getConfig(plugin, "arenas").getInt("instances." + arena.getID() + ".minimumplayers");
    if (min == 0) {
      min = 1;
    }
    inventory.setItem(ClickPosition.SET_MINIMUM_PLAYERS.getPosition(), new ItemBuilder(new ItemStack(Material.COAL, min))
        .name(ChatColor.GOLD + "► Set" + ChatColor.DARK_GREEN + " minimum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players are needed")
        .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
        .lore(ChatColor.RED + "Set it minimum 3 when using TEAM game type!!!")
        .lore(isOptionDone("instances." + arena.getID() + ".minimumplayers"))
        .build());
    int max = ConfigUtils.getConfig(plugin, "arenas").getInt("instances." + arena.getID() + ".maximumplayers");
    if (max == 0) {
      max = 1;
    }
    inventory.setItem(ClickPosition.SET_MAXIMUM_PLAYERS.getPosition(), new ItemBuilder(new ItemStack(Material.REDSTONE, max))
        .name(ChatColor.GOLD + "► Set" + ChatColor.GREEN + " maximum players " + ChatColor.GOLD + "size")
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore(isOptionDone("instances." + arena.getID() + ".maximumplayers"))
        .build());

    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      inventory.setItem(ClickPosition.ADD_SIGN.getPosition(), new ItemBuilder(new ItemStack(Material.SIGN))
          .name(ChatColor.GOLD + "► Add game" + ChatColor.AQUA + " sign")
          .lore(ChatColor.GRAY + "Target a sign and click this.")
          .lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)")
          .build());
    }

    inventory.setItem(ClickPosition.SET_GAME_TYPE.getPosition(), new ItemBuilder(XMaterial.CLOCK.parseItem())
        .name(ChatColor.GOLD + "► Set game type")
        .lore(ChatColor.GRAY + "Set game mode of build battle arena.")
        .lore(ChatColor.GRAY + "Valid types: SOLO, TEAM")
        .lore(ChatColor.GRAY + "SOLO - 1 player per plot")
        .lore(ChatColor.GRAY + "TEAM - 2 players per plot")
        .lore(isOptionDone("instances." + arena.getID() + ".gametype"))
        .build());
    inventory.setItem(ClickPosition.SET_MAP_NAME.getPosition(), new ItemBuilder(new ItemStack(Material.NAME_TAG))
        .name(ChatColor.GOLD + "► Set" + ChatColor.RED + " map name " + ChatColor.GOLD + "(currently: " + arena.getMapName() + ")")
        .lore(ChatColor.GRAY + "Replace this name tag with named name tag.")
        .lore(ChatColor.GRAY + "It will be set as arena name.")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "Drop name tag here don't move")
        .lore(ChatColor.RED + "" + ChatColor.BOLD + "it and replace with new!!!")
        .build());
    inventory.setItem(ClickPosition.ADD_GAME_PLOT.getPosition(), new ItemBuilder(new ItemStack(Material.BARRIER))
        .name(ChatColor.GOLD + "► Add game plot")
        .lore(ChatColor.GRAY + "Select your plot with our built-in")
        .lore(ChatColor.GRAY + "selector (select minimum and maximum")
        .lore(ChatColor.GRAY + "plot opposite selections with built-in wand)")
        .lore(ChatColor.GRAY + "And click this.")
        .lore(ChatColor.GRAY + "Command for wand is: " + ChatColor.YELLOW + "/bba plotwand")
        .lore(ChatColor.GREEN + "PLEASE SELECT FLOOR TOO!")
        .lore(isOptionDoneList("instances." + arena.getID() + ".plots"))
        .build());
    inventory.setItem(ClickPosition.ADD_FLOOR_CHANGER_NPC.getPosition(), new ItemBuilder(new ItemStack(Material.GRASS))
        .name(ChatColor.GOLD + "► Add floor changer NPC")
        .lore(ChatColor.GRAY + "Add floor changer NPC to your plot.")
        .lore(ChatColor.RED + "Requires Citizens plugin!")
        .build());
    inventory.setItem(ClickPosition.REGISTER_ARENA.getPosition(), new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
        .name(ChatColor.GOLD + "► " + ChatColor.GREEN + "Register arena")
        .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
        .lore(ChatColor.GRAY + "It will validate and register arena.")
        .build());

    inventory.setItem(17, new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(ChatColor.GOLD + "► View setup video")
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build());
  }

  private static String isOptionDone(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: " + config.getString(path) + ")";

  }

  public static void sendProTip(Player p) {
    int rand = new Random().nextInt(5 + 1);
    switch (rand) {
      case 0:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plajer-Lair/BuildBattle"));
        break;
      case 1:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plajer.xyz"));
        break;
      case 2:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Download some free maps! Get them here: https://wiki.plajer.xyz/minecraft/buildbattle/free_maps.php"));
        break;
      case 3:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7You can use PlaceholderAPI placeholders from our plugin! Check: https://wiki.plajer.xyz/minecraft/buildbattle/papi_placeholders.php"));
        break;
      case 4:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://uservoice.plajer.xyz/index.php?id=BuildBattle"));
        break;
      case 5:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Console can execute /bba votes <add/set> [amount] (player) command! Add super votes via console!"));
        break;
      default:
        break;
    }
  }

  private String isOptionDoneList(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes " + ChatColor.GRAY + "(value: " +
        config.getConfigurationSection(path).getKeys(false).size() + ")";
  }

  private String isOptionDoneBool(String path) {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    if (!config.isSet(path)) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    if (Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationUtils.getLocation(config.getString(path)))) {
      return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.RED + "No";
    }
    return ChatColor.GOLD + "" + ChatColor.BOLD + "Done: " + ChatColor.GREEN + "Yes";
  }

  public void addItem(ItemStack itemStack) {
    inventory.addItem(itemStack);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void openInventory(Player player) {
    player.openInventory(inventory);
  }

  public enum ClickPosition {
    SET_ENDING(0), SET_LOBBY(1), SET_MINIMUM_PLAYERS(2), SET_MAXIMUM_PLAYERS(3), ADD_SIGN(4), SET_GAME_TYPE(5), SET_MAP_NAME(6),
    ADD_GAME_PLOT(7), ADD_FLOOR_CHANGER_NPC(8), REGISTER_ARENA(9), VIEW_SETUP_VIDEO(17);

    private int position;

    ClickPosition(int position) {
      this.position = position;
    }

    public static ClickPosition getByPosition(int pos) {
      for (ClickPosition position : ClickPosition.values()) {
        if (position.getPosition() == pos) {
          return position;
        }
      }
      //couldn't find position, return tutorial
      return ClickPosition.VIEW_SETUP_VIDEO;
    }

    /**
     * @return gets position of item in inventory
     */
    public int getPosition() {
      return position;
    }
  }

}

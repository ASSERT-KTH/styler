/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2020  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package pl.plajer.murdermystery.utils;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.util.Vector;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajerlair.commonsbox.string.StringFormatUtils;

/**
 * @author Plajer
 * <p>
 * Created at 06.10.2018
 */
public class Utils {

  private static Main plugin;

  private Utils() {
  }

  public static void init(Main plugin) {
    Utils.plugin = plugin;
  }

  /**
   * Serialize int to use it in Inventories size
   * ex. you have 38 kits and it will serialize it to 45 (9*5)
   * because it is valid inventory size
   * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public static int serializeInt(Integer i) {
    if ((i % 9) == 0) {
      return i;
    } else {
      return (int) ((Math.ceil(i / 9) * 9) + 9);
    }
  }

  /**
   * Checks whether itemstack is named (not null, has meta and display name)
   *
   * @param stack item stack to check
   * @return true if named, false otherwise
   */
  public static boolean isNamed(ItemStack stack) {
    if (stack == null) {
      return false;
    }
    return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
  }

  public static void applyActionBarCooldown(Player p, int seconds) {
    new BukkitRunnable() {
      int ticks = 0;

      @Override
      public void run() {
        if (!ArenaRegistry.isInArena(p) || ArenaRegistry.getArena(p).getArenaState() != ArenaState.IN_GAME) {
          this.cancel();
        }
        if (ticks >= seconds * 20) {
          this.cancel();
        }
        String progress = StringFormatUtils.getProgressBar(ticks, seconds * 20, 10, "■", ChatColor.COLOR_CHAR + "a", ChatColor.COLOR_CHAR + "c");
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatManager.colorMessage("In-Game.Cooldown-Format", p)
          .replace("%progress%", progress).replace("%time%", String.valueOf((double) ((seconds * 20) - ticks) / 20))));
        ticks += 10;
      }
    }.runTaskTimer(plugin, 0, 10);
  }

  public static List<Block> getNearbyBlocks(Location location, int radius) {
    List<Block> blocks = new ArrayList<>();
    for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
      for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
        for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
          blocks.add(location.getWorld().getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public static Location getBlockCenter(Location location) {
    return location.add(0.5, 0, 0.5);
  }

  public static boolean checkIsInGameInstance(Player player) {
    if (ArenaRegistry.getArena(player) == null) {
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Not-Playing", player));
      return false;
    }
    return true;
  }

  public static boolean hasPermission(CommandSender sender, String perm) {
    if (sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands-No-Permission"));
    return false;
  }


  public static Vector rotateAroundAxisX(Vector v, double angle) {
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double y = v.getY() * cos - v.getZ() * sin;
    double z = v.getY() * sin + v.getZ() * cos;
    return v.setY(y).setZ(z);
  }

  public static Vector rotateAroundAxisY(Vector v, double angle) {
    angle = -angle;
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double x = v.getX() * cos + v.getZ() * sin;
    double z = v.getX() * -sin + v.getZ() * cos;
    return v.setX(x).setZ(z);
  }

}

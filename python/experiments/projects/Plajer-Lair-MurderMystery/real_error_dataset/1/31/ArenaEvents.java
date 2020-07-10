/*
 * MurderMystery - Find the murderer, kill him and survive!
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

package pl.plajer.murdermystery.arena;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.items.SpecialItemManager;
import pl.plajer.murdermystery.handlers.rewards.Reward;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.ItemPosition;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
public class ArenaEvents implements Listener {

  private Main plugin;

  public ArenaEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onArmorStandEject(EntityDismountEvent e) {
    if (!(e.getEntity() instanceof ArmorStand) || e.getEntity().getCustomName() == null
      || !e.getEntity().getCustomName().equals("MurderMysteryArmorStand")) {
      return;
    }
    if (!(e.getDismounted() instanceof Player)) {
      return;
    }
    if (e.getDismounted().isDead()) {
      e.getEntity().remove();
    }
    //we could use setCancelled here but for 1.12 support we cannot (no api)
    e.getDismounted().addPassenger(e.getEntity());
  }

  @EventHandler
  public void onFallDamage(EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    Player victim = (Player) e.getEntity();
    Arena arena = ArenaRegistry.getArena(victim);
    if (arena == null) {
      return;
    }
    boolean killed = false;
    if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
      if (e.getDamage() >= 20.0) {
        //kill the player for suicidal death, else do not
        victim.damage(1000.0);
        killed = true;
      }
      e.setCancelled(true);
    }
    //kill the player and move to the spawn point
    if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
      victim.damage(1000.0);
      victim.teleport(arena.getPlayerSpawnPoints().get(0));
      killed = true;
    }
    if (killed) {
      if (Role.isRole(Role.MURDERER, victim)) {
        ArenaUtils.onMurdererDeath(arena);
      } else if (Role.isRole(Role.ANY_DETECTIVE, victim)) {
        arena.setDetectiveDead(true);
        if (Role.isRole(Role.FAKE_DETECTIVE, victim)) {
          arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
        }
        ArenaUtils.dropBowAndAnnounce(arena, victim);
      }
    }
  }

  @EventHandler
  public void onBowShot(EntityShootBowEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    if (!Role.isRole(Role.ANY_DETECTIVE, (Player) e.getEntity())) {
      return;
    }
    User user = plugin.getUserManager().getUser((Player) e.getEntity());
    if (user.getCooldown("bow_shot") == 0) {
      user.setCooldown("bow_shot", 5);
      Player player = (Player) e.getEntity();
      Utils.applyActionBarCooldown(player, 5);
      e.getBow().setDurability((short) 0);
    } else {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onArrowPickup(PlayerPickupArrowEvent e) {
    if (ArenaRegistry.isInArena(e.getPlayer())) {
      e.getItem().remove();
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemPickup(PlayerPickupItemEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    e.setCancelled(true);
    if (e.getItem().getItemStack().getType() != Material.GOLD_INGOT) {
      return;
    }
    User user = plugin.getUserManager().getUser(e.getPlayer());
    if (user.isSpectator() || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if (user.getStat(StatsStorage.StatisticType.LOCAL_CURRENT_PRAY) == /* magic number */ 3) {
      e.setCancelled(true);
      return;
    }
    e.getItem().remove();
    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
    arena.getGoldSpawned().remove(e.getItem());
    ItemStack stack = new ItemStack(Material.GOLD_INGOT, e.getItem().getItemStack().getAmount());
    if (user.getStat(StatsStorage.StatisticType.LOCAL_CURRENT_PRAY) == /* magic number */ 4) {
      stack.setAmount(3 * e.getItem().getItemStack().getAmount());
    }
    ItemPosition.addItem(e.getPlayer(), ItemPosition.GOLD_INGOTS, stack);
    user.addStat(StatsStorage.StatisticType.LOCAL_GOLD, e.getItem().getItemStack().getAmount());
    ArenaUtils.addScore(user, ArenaUtils.ScoreAction.GOLD_PICKUP, e.getItem().getItemStack().getAmount());
    e.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Messages.Picked-Up-Gold"));

    if (Role.isRole(Role.ANY_DETECTIVE, e.getPlayer())) {
      return;
    }

    if (user.getStat(StatsStorage.StatisticType.LOCAL_GOLD) >= 10) {
      user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
      e.getPlayer().sendTitle(ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-For-Gold"),
          ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-Subtitle"), 5, 40, 5);
      ItemPosition.setItem(e.getPlayer(), ItemPosition.BOW, new ItemStack(Material.BOW, 1));
      ItemPosition.addItem(e.getPlayer(), ItemPosition.ARROWS, new ItemStack(Material.ARROW, 1));
      e.getPlayer().getInventory().setItem(/* same for all roles */ ItemPosition.GOLD_INGOTS.getOtherRolesItemPosition(), new ItemStack(Material.GOLD_INGOT, 0));
    }
  }

  @EventHandler
  public void onMurdererDamage(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
      return;
    }
    Player attacker = (Player) e.getDamager();
    Player victim = (Player) e.getEntity();
    if (!ArenaUtils.areInSameArena(attacker, victim)) {
      return;
    }
    //we are killing player via damage() method so event can be cancelled safely, will work for detective damage murderer and others
    e.setCancelled(true);

    //better check this for future even if anyone else cannot use sword
    if (!Role.isRole(Role.MURDERER, attacker)) {
      return;
    }

    //todo support for skins later
    //just don't kill user if item isn't murderer sword
    if (attacker.getInventory().getItemInMainHand().getType() != Material.IRON_SWORD) {
      return;
    }

    if (Role.isRole(Role.MURDERER, victim)) {
      plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.MURDERER_KILL);
    } else if (Role.isRole(Role.ANY_DETECTIVE, victim)) {
      plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.DETECTIVE_KILL);
    }

    victim.damage(100.0);
    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 50, 1);
    User user = plugin.getUserManager().getUser(attacker);

    user.addStat(StatsStorage.StatisticType.KILLS, 1);
    user.addStat(StatsStorage.StatisticType.LOCAL_KILLS, 1);
    ArenaUtils.addScore(user, ArenaUtils.ScoreAction.KILL_PLAYER, 0);

    Arena arena = ArenaRegistry.getArena(attacker);
    if (Role.isRole(Role.ANY_DETECTIVE, victim)) {
      //if already true, no effect is done :)
      arena.setDetectiveDead(true);
      if (Role.isRole(Role.FAKE_DETECTIVE, victim)) {
        arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
      }
      ArenaUtils.dropBowAndAnnounce(arena, victim);
    }
  }

  @EventHandler
  public void onArrowDamage(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player)) {
      return;
    }
    if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
      return;
    }
    Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
    Player victim = (Player) e.getEntity();
    if (!ArenaUtils.areInSameArena(attacker, victim)) {
      return;
    }
    //we won't allow to suicide
    if (attacker.equals(victim)) {
      e.setCancelled(true);
      return;
    }

    victim.damage(100.0);
    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 50, 1);
    User user = plugin.getUserManager().getUser(attacker);

    user.addStat(StatsStorage.StatisticType.KILLS, 1);
    if (Role.isRole(Role.MURDERER, attacker)) {
      user.addStat(StatsStorage.StatisticType.LOCAL_KILLS, 1);
      ArenaUtils.addScore(user, ArenaUtils.ScoreAction.KILL_PLAYER, 0);
    }

    Arena arena = ArenaRegistry.getArena(attacker);
    victim.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Died"), null, 5, 40, 50);

    if (Role.isRole(Role.MURDERER, victim)) {
      arena.setCharacter(Arena.CharacterType.HERO, attacker);
      ArenaUtils.onMurdererDeath(arena);
      ArenaUtils.addScore(plugin.getUserManager().getUser(attacker), ArenaUtils.ScoreAction.KILL_MURDERER, 0);
    } else if (Role.isRole(Role.ANY_DETECTIVE, victim)) {
      ArenaUtils.dropBowAndAnnounce(arena, victim);
    } else if (Role.isRole(Role.INNOCENT, victim)) {
      if (Role.isRole(Role.MURDERER, attacker)) {
        victim.sendTitle(null, ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Murderer-Killed-You"), 5, 40, 5);
      } else {
        victim.sendTitle(null, ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Player-Killed-You"), 5, 40, 5);
      }

      //if else, murderer killed, so don't kill him :)
      if (Role.isRole(Role.ANY_DETECTIVE, attacker) || Role.isRole(Role.INNOCENT, attacker)) {
        attacker.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Died"),
            ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Killed-Innocent"), 5, 40, 5);
        attacker.damage(100.0);
        ArenaUtils.addScore(plugin.getUserManager().getUser(attacker), ArenaUtils.ScoreAction.INNOCENT_KILL, 0);
        plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.DETECTIVE_KILL);

        if (Role.isRole(Role.ANY_DETECTIVE, attacker)) {
          arena.setDetectiveDead(true);
          if (Role.isRole(Role.FAKE_DETECTIVE, attacker)) {
            arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
          }
          ArenaUtils.dropBowAndAnnounce(arena, victim);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDie(PlayerDeathEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getEntity());
    if (arena == null) {
      return;
    }
    Location loc = e.getEntity().getLocation();
    e.setDeathMessage("");
    e.getDrops().clear();
    e.setDroppedExp(0);
    plugin.getCorpseHandler().spawnCorpse(e.getEntity(), arena);
    e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
    Player player = e.getEntity();
    if (arena.getArenaState() == ArenaState.STARTING) {
      player.teleport(loc);
      return;
    } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
      player.getInventory().clear();
      player.setFlying(false);
      player.setAllowFlight(false);
      User user = plugin.getUserManager().getUser(player);
      user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
      player.teleport(arena.getEndLocation());
      return;
    }
    User user = plugin.getUserManager().getUser(player);
    user.addStat(StatsStorage.StatisticType.DEATHS, 1);
    player.teleport(loc);
    user.setSpectator(true);
    player.setGameMode(GameMode.SURVIVAL);
    user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
    ArenaUtils.hidePlayer(player, arena);
    player.setAllowFlight(true);
    player.setFlying(true);
    player.getInventory().clear();
    ChatManager.broadcastAction(arena, player, ChatManager.ActionType.DEATH);

    //we must call it tick/two later due to instant respawn bug
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      e.getEntity().spigot().respawn();
      player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name")).build());
      player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name")).build());
      player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());
    }, 2);
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    if (arena.getPlayers().contains(e.getPlayer())) {
      Player player = e.getPlayer();
      User user = plugin.getUserManager().getUser(player);
      player.setAllowFlight(true);
      player.setFlying(true);
      user.setSpectator(true);
      player.setGameMode(GameMode.SURVIVAL);
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
    }
  }

  @EventHandler
  public void onItemMove(InventoryClickEvent e) {
    if (e.getWhoClicked() instanceof Player) {
      if (ArenaRegistry.getArena((Player) e.getWhoClicked()) != null) {
        e.setResult(Event.Result.DENY);
      }
    }
  }

}

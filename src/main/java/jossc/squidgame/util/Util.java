package jossc.squidgame.util;

import cn.nukkit.Player;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

public class Util {

  public static String SCOREBOARD_TITLE =
    TextFormat.BOLD.toString() + TextFormat.YELLOW + "SQUID GAME";

  public static String formatTime(int time) {
    int minutes = Math.floorDiv(time, 60);
    int seconds = (int) Math.floor(time % 60);
    return (
      (minutes < 10 ? "0" : "") +
      minutes +
      ":" +
      (seconds < 10 ? "0" : "") +
      seconds
    );
  }

  public static void clearAllPlayerArmorInventory(Player player) {
    Item air = Item.get(Item.AIR);

    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(air);
    inventory.setChestplate(air);
    inventory.setLeggings(air);
    inventory.setBoots(air);

    updatePlayerInventory(player);
  }

  public static void clearAllPlayerInventory(Player player) {
    player.getInventory().clearAll();
    clearAllPlayerArmorInventory(player);
    updatePlayerInventory(player);
  }

  public static void updatePlayerInventory(Player player) {
    PlayerInventory inventory = player.getInventory();
    inventory.sendArmorContents(player);
    inventory.sendContents(player);
    inventory.sendHeldItem(player);
  }

  public static void convertSpectator(Player player) {
    convertSpectator(player, false);
  }

  public static void convertSpectator(Player player, boolean teleport) {
    setDefaultAttributes(player, Player.SPECTATOR, teleport);
  }

  public static void setDefaultAttributes(Player player) {
    setDefaultAttributes(player, Player.ADVENTURE, false);
  }

  public static void setDefaultAttributes(
    Player player,
    int gamemode,
    boolean teleport
  ) {
    player.setGamemode(gamemode);
    player.setHealth(20);
    player.setMaxHealth(20);
    player.setOnFire(0);
    player.extinguish();
    player.setFoodEnabled(false);
    player.getFoodData().setLevel(20);
    player.setExperience(0);
    player.setImmobile(false);
    player.sendExperienceLevel(0);
    player.getCraftingGrid().clearAll();
    player.getCursorInventory().clearAll();
    player.getUIInventory().clearAll();
    player.getOffhandInventory().clearAll();
    player.getEnderChestInventory().clearAll();
    player.removeAllEffects();
    clearAllPlayerInventory(player);
    updatePlayerInventory(player);

    if (teleport) {
      player.teleport(
        player
          .getServer()
          .getDefaultLevel()
          .getSafeSpawn()
          .asVector3f()
          .asVector3()
          .add(0, 1)
      );
    }
  }
}

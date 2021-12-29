package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.ItemBootsLeather;
import cn.nukkit.item.ItemChestplateLeather;
import cn.nukkit.item.ItemLeggingsLeather;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.TimeUtils;

public abstract class Microgame extends GamePhase {

  protected int startCountdown = 11;

  protected List<Player> roundWinners = new ArrayList<>();

  @Setter
  protected net.josscoder.gameapi.map.Map map = null;

  public Microgame(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  protected void onStart() {
    getNeutralPlayers().forEach(Entity::setImmobile);
  }

  @Override
  public void onUpdate() {
    if (startCountdown >= 0) {
      broadcastActionBar("&bAlive players &l" + countNeutralPlayers());

      startCountdown--;

      if (startCountdown == 10 || startCountdown <= 5 && startCountdown > 0) {
        broadcastMessage(
          "&b&l» &r&fThis microgame will starts in &b" + startCountdown + "&f!"
        );
        broadcastSound("note.bassattack", 2, 2);

        if (startCountdown == 5 && map != null) {
          getOnlinePlayers().forEach(player -> map.teleportToSafeSpawn(player));
        }

        return;
      }

      if (startCountdown == 0) {
        String instruction = getInstruction();

        if (instruction.isEmpty()) {
          return;
        }

        broadcastMessage("&7" + getName() + " &e&l» &r&f" + instruction);

        schedule(
          () -> {
            getNeutralPlayers()
              .forEach(
                player -> {
                  player.setImmobile(false);
                  giveArmor(player);
                }
              );
            onGameStart();
          },
          20 * 6
        );
      }

      return;
    }

    broadcastActionBar(
      "&bThis microgame ends in &l" +
      TimeUtils.timeToString((int) getRemainingDuration().getSeconds())
    );
    onGameUpdate();
  }

  public List<Player> getRoundLosers() {
    List<Player> losers = new ArrayList<>();

    getNeutralPlayers()
      .forEach(
        player -> {
          if (!roundWinners.contains(player)) {
            losers.add(player);
          }
        }
      );

    return losers;
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || countNeutralPlayers() <= 1;
  }

  @Override
  protected void onEnd() {
    if (countNeutralPlayers() <= 1) {
      if (countNeutralPlayers() == 1) {
        Player winner = getNeutralPlayers().get(0);

        broadcastMessage(
          "&d&l» &r&fGood news... " +
          winner.getName() +
          " is the winner, congrats!"
        );

        Map<Player, Integer> pedestalWinners = new HashMap<>();
        pedestalWinners.put(winner, 1);

        game.end(pedestalWinners);
      } else {
        broadcastMessage(
          "&c&l» &r&cBad news... There were no winners in this game!"
        );

        game.end(null);
      }

      return;
    }

    getRoundLosers().forEach(this::lose);

    roundWinners.clear();

    game.schedule(this::onGameEnd, 20 * 4);
  }

  public abstract String getName();

  public abstract String getInstruction();

  public abstract void onGameStart();

  public abstract void onGameUpdate();

  public abstract void onGameEnd();

  public void win(Player player) {
    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    user.giveDefaultAttributes();

    player.sendTitle(
      TextFormat.colorize("&bYou won this game!"),
      TextFormat.WHITE + "You will continue in the next game."
    );
    playSound(player, "random.levelup", 2, 3);
  }

  public void lose(Player player) {
    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    if (map != null) {
      map.teleportToSafeSpawn(player);
    }

    user.convertSpectator(true, false);
  }

  private void giveArmor(Player player) {
    PlayerInventory inventory = player.getInventory();

    DyeColor color = DyeColor.GREEN;

    ItemChestplateLeather chestPlateLeather = new ItemChestplateLeather();
    chestPlateLeather.setColor(color);

    ItemLeggingsLeather leggingsLeather = new ItemLeggingsLeather();
    leggingsLeather.setColor(color);

    ItemBootsLeather bootsLeather = new ItemBootsLeather();
    bootsLeather.setColor(color);

    inventory.setChestplate(chestPlateLeather);
    inventory.setLeggings(leggingsLeather);
    inventory.setBoots(bootsLeather);

    User user = userFactory.get(player);

    if (user != null) {
      user.updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBurn(BlockBurnEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemDrop(PlayerDropItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamage(EntityDamageEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntitySpawn(CreatureSpawnEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
    event.setCancelled();
  }
}

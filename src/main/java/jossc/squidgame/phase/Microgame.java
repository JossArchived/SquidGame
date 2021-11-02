package jossc.squidgame.phase;

import cn.nukkit.Player;
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
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jossc.game.Game;
import jossc.game.phase.GamePhase;
import jossc.game.phase.lobby.EndGamePhase;
import lombok.Getter;
import lombok.Setter;

public abstract class Microgame extends GamePhase {

  protected int countdown = 11;

  protected List<Player> roundWinners = new ArrayList<>();

  @Getter
  @Setter
  protected Position gamePosition;

  public Microgame(Game game) {
    super(game);
  }

  public Microgame(Game game, Duration duration) {
    super(game, duration);
  }

  public abstract String getName();

  public abstract String getInstruction();

  @Override
  protected void onStart() {}

  private void endGameByNotHavePlayers() {
    game
      .getPhaseSeries()
      .addNext(
        new EndGamePhase(
          game,
          (spectatorsSize() >= 1 ? Duration.ofSeconds(5) : Duration.ZERO),
          null
        )
      );
    end();
  }

  private void endGameByHaveTheLastPlayer() {
    Map<Player, Integer> winners = new HashMap<>();

    getNeutralPlayers()
      .forEach(
        player -> {
          winners.put(player, 1);

          getPlayers()
            .forEach(
              onlinePlayer ->
                onlinePlayer.sendMessage(
                  TextFormat.colorize(
                    "&d&l» &r&7" + player.getName() + " is the winner!"
                  )
                )
            );
        }
      );

    game
      .getPhaseSeries()
      .addNext(new EndGamePhase(game, Duration.ofSeconds(10), winners));
    end();
  }

  public void giveArmor(Player player) {
    PlayerInventory inventory = player.getInventory();

    DyeColor color = DyeColor.GREEN;

    ItemChestplateLeather chestplateLeather = new ItemChestplateLeather();
    chestplateLeather.setColor(color);

    ItemLeggingsLeather leggingsLeather = new ItemLeggingsLeather();
    leggingsLeather.setColor(color);

    ItemBootsLeather bootsLeather = new ItemBootsLeather();
    bootsLeather.setColor(color);

    inventory.setChestplate(chestplateLeather);
    inventory.setLeggings(leggingsLeather);
    inventory.setBoots(bootsLeather);

    game.updatePlayerInventory(player);
  }

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      if (countdown == 10 || countdown <= 5) {
        if (countdown == 5) {
          getNeutralPlayers().forEach(player -> player.teleport(gamePosition));
        }

        broadcastMessage(
          "&c&l» &r&fThis game will start in &c" +
          countdown +
          "&f second" +
          (countdown == 1 ? "" : "s") +
          "!"
        );
        broadcastSound("note.bassattack", 2, 2);

        return;
      }
    } else if (countdown == 0) {
      String instruction = getInstruction();

      if (instruction.isEmpty()) {
        return;
      }

      broadcastMessage("&7" + getName() + " &e&l» &r&f" + instruction);

      schedule(
        () -> {
          getNeutralPlayers().forEach(this::giveArmor);
          onGameStart();
        },
        20
      );

      return;
    }

    if (neutralPlayersSize() == 1) {
      endGameByHaveTheLastPlayer();
    } else if (neutralPlayersSize() < 1) {
      endGameByNotHavePlayers();
    } else {
      broadcastActionBar(
        "&6This game ends in &l" + getRemainingDuration().getSeconds()
      );
      onGameUpdate();
    }
  }

  public abstract void onGameStart();

  public abstract void onGameUpdate();

  @Override
  protected void onEnd() {
    super.onEnd();
    onGameEnd();
    roundWinners.clear();
    getNeutralPlayers()
      .forEach(
        player ->
          player.teleport(
            Position.fromObject(new Vector3(113, 5, 133), game.getMap())
          )
      );
  }

  public abstract void onGameEnd();

  public List<Player> getRoundLosers() {
    List<Player> losers = new ArrayList<>();

    getNeutralPlayers()
      .forEach(
        player -> {
          if (!roundWinners.contains(player)) {
            roundWinners.add(player);
          }
        }
      );

    return losers;
  }

  public void win(Player player) {
    game.giveDefaultAttributes(player);
    roundWinners.add(player);
    player.sendTitle(
      TextFormat.colorize("&bYou won this game!"),
      TextFormat.WHITE + "You will continue in the next game."
    );
    playSound(player, "random.levelup", 2, 3);
  }

  public void lose(Player player) {
    game.convertSpectator(player, true);
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

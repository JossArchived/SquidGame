package jossc.squidgame.arena.state;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import jossc.game.utils.scoreboard.ScoreboardBuilder;
import jossc.squidgame.arena.Arena;
import jossc.squidgame.util.Util;

public abstract class Microgame extends GameState {

  protected List<Player> roundWinners;
  protected ScoreboardBuilder scoreboard = new ScoreboardBuilder();

  public Microgame(PluginBase plugin) {
    this(plugin, null);
  }

  public Microgame(PluginBase plugin, Arena arena) {
    this(plugin, Duration.ZERO, arena);
  }

  public Microgame(PluginBase plugin, Duration duration, Arena arena) {
    super(plugin, duration, arena);
    roundWinners = new ArrayList<>();
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Util.convertSpectator(event.getPlayer(), true);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    roundWinners.remove(event.getPlayer());
  }

  public abstract String getInstructions();

  @Override
  protected void onStart() {
    getPlayers()
      .forEach(player -> scoreboard.send(player, Util.SCOREBOARD_TITLE));

    onGameStart();

    String instructions = getInstructions();

    if (!instructions.isEmpty()) {
      schedule(
        () ->
          broadcastTitle(
            "",
            TextFormat.YELLOW + instructions,
            0,
            (int) (duration.toMillis() / 1000) * 20,
            0
          ),
        11
      );
    }
  }

  public abstract void onGameStart();

  @Override
  protected void onEnd() {
    onGameEnd();

    getPlayers().forEach(player -> scoreboard.remove(player));

    roundWinners.clear();
  }

  public abstract void onGameEnd();

  private void markAsEnd(boolean withPlayers) {
    arena.getMainState().addNext(new EndGameState(plugin, arena, withPlayers));
    end();
  }

  @Override
  public void onUpdate() {
    if (neutralPlayersSize() == 1) {
      announceLastPlayerStanding();
      markAsEnd(true);

      return;
    }

    if (neutralPlayersSize() <= 0) {
      markAsEnd(spectatorsSize() >= 1);

      return;
    }

    onGameUpdate();
  }

  public abstract void onGameUpdate();

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
    player.sendTitle(
      TextFormat.BOLD.toString() + TextFormat.DARK_GREEN + "YOU WON THIS GAME!",
      TextFormat.GREEN + "You will continue in the next game",
      0,
      (int) (duration.toMillis() / 1000) * 20,
      0
    );
    playSound(player, "random.levelup", 2, 3);
    Util.setDefaultAttributes(player);
    roundWinners.add(player);
  }

  public void lose(Player player) {
    player.sendTitle(
      TextFormat.BOLD.toString() + TextFormat.DARK_RED + "YOU HAVE LOST!",
      TextFormat.RED + "You will not appear again",
      0,
      (int) (duration.toMillis() / 1000) * 20,
      0
    );
    playSound(player, "mob.endermen.death");
    Util.convertSpectator(player);
  }

  public void announceLastPlayerStanding() {
    getNeutralPlayers()
      .forEach(
        player -> {
          player.sendTitle(
            TextFormat.BOLD.toString() + TextFormat.YELLOW + "CONGRATULATIONS!",
            TextFormat.GREEN + "You have won"
          );
          broadcastMessage(
            TextFormat.YELLOW +
            player.getName() +
            TextFormat.GREEN +
            " won the game!"
          );
          playSound(player, "mob.pillager.celebrate", 2, 3);
        }
      );
  }
}

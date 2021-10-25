package jossc.squidgame.state;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.game.state.GameState;
import jossc.squidgame.SquidGame;
import jossc.squidgame.listener.DeathListener;

public abstract class Microgame extends GameState {

  public Microgame(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  public abstract String getInstructions();

  @Override
  protected void onStart() {
    register(new DeathListener());

    if (!getInstructions().isEmpty()) {
      broadcastMessage("                        ");
      broadcastMessage(
        TextFormat.BOLD.toString() + TextFormat.RED + "Instructions"
      );
      broadcastMessage(TextFormat.colorize(getInstructions()));
      broadcastMessage("                        ");
    }

    onGameStart();
  }

  public abstract void onGameStart();

  @Override
  protected void onEnd() {
    onGameEnd();
  }

  public abstract void onGameEnd();

  private void addNextState(GameState state) {
    SquidGame.getPlugin().getMainState().addNext(state);
  }

  @Override
  public void onUpdate() {
    if (neutralPlayersSize() == 1) {
      announceLastPersonStanding();
      addNextState(new CelebrateGameState(plugin));
    } else if (neutralPlayersSize() <= 0) {
      addNextState(new ResetGameState(plugin));
    } else {
      onGameUpdate();
    }
  }

  public abstract void onGameUpdate();

  public void win(Player player) {
    player.sendTitle(
      TextFormat.BOLD.toString() + TextFormat.YELLOW + "YOU WON THIS GAME!",
      TextFormat.GREEN + "You will continue in the next game",
      0,
      (int) (duration.toMillis() / 1000) * 20,
      0
    );
    playSound(player, "mob.pillager.celebrate", 2, 3);
  }

  public void lose(Player player) {
    player.sendTitle(
      TextFormat.BOLD.toString() + TextFormat.RED + "YOU HAVE LOST!",
      TextFormat.DARK_GRAY + "You will not appear again",
      0,
      (int) (duration.toMillis() / 1000) * 20,
      0
    );
    playSound(player, "mob.endermen.death");
    player.setGamemode(Player.SPECTATOR);
  }

  public void announceLastPersonStanding() {
    getNeutralPlayers()
      .forEach(
        player -> {
          player.sendTitle(
            TextFormat.BOLD.toString() + TextFormat.YELLOW + "CONGRATULATIONS!",
            TextFormat.GREEN + "You have won!"
          );
          playSound(player, "random.totem");
          broadcastMessage(player.getName() + " won the game!");
        }
      );
  }

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || neutralPlayersSize() <= 1;
  }
}

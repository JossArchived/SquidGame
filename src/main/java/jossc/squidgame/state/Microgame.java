package jossc.squidgame.state;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.game.state.GameState;

public abstract class Microgame extends GameState {

  public Microgame(PluginBase plugin, Duration duration) {
    super(plugin, duration);
  }

  public abstract String getInstructions();

  @Override
  protected void onStart() {
    if (!getInstructions().isEmpty()) {
      broadcastMessage(
        TextFormat.BOLD.toString() +
        TextFormat.RED +
        "Instructions: " +
        TextFormat.RESET +
        TextFormat.colorize(getInstructions())
      );
    }

    onGameStart();
  }

  public abstract void onGameStart();

  @Override
  protected void onEnd() {
    onGameEnd();
  }

  public abstract void onGameEnd();

  public void win(Player player) {
    player.sendTitle(
      TextFormat.BOLD.toString() + TextFormat.DARK_GREEN + "YOU WON THIS GAME!",
      TextFormat.DARK_GRAY + "You will continue in the next game",
      0,
      (int) (duration.toMillis() / 1000) * 20,
      0
    );
    playSound(player, "mob.pillager.celebrate", 2, 3);
    player.setGamemode(Player.SPECTATOR);
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

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || neutralPlayersSize() < 1;
  }
}

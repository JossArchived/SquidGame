package jossc.squidgame.state;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import jossc.game.state.GameState;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

abstract public class Microgame extends GameState {

  protected final Duration duration;

  public Microgame(PluginBase plugin, Duration duration) {
    super(plugin);

    this.duration = duration;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return duration;
  }

  public abstract String getInstructions();

  @Override
  protected void onStart() {
    broadcastMessage(getInstructions());

    onGameStart();
  }

  public abstract void onGameStart();

  @Override
  protected void onEnd() {
    onGameEnd();
  }

  public abstract void onGameEnd();

  public void win(Player player) {
    
  }

  public void lose(Player player) {

  }
}

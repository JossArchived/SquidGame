package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.game.state.GameState;

public class EndGameState extends GameState {

  public EndGameState(PluginBase plugin, boolean withPlayers) {
    super(plugin, (withPlayers ? Duration.ofSeconds(20) : Duration.ZERO));
  }

  @Override
  protected void onStart() {}

  @Override
  public void onUpdate() {}

  @Override
  protected void onEnd() {}
}

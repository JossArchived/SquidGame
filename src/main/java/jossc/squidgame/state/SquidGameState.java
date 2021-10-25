package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class SquidGameState extends Microgame {

  public SquidGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(3));
  }

  @Override
  public String getInstructions() {
    return "";
  }

  @Override
  public void onGameStart() {}

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {}
}

package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class LightsOffGameState extends Microgame {

  public LightsOffGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(1));
  }

  @Override
  public String getInstructions() {
    return "";
  }

  @Override
  public void onGameStart() {}

  @Override
  public void onUpdate() {}

  @Override
  public void onGameEnd() {}
}

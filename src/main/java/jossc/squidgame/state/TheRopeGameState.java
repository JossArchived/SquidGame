package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class TheRopeGameState extends Microgame {

  public TheRopeGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(3));
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

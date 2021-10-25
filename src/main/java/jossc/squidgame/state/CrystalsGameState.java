package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;

public class CrystalsGameState extends Microgame {

  public CrystalsGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(1));
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

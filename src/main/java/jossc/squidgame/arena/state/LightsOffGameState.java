package jossc.squidgame.arena.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.squidgame.arena.Arena;

public class LightsOffGameState extends Microgame {

  public LightsOffGameState(PluginBase plugin, Arena arena) {
    super(plugin, Duration.ofMinutes(1), arena);
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

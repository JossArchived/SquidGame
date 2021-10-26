package jossc.squidgame.arena.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.squidgame.arena.Arena;

public class DalgonaGameState extends Microgame {

  public DalgonaGameState(PluginBase plugin, Arena arena) {
    super(plugin, Duration.ofMinutes(3), arena);
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
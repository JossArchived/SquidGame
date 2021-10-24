package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;

import java.time.Duration;

public class LightsOff extends Microgame {

  public LightsOff(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(1));
  }

  @Override
  public String getInstructions() {
    return "";
  }

  @Override
  public void onGameStart() {

  }

  @Override
  public void onUpdate() {

  }

  @Override
  public void onGameEnd() {

  }
}

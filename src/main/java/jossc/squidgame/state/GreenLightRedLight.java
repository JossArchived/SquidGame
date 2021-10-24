package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;

import java.time.Duration;

public class GreenLightRedLight extends Microgame {

  public GreenLightRedLight(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(5));
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
